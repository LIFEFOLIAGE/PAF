/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package it.almaviva.foliage.authentication;

import org.springframework.security.authentication.AbstractAuthenticationToken;

import lombok.Getter;

/**
 *
 * @author A.Rossi
 */
public class JwtAuthentication extends AbstractAuthenticationToken {

    @Getter
    private final AccessToken accessToken;
    public String getUsername() {
        return accessToken.getUsername();
    }

    public JwtAuthentication(AccessToken accessToken) {
        super(accessToken.getAuthorities());
        this.accessToken = accessToken;
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return accessToken.getValueAsString();
    }
}