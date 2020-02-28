package com.johncnstn.auth.entity.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum UserRoleType {
    USER("USER"),
    ADMIN("ADMIN");

    private String value;

    UserRoleType(final String value) {
        this.value = value;
    }

    @Override
    @JsonValue
    public String toString() {
        return String.valueOf(value);
    }

    @JsonCreator
    public static UserRoleType fromValue(final String text) {
        for (var b : UserRoleType.values()) {
            if (String.valueOf(b.value).equals(text)) {
                return b;
            }
        }
        return null;
    }
}
