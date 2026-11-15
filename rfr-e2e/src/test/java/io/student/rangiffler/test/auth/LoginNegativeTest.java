package io.student.rangiffler.test.auth;

import com.microsoft.playwright.Page;
import io.student.rangiffler.data.TestUser;
import io.student.rangiffler.jupiter.annotation.BrowserLocale;
import io.student.rangiffler.jupiter.annotation.User;
import io.student.rangiffler.ui.page.LandingPage;
import io.student.rangiffler.utils.RandomDataUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@BrowserLocale("en-US")
@DisplayName("Негативный вход")
class LoginNegativeTest {
  private static final String BAD_CREDENTIALS = "Bad credentials";

  @Test
  @DisplayName("Неверный пароль показывает Bad credentials")
  void wrongPasswordShowsBadCredentials(Page page, @User TestUser user) {
    new LandingPage(page).open()
        .clickLogin()
        .fillLoginForm(user.username(), RandomDataUtils.randomPassword())
        .clickSignInWithError()
        .checkThatError(BAD_CREDENTIALS);
  }

  @Test
  @DisplayName("Несуществующий пользователь показывает Bad credentials")
  void unknownUserShowsBadCredentials(Page page) {
    new LandingPage(page).open()
        .clickLogin()
        .fillLoginForm(RandomDataUtils.randomUsername(), RandomDataUtils.randomPassword())
        .clickSignInWithError()
        .checkThatError(BAD_CREDENTIALS);
  }
}
