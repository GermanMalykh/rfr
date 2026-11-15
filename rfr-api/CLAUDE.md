## Моки vs БД

Профиль, страны, люди и дружба — реальная БД. Лента/`feed` и все операции с фото — мок из `src/main/resources/mock/`: ответ «успешный», в БД ничего не пишется. Таблицы `photo`/`like`/`statistic` в миграции есть, код их не трогает. Моки не выкидывать, пока явно не попросят заменить.

## Ловушки

- API-профиль появляется при первом `Query.user` (страна по умолчанию `ru`).
- Связь auth ↔ api только по `username`; UUID в схемах разные.
- GraphQL-ошибки приходят с HTTP 200, смотри `errors[].extensions.classification`.
- В Bearer нужен access token (`read` для query, `write` для mutation); `id_token` даёт 401.
- Дружба — одна строка на пару: `ADD` → `PENDING`; `ACCEPT`/`REJECT` только у адресата; `DELETE` — любой стороне.
- Каскадов в FK нет: перед удалением пользователя сначала чисти `friendship`.
- Модель из `query.graphqls` пишет DGS Codegen — руками в `build/generated` не править.
