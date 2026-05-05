package com.mipt.tchtech.dto;

import com.mipt.tchtech.model.Priority;

public class PriorityStatisticsDto {

    private Priority priority;
    private long count;

    public PriorityStatisticsDto() {
    }

    public PriorityStatisticsDto(Priority priority, long count) {
        this.priority = priority;
        this.count = count;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }
}
