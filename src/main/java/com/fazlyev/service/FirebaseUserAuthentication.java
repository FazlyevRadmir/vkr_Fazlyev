package com.fazlyev.service;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class FirebaseUserAuthentication extends AbstractAuthenticationToken {
    private final String principal;
    private final String uid;
    private final String firebaseToken;

    public FirebaseUserAuthentication(String email, String uid,
                                      Collection<? extends GrantedAuthority> authorities,
                                      String firebaseToken) {
        super(authorities);
        this.principal = email;
        this.uid = uid;
        this.firebaseToken = firebaseToken;
        super.setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return firebaseToken;
    }

    @Override
    public Object getPrincipal() {
        return principal;
    }

    public String getUid() {
        return uid;
    }
}