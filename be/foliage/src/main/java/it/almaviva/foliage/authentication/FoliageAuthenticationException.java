package it.almaviva.foliage.authentication;

import org.springframework.security.core.AuthenticationException;

public class FoliageAuthenticationException extends AuthenticationException {
    public FoliageAuthenticationException(String message) {
        super(message);
    }

    public FoliageAuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }
}
