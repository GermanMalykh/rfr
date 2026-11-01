package io.student.rangiffler.utils;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public final class RandomDataUtils {
  public static final String PREFIX = "rfr_";
  private static final List<String> COUNTRIES = List.of(
      "France",
      "Germany",
      "Italy",
      "Spain",
      "Japan"
  );

  private RandomDataUtils() {
  }

  public static String randomUsername() {
    return PREFIX + UUID.randomUUID().toString().replace("-", "").substring(0, 8);
  }

  public static String randomPassword() {
    return "Aa1" + UUID.randomUUID().toString().replace("-", "").substring(0, 8);
  }

  public static String randomFirstName() {
    return "Fn" + shortId();
  }

  public static String randomSurname() {
    return "Sn" + shortId();
  }

  public static String country() {
    return COUNTRIES.getFirst();
  }

  public static String anotherCountry(String exclude) {
    return COUNTRIES.stream()
        .filter(country -> !country.equals(exclude))
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("No country other than " + exclude));
  }

  public static Path avatar() {
    try {
      return Path.of(Objects.requireNonNull(
          RandomDataUtils.class.getClassLoader().getResource("img/avatar.png")).toURI());
    } catch (URISyntaxException e) {
      throw new IllegalStateException("Avatar resource img/avatar.png is missing", e);
    }
  }

  public static String dataUrl(Path path) {
    try {
      String mimeType = Objects.requireNonNullElse(Files.probeContentType(path), "application/octet-stream");
      String base64 = Base64.getEncoder().encodeToString(Files.readAllBytes(path));
      return "data:" + mimeType + ";base64," + base64;
    } catch (IOException e) {
      throw new IllegalStateException("Cannot build data URL for " + path, e);
    }
  }

  private static String shortId() {
    return UUID.randomUUID().toString().replace("-", "").substring(0, 8);
  }
}
