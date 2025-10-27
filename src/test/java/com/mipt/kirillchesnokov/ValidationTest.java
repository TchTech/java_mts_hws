package com.mipt.kirillchesnokov;

import com.mipt.kirillchesnokov.hw8.*;
import com.mipt.kirillchesnokov.hw8.annotations.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ValidatorTest {

    static class DTO {
        @NotNull(message = "name null")
        @Size(min = 2, max = 3, message = "name size")
        String name;

        @Email(message = "email invalid")
        String email;

        @Range(min = 0, max = 10, message = "age range")
        Integer age;

        @Size(min = 1, max = 5, message = "note size")
        Object note;
    }

    @Test
    void validObjectPasses() {
        DTO d = new DTO();
        d.name = "Аня";
        d.email = "a@b.ru";
        d.age = 5;
        d.note = "ok";
        ValidationResult vr = Validator.validate(d);
        assertTrue(vr.isValid(), "expected valid");
        assertTrue(vr.getErrors().isEmpty());
    }

    @Test
    void collectsAllErrors() {
        DTO d = new DTO();
        d.name = "AnyName";
        d.email = "wrongwrongwrong";
        d.age = 998;
        d.note = 123;
        ValidationResult vr = Validator.validate(d);
        assertFalse(vr.isValid());
        assertEquals(4, vr.getErrors().size());
        assertTrue(vr.getErrors().contains("name size"));
        assertTrue(vr.getErrors().contains("email invalid"));
        assertTrue(vr.getErrors().contains("age range"));
        assertTrue(vr.getErrors().stream().anyMatch(s -> s.contains("@Size")));
    }

    @Test
    void nullsOnlyFailWhenNotNullPresent() {
        DTO d = new DTO();
        d.name = null;
        d.email = null;
        d.age = null;
        d.note = null;
        ValidationResult vr = Validator.validate(d);
        assertFalse(vr.isValid());
        assertEquals(1, vr.getErrors().size());
        assertTrue(vr.getErrors().contains("name null"));
    }

    @Test
    void boundaryValues() {
        DTO d = new DTO();
        d.name = "L";
        d.email = "a@b.x";
        d.age = -1;
        d.note = "1";
        ValidationResult vr = Validator.validate(d);
        assertFalse(vr.isValid());
        assertTrue(vr.getErrors().stream().anyMatch(s -> s.contains("age range")));
    }
}
