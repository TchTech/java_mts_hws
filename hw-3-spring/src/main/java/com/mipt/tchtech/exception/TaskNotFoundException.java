package com.mipt.tchtech.exception;

public class TaskNotFoundException extends RuntimeException {

    public TaskNotFoundException(String message) {
        super(message);
    }

    public TaskNotFoundException(long id) {
        super("Задача с id=" + id + " не найдена");
    }
}