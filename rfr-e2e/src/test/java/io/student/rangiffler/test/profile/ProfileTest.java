package io.student.rangiffler.test.profile;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.junit.UsePlaywright;
import io.student.rangiffler.data.TestUser;
import io.student.rangiffler.jupiter.annotation.User;
import io.student.rangiffler.ui.page.LandingPage;
import io.student.rangiffler.ui.page.ProfileForm;
import io.student.rangiffler.ui.page.ProfilePage;
import io.student.rangiffler.utils.RandomDataUtils;
import java.nio.file.Path;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@UsePlaywright
@DisplayName("Профиль пользователя")
class ProfileTest {

  @Test
  @DisplayName("First Name сохраняется и читается после reload")
  void shouldSaveFirstName(Page page, @User TestUser user) {
    String firstName = RandomDataUtils.randomFirstName();

    openProfile(page, user)
        .fillProfileForm(ProfileForm.builder().firstName(firstName).build())
        .clickSave()
        .reload()
        .checkThatFirstName(firstName);
  }

  @Test
  @DisplayName("Surname сохраняется и читается после reload")
  void shouldSaveSurname(Page page, @User TestUser user) {
    String surname = RandomDataUtils.randomSurname();

    openProfile(page, user)
        .fillProfileForm(ProfileForm.builder().surname(surname).build())
        .clickSave()
        .reload()
        .checkThatSurname(surname);
  }

  @Test
  @DisplayName("Смена страны сохраняется и читается после reload")
  void shouldChangeLocation(Page page, @User TestUser user) {
    String country = RandomDataUtils.country();

    openProfile(page, user)
        .fillProfileForm(ProfileForm.builder().country(country).build())
        .clickSave()
        .reload()
        .checkThatLocation(country);
  }

  @Test
  @DisplayName("Аватар сохраняется и читается после reload")
  void shouldSaveAvatar(Page page, @User TestUser user) {
    Path avatar = RandomDataUtils.avatar();
    String expectedDataUrl = RandomDataUtils.dataUrl(avatar);

    openProfile(page, user)
        .fillProfileForm(ProfileForm.builder().avatar(avatar).build())
        .clickSave()
        .reload()
        .checkThatAvatar(expectedDataUrl);
  }

  @Test
  @DisplayName("Reset возвращает сохранённые значения")
  void shouldResetToSavedValues(Page page, @User TestUser user) {
    String firstName = RandomDataUtils.randomFirstName();
    String surname = RandomDataUtils.randomSurname();
    String country = RandomDataUtils.country();

    ProfilePage profile = openProfile(page, user)
        .fillProfileForm(ProfileForm.builder()
            .firstName(firstName)
            .surname(surname)
            .country(country)
            .build())
        .clickSave()
        .reload()
        .checkThatFirstName(firstName)
        .checkThatSurname(surname)
        .checkThatLocation(country);

    profile
        .fillProfileForm(ProfileForm.builder()
            .firstName(RandomDataUtils.randomFirstName())
            .surname(RandomDataUtils.randomSurname())
            .country(RandomDataUtils.anotherCountry(country))
            .build())
        .clickReset()
        .checkThatFirstName(firstName)
        .checkThatSurname(surname)
        .checkThatLocation(country);
  }

  private static ProfilePage openProfile(Page page, TestUser user) {
    return new LandingPage(page).open()
        .clickLogin()
        .fillLoginForm(user.username(), user.password())
        .clickSignIn()
        .clickProfile();
  }
}
