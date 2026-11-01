package io.student.rangiffler.jupiter.extension;

import io.student.rangiffler.jupiter.annotation.BrowserLocale;
import java.util.Locale;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

public final class BrowserLocaleExtension implements BeforeAllCallback, AfterAllCallback {
  @Override
  public void beforeAll(ExtensionContext context) {
    Class<?> testClass = context.getRequiredTestClass();
    BrowserLocale annotation = testClass.getAnnotation(BrowserLocale.class);
    if (annotation == null) {
      throw new IllegalStateException(
          "@BrowserLocale is required on " + testClass.getName()
              + " when BrowserLocaleExtension is registered");
    }
    BrowserLocaleOptionsFactory.install(requireValidLocale(annotation.value(), testClass));
  }

  @Override
  public void afterAll(ExtensionContext context) {
    BrowserLocaleOptionsFactory.clear();
  }

  private static String requireValidLocale(String raw, Class<?> testClass) {
    if (raw == null || raw.isBlank()) {
      throw new IllegalArgumentException(
          "@BrowserLocale on " + testClass.getName() + " must not be blank");
    }
    Locale locale = Locale.forLanguageTag(raw.trim().replace('_', '-'));
    if (locale.getLanguage().isEmpty()) {
      throw new IllegalArgumentException(
          "@BrowserLocale on " + testClass.getName()
              + " must be a valid language tag (e.g. en-US), got: '" + raw + "'");
    }
    return locale.toLanguageTag();
  }
}
