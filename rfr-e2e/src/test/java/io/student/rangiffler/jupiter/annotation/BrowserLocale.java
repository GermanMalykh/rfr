package io.student.rangiffler.jupiter.annotation;

import com.microsoft.playwright.junit.UsePlaywright;
import io.student.rangiffler.jupiter.extension.BrowserLocaleExtension;
import io.student.rangiffler.jupiter.extension.BrowserLocaleOptionsFactory;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.junit.jupiter.api.extension.ExtendWith;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@ExtendWith(BrowserLocaleExtension.class)
@UsePlaywright(BrowserLocaleOptionsFactory.class)
public @interface BrowserLocale {
  String value();
}
