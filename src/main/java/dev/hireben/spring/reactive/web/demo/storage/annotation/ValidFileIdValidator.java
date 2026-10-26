package dev.hireben.spring.reactive.web.demo.storage.annotation;

import org.hibernate.validator.constraintvalidation.HibernateConstraintValidatorContext;

import dev.hireben.spring.reactive.web.demo.common.utility.ExceptionUtil;
import dev.hireben.spring.reactive.web.demo.storage.annotation.api.ValidFileId;
import dev.hireben.spring.reactive.web.demo.storage.property.StorageProperties;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class ValidFileIdValidator implements ConstraintValidator<ValidFileId, String> {

  private final StorageProperties properties;

  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {
    context.disableDefaultConstraintViolation();

    if (value == null || value.isBlank()) {
      context.buildConstraintViolationWithTemplate(withErrorMessageKey("id_blank"))
          .addConstraintViolation();
      return false;
    }

    if (value.length() > properties.file().upload().idMaxLength()) {
      context.unwrap(HibernateConstraintValidatorContext.class)
          .addMessageParameter("max", properties.file().upload().idMaxLength())
          .buildConstraintViolationWithTemplate(withErrorMessageKey("id_too_long"))
          .addConstraintViolation();
      return false;
    }

    char prev = 0;
    for (int i = 0; i < value.length(); i++) {
      char c = value.charAt(i);
      if (!isUrlSafe(c)) {
        context.buildConstraintViolationWithTemplate(withErrorMessageKey("id_not_url_safe"))
            .addConstraintViolation();
        return false;
      }
      if (c == '.' && (i == 0 || prev == '.')) {
        context.buildConstraintViolationWithTemplate(withErrorMessageKey("id_illegal"))
            .addConstraintViolation();
        return false;
      }
      prev = c;
    }

    return true;
  }

  private static boolean isUrlSafe(char c) {
    return (c >= 'a' && c <= 'z')
        || (c >= 'A' && c <= 'Z')
        || (c >= '0' && c <= '9')
        || c == '-' || c == '_' || c == '.' || c == '~';
  }

  private static String withErrorMessageKey(String key) {
    return "{%s}".formatted(ExceptionUtil.errorMessageSourceKey("storage", key));
  }

}
