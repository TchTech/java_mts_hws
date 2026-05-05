package com.mipt.tchtech.exception;

import java.util.List;

public class BulkOperationException extends RuntimeException {

    private final List<Long> missingIds;

    public BulkOperationException(List<Long> missingIds) {
        super("Не найдены задачи с идентификаторами: " + missingIds);
        this.missingIds = missingIds;
    }

    public List<Long> getMissingIds() {
        return missingIds;
    }
}
