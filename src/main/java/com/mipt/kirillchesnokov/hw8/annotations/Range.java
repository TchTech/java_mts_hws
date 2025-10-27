package com.mipt.kirillchesnokov.hw8.annotations;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Range {
    long min();
    long max();
    String message();
}