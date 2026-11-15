#!/usr/bin/env bash
set -euo pipefail
# beforeShellExecution: блокирует опасные shell-команды (в т.ч. обходы через
# кавычки, \escape, /path/to/rm, bash/sh -c, VAR=rm; $VAR).
input=$(cat)
python3 - "$input" <<'PY'
import json
import re
import sys

def deny(msg: str) -> None:
    print(json.dumps({
        "permission": "deny",
        "agent_message": msg,
        "user_message": msg,
    }, ensure_ascii=False))
    sys.exit(0)


def allow() -> None:
    print('{ "permission": "allow" }')
    sys.exit(0)


try:
    data = json.loads(sys.argv[1])
except (json.JSONDecodeError, IndexError):
    sys.exit(1)

command = data.get("command") or ""
if not command.strip():
    allow()


def normalize(s: str) -> str:
    """Снять простые обходы: \\rm, \"rm\", 'rm'."""
    s = re.sub(r"\\([A-Za-z])", r"\1", s)
    s = s.replace('"', " ").replace("'", " ")
    s = re.sub(r"[\t\n\r]+", " ", s)
    s = re.sub(r" +", " ", s)
    return s.strip()


norm = normalize(command)

WRAPPERS = re.compile(
    r"^(?:sudo|command|builtin|exec|eval|nice|nohup|time|env|xargs|watch)"
    r"(?:\s+-\S+)*\s+",
    re.IGNORECASE,
)
SHELL_C = re.compile(
    r"^(?:bash|sh|zsh|dash|ksh)\s+-c\s+(.*)$",
    re.IGNORECASE,
)
ASSIGN = re.compile(
    r"^(?:export\s+)?[A-Za-z_][A-Za-z0-9_]*=\S+\s+",
    re.IGNORECASE,
)


def strip_prefixes(part: str) -> str:
    while True:
        m = ASSIGN.match(part)
        if not m:
            break
        part = part[m.end():]
    while True:
        m = WRAPPERS.match(part)
        if not m:
            break
        part = part[m.end():]
    return part


def iter_simple_commands(s: str):
    """Сегменты по ; | & && || — простая модель pipeline/list."""
    for raw in re.split(r"(?:&&|\|\||[;|&])+", s):
        part = raw.strip()
        if not part:
            continue
        part = strip_prefixes(part)
        m = SHELL_C.match(part)
        if m:
            yield from iter_simple_commands(m.group(1).strip())
            continue
        yield part


def basename_token(tok: str) -> str:
    return tok.rsplit("/", 1)[-1].lower()


def first_word(part: str) -> str:
    if not part:
        return ""
    return part.split(" ", 1)[0]


# --- VAR=rm / VAR=/bin/rm затем $VAR / ${VAR} ---
dangerous_bins = ("rm", "unlink")
assigned = set()
for m in re.finditer(
    rf"(?:^|[\s;|&])(?:export\s+)?([A-Za-z_][A-Za-z0-9_]*)=(?:\S*/)?({'|'.join(dangerous_bins)})(?=[\s;|&]|$)",
    norm,
    flags=re.IGNORECASE,
):
    assigned.add(m.group(1))

for var in assigned:
    if re.search(rf"(?:^|[\s;|&])\$\{{{var}\}}(?=[\s;|&]|$)", norm) or re.search(
        rf"(?:^|[\s;|&])\${var}(?=[\s;|&]|$)", norm
    ):
        deny(f"Запрещено: удаление через переменную ${var}.")

# --- $(…) / `…` с rm|unlink ---
if re.search(r"\$\([^)]*\b(?:rm|unlink)\b[^)]*\)", norm, re.I) or re.search(
    r"`[^`]*\b(?:rm|unlink)\b[^`]*`", norm, re.I
):
    deny("Запрещено: rm/unlink через подстановку команды.")

# --- команды в позиции argv0 ---
for part in iter_simple_commands(norm):
    words = part.split()
    if not words:
        continue
    cmd = basename_token(words[0])

    if cmd == "rm":
        deny("Запрещено: rm.")
    if cmd == "unlink":
        deny("Запрещено: unlink.")
    if cmd == "find" and any(w == "-delete" for w in words[1:]):
        deny("Запрещено: find … -delete.")
    if cmd == "git":
        # git [-C dir | -c k=v]* <subcommand>
        i = 1
        while i < len(words):
            if words[i] == "-C" and i + 1 < len(words):
                i += 2
                continue
            if words[i].startswith("-c") and "=" in words[i]:
                i += 1
                continue
            if words[i] == "-c" and i + 1 < len(words):
                i += 2
                continue
            break
        sub = words[i].lower() if i < len(words) else ""
        if sub == "push":
            deny("Запрещено: git push (в т.ч. git -C / git -c … push).")
        if sub == "rm":
            deny("Запрещено: git rm.")

allow()
PY
