package com.johncnstn.auth.constraints;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.johncnstn.auth.unit.AbstractUnitTest;
import org.junit.jupiter.api.Test;

public class PasswordValidatorTest extends AbstractUnitTest {

    @Test
    public void nullShouldNotBeValid() {
        testPasswordValidation(null, false);
    }

    @Test
    public void emptyShouldNotBeValid() {
        testPasswordValidation("", false);
    }

    @Test
    public void withWhitespaceShouldNotBeValid() {
        testPasswordValidation("Password 1", false);
    }

    @Test
    public void withNonAsciiShouldNotBeValid() {
        testPasswordValidation("Passwordя1", false);
    }

    @Test
    public void withoutAnyLowerShouldNotBeValid() {
        testPasswordValidation("PASSWORD1", false);
    }

    @Test
    public void withoutAnyUpperShouldNotBeValid() {
        testPasswordValidation("password1", false);
    }

    @Test
    public void withoutDigitsShouldNotBeValid() {
        testPasswordValidation("password", false);
    }

    @Test
    public void goodValueShouldBeValid() {
        testPasswordValidation("Password1", true);
    }

    private void testPasswordValidation(String password, boolean shouldBeValid) {
        // WHEN
        var isValid = PasswordValidator.isPasswordValid(password);

        // THEN
        assertEquals(shouldBeValid, isValid);
    }
}
