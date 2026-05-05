package com.mipt.tchtech.dto;

import java.time.LocalDate;
import java.util.Set;

import com.mipt.tchtech.model.Priority;
import com.mipt.tchtech.validation.DueDateNotBeforeCreation;
import com.mipt.tchtech.validation.OnUpdate;
import jakarta.validation.constraints.Size;

@DueDateNotBeforeCreation(groups = OnUpdate.class)
public class TaskUpdateDto {

    @Size(min = 3, max = 100, groups = OnUpdate.class, message = "Название должно содержать от 3 до 100 символов")
    private String title;

    @Size(max = 500, message = "Описание не должно превышать 500 символов")
    private String description;

    private Boolean completed;

    private LocalDate dueDate;

    private Priority priority;

    @Size(max = 5, message = "Нельзя указать более 5 тегов")
    private Set<String> tags;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getCompleted() {
        return completed;
    }

    public void setCompleted(Boolean completed) {
        this.completed = completed;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public Set<String> getTags() {
        return tags;
    }

    public void setTags(Set<String> tags) {
        this.tags = tags;
    }
}
