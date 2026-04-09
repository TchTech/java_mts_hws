package com.mipt.tchtech.security;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class PepperedBCryptPasswordEncoder implements PasswordEncoder {

    private final BCryptPasswordEncoder delegate = new BCryptPasswordEncoder();
    private final String pepper;

    public PepperedBCryptPasswordEncoder(String pepper) {
        this.pepper = pepper;
    }

    @Override
    public String encode(CharSequence rawPassword) {
        return delegate.encode(rawPassword + pepper);
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        return delegate.matches(rawPassword + pepper, encodedPassword);
    }
}