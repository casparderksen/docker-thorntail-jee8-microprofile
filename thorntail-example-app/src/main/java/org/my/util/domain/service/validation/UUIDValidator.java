package org.my.util.domain.service.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

public class UUIDValidator implements ConstraintValidator<ValidUUID, String> {

    private Pattern pattern;

    @Override
    public void initialize(ValidUUID constraint) {
        pattern = Pattern.compile("[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[1-5][0-9a-fA-F]{3}-[89abAB][0-9a-fA-F]{3}-[0-9a-fA-F]{12}");
    }

    @Override
    public boolean isValid(String uuid, ConstraintValidatorContext context) {
        return pattern.matcher(uuid).matches();
    }
}
