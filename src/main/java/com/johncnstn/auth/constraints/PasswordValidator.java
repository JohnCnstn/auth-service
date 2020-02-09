package com.johncnstn.auth.constraints;

import com.google.common.base.CharMatcher;
import org.apache.commons.lang3.CharUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import static org.apache.commons.lang3.StringUtils.containsWhitespace;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

public class PasswordValidator implements ConstraintValidator<Password, CharSequence> {

    @SuppressWarnings("checkstyle:LineLength")
    private static final CharMatcher PRINTABLE_MATCHER = CharMatcher.forPredicate(CharUtils::isAsciiPrintable).precomputed();
    @SuppressWarnings("checkstyle:LineLength")
    private static final CharMatcher LOWER_MATCHER = CharMatcher.forPredicate(CharUtils::isAsciiAlphaLower).precomputed();
    @SuppressWarnings("checkstyle:LineLength")
    private static final CharMatcher UPPER_MATCHER = CharMatcher.forPredicate(CharUtils::isAsciiAlphaUpper).precomputed();
    @SuppressWarnings("checkstyle:LineLength")
    private static final CharMatcher DIGITS_MATCHER = CharMatcher.forPredicate(CharUtils::isAsciiNumeric).precomputed();

    public static boolean isValid(CharSequence value) {
        return isNotEmpty(value)
                && !containsWhitespace(value)
                && PRINTABLE_MATCHER.matchesAllOf(value)
                && LOWER_MATCHER.matchesAnyOf(value)
                && UPPER_MATCHER.matchesAnyOf(value)
                && DIGITS_MATCHER.matchesAnyOf(value);
    }

    @Override
    public boolean isValid(CharSequence value, ConstraintValidatorContext context) {
        return isValid(value);
    }

    @Override
    public void initialize(Password constraintAnnotation) {
    }

}
