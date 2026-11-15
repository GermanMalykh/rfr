package io.student.rangiffler.jupiter.extension;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.junit.Options;
import com.microsoft.playwright.junit.OptionsFactory;

public final class BrowserLocaleOptionsFactory implements OptionsFactory {
  private static final ThreadLocal<String> LOCALE = new ThreadLocal<>();

  static void install(String locale) {
    LOCALE.set(locale);
  }

  static void clear() {
    LOCALE.remove();
  }

  @Override
  public Options getOptions() {
    String locale = LOCALE.get();
    if (locale == null) {
      throw new IllegalStateException(
          "Browser locale is not installed. Annotate the test class with @BrowserLocale(\"en-US\") "
              + "(do not use BrowserLocaleOptionsFactory without @BrowserLocale)");
    }
    return new Options()
        .setContextOptions(new Browser.NewContextOptions().setLocale(locale));
  }
}
