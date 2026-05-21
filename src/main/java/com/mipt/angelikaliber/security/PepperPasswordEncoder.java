package com.mipt.angelikaliber.security;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class PepperPasswordEncoder implements PasswordEncoder {

    private final BCryptPasswordEncoder delegate = new BCryptPasswordEncoder();
    private final String pepper;

    public PepperPasswordEncoder(String pepper) {
        this.pepper = pepper;
    }

    @Override
    public String encode(CharSequence rawPassword) {
        return delegate.encode(rawPassword.toString() + pepper);
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        return delegate.matches(rawPassword.toString() + pepper, encodedPassword);
    }
}
