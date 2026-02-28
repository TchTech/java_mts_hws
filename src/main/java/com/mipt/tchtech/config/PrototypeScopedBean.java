package com.mipt.tchtech.config;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Бин с областью видимости prototype.
 * Создает новый экземпляр при каждом запросе из контекста и генерирует уникальный идентификатор.
 *
 * @author mts.tchtech
 * @version 1.0
 */
@Component
@Scope("prototype")
public class PrototypeScopedBean {

    private static final Logger log = LoggerFactory.getLogger(PrototypeScopedBean.class);
    private final String generatedId;

    public PrototypeScopedBean() {
        this.generatedId = UUID.randomUUID().toString();
        log.info("PrototypeScopedBean создан, сгенерирован ID: {}", generatedId);
    }

    public String getGeneratedId() {
        return generatedId;
    }
}
