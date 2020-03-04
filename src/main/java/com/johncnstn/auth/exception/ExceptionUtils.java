package com.johncnstn.auth.exception;

import java.util.function.Supplier;
import lombok.experimental.UtilityClass;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@UtilityClass
public class ExceptionUtils {

    public static Supplier<UsernameNotFoundException> usernameNotFound(String message) {
        return () -> new UsernameNotFoundException(message);
    }
}
