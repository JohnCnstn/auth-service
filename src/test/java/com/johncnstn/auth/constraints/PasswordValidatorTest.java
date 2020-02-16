package com.johncnstn.auth.constraints;

import com.johncnstn.auth.unit.AbstractUnitTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PasswordValidatorTest extends AbstractUnitTest {

    @Test
    public void nullValue() {
        testPassword(null, false);
    }

    @Test
    public void empty() {
        testPassword("", false);
    }

    @Test
    public void withWhitespace() {
        testPassword("Password 1", false);
    }

    @Test
    public void withNonAscii() {
        testPassword("Passwordя1", false);
    }

    @Test
    public void withoutAnyLower() {
        testPassword("PASSWORD1", false);
    }

    @Test
    public void withoutAnyUpper() {
        testPassword("password1", false);
    }

    @Test
    public void withoutDigits() {
        testPassword("password", false);
    }

    @Test
    public void goodValue() {
        testPassword("Password1", true);
    }

    @Test
    private void testPassword(String password, boolean expected) {
        // WHEN
        var result = PasswordValidator.isValid(password);

        // THEN
        assertEquals(expected, result);
    }
}
