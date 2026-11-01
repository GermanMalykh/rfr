package io.student.rangiffler.ui.component;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import io.qameta.allure.Param;
import io.qameta.allure.Step;
import io.qameta.allure.model.Parameter;

public final class CredentialsForm {
  private final Page page;
  private final Locator username;

  public CredentialsForm(Page page) {
    this.page = page;
    this.username = page.getByPlaceholder("Type your username");
  }

  @Step("Ввести логин и пароль: {username} / ***")
  public CredentialsForm fillCredentials(
      String username,
      @Param(mode = Parameter.Mode.HIDDEN) String password) {
    this.username.fill(username);
    page.fill("input[name='password']", password);
    return this;
  }
}
