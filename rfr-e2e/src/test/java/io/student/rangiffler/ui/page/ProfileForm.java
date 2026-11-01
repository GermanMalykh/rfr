package io.student.rangiffler.ui.page;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public final class ProfileForm {
  private final String firstName;
  private final String surname;
  private final String country;
  private final Path avatar;

  private ProfileForm(Builder builder) {
    this.firstName = builder.firstName;
    this.surname = builder.surname;
    this.country = builder.country;
    this.avatar = builder.avatar;
  }

  public static Builder builder() {
    return new Builder();
  }

  public String firstName() {
    return firstName;
  }

  public String surname() {
    return surname;
  }

  public String country() {
    return country;
  }

  public Path avatar() {
    return avatar;
  }

  @Override
  public String toString() {
    List<String> parts = new ArrayList<>();
    if (firstName != null) {
      parts.add("firstName=" + firstName);
    }
    if (surname != null) {
      parts.add("surname=" + surname);
    }
    if (country != null) {
      parts.add("country=" + country);
    }
    if (avatar != null) {
      parts.add("avatar=" + avatar.getFileName());
    }
    return parts.toString();
  }

  public static final class Builder {
    private String firstName;
    private String surname;
    private String country;
    private Path avatar;

    public Builder firstName(String firstName) {
      this.firstName = firstName;
      return this;
    }

    public Builder surname(String surname) {
      this.surname = surname;
      return this;
    }

    public Builder country(String country) {
      this.country = country;
      return this;
    }

    public Builder avatar(Path avatar) {
      this.avatar = avatar;
      return this;
    }

    public ProfileForm build() {
      return new ProfileForm(this);
    }
  }
}
