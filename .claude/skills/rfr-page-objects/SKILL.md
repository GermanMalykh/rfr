---
name: rfr-page-objects
description: Создаёт и меняет Page Objects и компоненты UI Rangiffler на Playwright Java в rfr-e2e (пакеты ui/page и ui/component). Применять к страницам, локаторам, действиям и проверкам UI. Фикстуры, SQL и JUnit extensions — вне ответственности страниц.
---

# Page Objects Rangiffler

Страницы — `rfr-e2e/src/test/java/io/student/rangiffler/ui/page`, общие части — `ui/component`, адреса — `config/TestConfig`.
Доступные имена и роли элементов подтверждай в браузере через Playwright MCP (`http://localhost:3001`), не угадывай по исходникам фронта.

## Контракт

1. Класс `final`, поля `private final`: `Page`, `Locator`, компоненты. Исключение — `public static final String URL` с базой из `TestConfig`; он есть у каждой страницы, даже если на неё попадают только переходом.
2. Конструктор принимает только `Page` и только присваивает поля: без переходов, кликов, проверок, ожиданий и SQL.
3. Композиция вместо наследования: общие части (`CredentialsForm`, `HeaderComponent`) — компоненты с тем же `Page`, базовых классов нет.
4. Каждый публичный метод страницы и компонента — с `@Step`, и каждый несекретный параметр попадает в текст шага: `@Step("Заполнить форму входа: {username}")`.
5. Чувствительные данные (пароли, токены, data URL аватара и т.п.) не должны попадать в Allure в открытом виде: не подставляй значение в текст `@Step` (`{password}` запрещён), вместо значения пиши маску `***` (`… / ***`); параметр помечай `@Param(mode = Parameter.Mode.HIDDEN)`. Для паролей не вызывай `Locator.fill(value)` — allure-playwright кладёт value в имя шага; используй `page.fill("input[name=…]", secret)`, тогда value редактируется.
6. Переход возвращает объект следующей страницы, действие и проверка на текущей — `this`.
7. Одно бизнес-действие — один метод, имя которого называет форму: `fillLoginForm(username, password)`, `fillRegistrationForm(...)`. Без безликих `fillForm`/`fill`/`submit` и цепочек `fillUsername().fillPassword()`; отдельный метод на поле — только если тесту нужно именно поле (валидация).
8. Действие отдельно от проверки: `fill…`/`click…` ничего не проверяют; `checkThat…` только проверяют через `PlaywrightAssertions.assertThat(locator)` и ничего не кликают. Без `locator.isVisible()` в JUnit-проверках и ручных ожиданий.
9. Без `checkThatOpened`, `checkPageLoaded`, `isLoaded`, `waitUntilLoaded`: `click`/`fill` ждут элемент, web-first assertions ждут состояние. URL и видимость страницы проверяем, только когда переход и есть предмет теста.
10. Локаторы: `getByRole` с точным `name`, `getByLabel`, `getByPlaceholder`, `getByText` с точным совпадением, `getByTestId`. Без `first()`/`nth()`, MUI-классов и XPath; неуникальный элемент сужай родителем: `page.getByRole(AriaRole.BANNER).getByRole(…)`.
11. MUI `Select` (`combobox`): option не ищи по точному `name` страны — accessible name бывает составным (`France France` = alt флага + текст). Бери `getByRole(OPTION).filter(has: getByRole(IMG, name=страна, exact))`.
12. Если после действия сразу идут `reload()`/навигация/чтение сохранённого состояния, дождись завершения запроса внутри действия: `page.waitForResponse(..., click::run)`, иначе следующий шаг обгонит мутацию (как `UpdateUser` в `clickSave()`). Асинхронный UI без сети (FileReader после `setInputFiles`) тоже дождись в действии через `assertThat(locator).hasAttribute(...)` — иначе Save уйдёт до `formValues.avatar`.
13. Аватар профиля и флаги стран оба `data:image/…` в `main`. Флаги в Location имеют `alt` (имя страны), MUI Avatar в ImageUpload — нет: `main img[src^='data:image/']:not([alt])`.
14. Проверка не должна быть тавтологией локатора: `assertThat(heading("Travels map")).hasText("Travels map")` ничего не доказывает. Ожидаемое значение приходит из теста (`checkThatUsername(username)`).
15. Вход только через UI; `access_token`/`id_token` в `localStorage` не подставлять (до занятия 13).
16. Без комментариев и Javadoc: смысл передают имена методов и текст `@Step`.

## Проверка результата

Прогон `./gradlew :rfr-e2e:test --tests '<Тест>' --rerun-tasks` — единственное подтверждение. Проход сценария через MCP запуском теста не считается.
В отчёте разделяй: что подтверждено в браузере через MCP, что взято из исходников.
