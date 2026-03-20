package com.mipt.tchtech.exception;

public class TaskNotFoundException extends RuntimeException {

    private final String taskId;

    public TaskNotFoundException(String taskId) {
        super("Задача не найдена: " + taskId);
        this.taskId = taskId;
    }

    public String getTaskId() {
        return taskId;
    }
}
