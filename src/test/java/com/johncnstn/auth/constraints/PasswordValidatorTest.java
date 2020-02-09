package com.johncnstn.auth.constraints;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PasswordValidatorTest {

    @Test
    public void testNull() {
        testPassword(null, false);
    }

    @Test
    public void testEmpty() {
        testPassword("", false);
    }

    @Test
    public void testWithWhitespace() {
        testPassword("Password 1", false);
    }

    @Test
    public void testWithNonAscii() {
        testPassword("Passwordя1", false);
    }

    @Test
    public void testWithoutAnyLower() {
        testPassword("PASSWORD1", false);
    }

    @Test
    public void testWithoutAnyUpper() {
        testPassword("password1", false);
    }

    @Test
    public void testWithoutDigits() {
        testPassword("password", false);
    }

    @Test
    public void testFine() {
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
