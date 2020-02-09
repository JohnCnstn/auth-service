package com.johncnstn.auth.entity.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum UserRoleEntity {

    USER("USER"), ADMIN("ADMIN");

    private String value;

    UserRoleEntity(final String value) {
        this.value = value;
    }

    @Override
    @JsonValue
    public String toString() {
        return String.valueOf(value);
    }

    @JsonCreator
    public static UserRoleEntity fromValue(final String text) {
        for (var b : UserRoleEntity.values()) {
            if (String.valueOf(b.value).equals(text)) {
                return b;
            }
        }
        return null;
    }

}
