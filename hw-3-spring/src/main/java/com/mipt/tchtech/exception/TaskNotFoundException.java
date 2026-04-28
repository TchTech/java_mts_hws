package com.mipt.tchtech.exception;

public class TaskNotFoundException extends RuntimeException {

    private final Object taskId;

    public TaskNotFoundException(Object taskId) {
        super("Задача не найдена: " + taskId);
        this.taskId = taskId;
    }

    public Object getTaskId() {
        return taskId;
    }
}