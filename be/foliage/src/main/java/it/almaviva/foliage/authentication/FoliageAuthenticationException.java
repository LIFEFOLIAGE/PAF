/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package it.almaviva.foliage.authentication;

import org.springframework.security.core.AuthenticationException;

/**
 *
 * @author A.Rossi
 */
public class FoliageAuthenticationException extends AuthenticationException {
    public FoliageAuthenticationException(String message) {
        super(message);
    }

    public FoliageAuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }
}
