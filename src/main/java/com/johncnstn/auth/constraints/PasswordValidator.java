package com.johncnstn.auth.constraints;

import static org.apache.commons.lang3.StringUtils.containsWhitespace;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

import com.google.common.base.CharMatcher;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import org.apache.commons.lang3.CharUtils;

public class PasswordValidator implements ConstraintValidator<Password, CharSequence> {

    private static final CharMatcher PRINTABLE_MATCHER =
            CharMatcher.forPredicate(CharUtils::isAsciiPrintable).precomputed();

    private static final CharMatcher LOWER_MATCHER =
            CharMatcher.forPredicate(CharUtils::isAsciiAlphaLower).precomputed();

    private static final CharMatcher UPPER_MATCHER =
            CharMatcher.forPredicate(CharUtils::isAsciiAlphaUpper).precomputed();

    private static final CharMatcher DIGITS_MATCHER =
            CharMatcher.forPredicate(CharUtils::isAsciiNumeric).precomputed();

    public static boolean isPasswordValid(CharSequence value) {
        return isNotEmpty(value)
                && !containsWhitespace(value)
                && PRINTABLE_MATCHER.matchesAllOf(value)
                && LOWER_MATCHER.matchesAnyOf(value)
                && UPPER_MATCHER.matchesAnyOf(value)
                && DIGITS_MATCHER.matchesAnyOf(value);
    }

    @Override
    public boolean isValid(CharSequence value, ConstraintValidatorContext context) {
        return isPasswordValid(value);
    }

    @Override
    public void initialize(Password constraintAnnotation) {}
}
