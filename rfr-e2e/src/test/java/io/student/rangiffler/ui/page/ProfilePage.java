package io.student.rangiffler.ui.page;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import io.qameta.allure.Param;
import io.qameta.allure.Step;
import io.qameta.allure.model.Parameter;
import io.student.rangiffler.config.TestConfig;
import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public final class ProfilePage {
  public static final String URL = TestConfig.frontUrl() + "/profile";
  private final Page page;
  private final Locator firstNameInput;
  private final Locator surnameInput;
  private final Locator usernameInput;
  private final Locator location;
  private final Locator avatarInput;
  private final Locator avatarImage;
  private final Locator reset;
  private final Locator save;

  public ProfilePage(Page page) {
    this.page = page;
    this.firstNameInput = page.getByLabel("First Name", new Page.GetByLabelOptions().setExact(true));
    this.surnameInput = page.getByLabel("Surname", new Page.GetByLabelOptions().setExact(true));
    this.usernameInput = page.getByLabel("Username", new Page.GetByLabelOptions().setExact(true));
    this.location = page.getByRole(AriaRole.COMBOBOX,
        new Page.GetByRoleOptions().setName("Location").setExact(true));
    this.avatarInput = page.getByLabel("Update avatar", new Page.GetByLabelOptions().setExact(true));
    this.avatarImage = page.locator("main img[src^='data:image/']");
    this.reset = page.getByRole(AriaRole.BUTTON,
        new Page.GetByRoleOptions().setName("Reset").setExact(true));
    this.save = page.getByRole(AriaRole.BUTTON,
        new Page.GetByRoleOptions().setName("Save").setExact(true));
  }

  @Step("Открыть профиль")
  public ProfilePage open() {
    page.navigate(URL);
    return this;
  }

  @Step("Перезагрузить страницу профиля")
  public ProfilePage reload() {
    page.reload();
    return this;
  }

  @Step("Заполнить форму профиля: {form}")
  public ProfilePage fillProfileForm(ProfileForm form) {
    if (form.firstName() != null) {
      firstNameInput.fill(form.firstName());
    }
    if (form.surname() != null) {
      surnameInput.fill(form.surname());
    }
    if (form.country() != null) {
      location.click();
      page.getByRole(AriaRole.OPTION)
          .filter(new Locator.FilterOptions()
              .setHas(page.getByRole(AriaRole.IMG,
                  new Page.GetByRoleOptions().setName(form.country()).setExact(true))))
          .click();
    }
    if (form.avatar() != null) {
      avatarInput.setInputFiles(form.avatar());
    }
    return this;
  }

  @Step("Нажать Save")
  public ProfilePage clickSave() {
    page.waitForResponse(
        response -> response.url().contains("/graphql")
            && response.request().postData() != null
            && response.request().postData().contains("UpdateUser"),
        save::click);
    return this;
  }

  @Step("Нажать Reset")
  public ProfilePage clickReset() {
    reset.click();
    return this;
  }

  @Step("Проверить имя пользователя в профиле: {username}")
  public ProfilePage checkThatUsername(String username) {
    assertThat(usernameInput).hasValue(username);
    return this;
  }

  @Step("Проверить First Name в профиле: {firstName}")
  public ProfilePage checkThatFirstName(String firstName) {
    assertThat(firstNameInput).hasValue(firstName);
    return this;
  }

  @Step("Проверить Surname в профиле: {surname}")
  public ProfilePage checkThatSurname(String surname) {
    assertThat(surnameInput).hasValue(surname);
    return this;
  }

  @Step("Проверить Location в профиле: {countryName}")
  public ProfilePage checkThatLocation(String countryName) {
    assertThat(location.getByRole(AriaRole.IMG,
        new Locator.GetByRoleOptions().setName(countryName).setExact(true))).isVisible();
    return this;
  }

  @Step("Проверить src аватара")
  public ProfilePage checkThatAvatar(@Param(mode = Parameter.Mode.HIDDEN) String expectedDataUrl) {
    assertThat(avatarImage).hasAttribute("src", expectedDataUrl);
    return this;
  }
}
