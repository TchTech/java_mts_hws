package com.mipt.kirillchesnokov.hw8;

import com.mipt.kirillchesnokov.hw8.annotations.*;
import java.lang.reflect.Field;
import java.util.regex.Pattern;

public class Validator {
    private static final Pattern EMAIL_RE = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    public static ValidationResult validate(Object object) {
        ValidationResult result = new ValidationResult();
        if (object == null) {
            result.addError("Object is Null");
            return result;
        }
        Class<?> clazz = object.getClass();
        for (Field f : clazz.getDeclaredFields()) {
            f.setAccessible(true);
            Object value;
            try {
                value = f.get(object);
            } catch (IllegalAccessException e) {
                result.addError("Не удалось получить класс: " + f.getName());
                continue;
            }

            NotNull nn = f.getAnnotation(NotNull.class);
            if (nn != null && value == null) {
                result.addError(nn.message());
            }

            Size sz = f.getAnnotation(Size.class);
            if (sz != null) {
                if (value == null) {
                } else if (value instanceof CharSequence cs) {
                    int len = cs.length();
                    if (len < sz.min() || len > sz.max()) {
                        result.addError(sz.message());
                    }
                } else {
                    result.addError("@Size применим только к CharSequence. Field: " + f.getName());
                }
            }

            Range rg = f.getAnnotation(Range.class);
            if (rg != null) {
                if (value == null) {
                } else if (value instanceof Number num) {
                    long v = num.longValue();
                    if (v < rg.min() || v > rg.max()) {
                        result.addError(rg.message());
                    }
                } else {
                    result.addError("@Range применим только к Number. Field: " + f.getName());
                }
            }

            Email em = f.getAnnotation(Email.class);
            if (em != null) {
                if (value == null) {
                } else if (value instanceof CharSequence cs) {
                    if (!EMAIL_RE.matcher(cs).matches()) {
                        result.addError(em.message());
                    }
                } else {
                    result.addError("@Email Применим только к CharSequence. Field: " + f.getName());
                }
            }
        }
        return result;
    }
}
