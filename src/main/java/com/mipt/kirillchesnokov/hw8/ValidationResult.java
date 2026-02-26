package com.mipt.kirillchesnokov.hw8;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ValidationResult {
    private boolean valid = true;
    private final List<String> errors = new ArrayList<>();

    public boolean isValid() {
        return valid;
    }

    public List<String> getErrors() {
        return Collections.unmodifiableList(errors);
    }

    public void addError(String message) {
        if (message == null || message.isBlank()) return;
        valid = false;
        errors.add(message);
    }
}
