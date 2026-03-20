package com.mipt.tchtech.config;

import java.time.LocalDateTime;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

/**
 * Бин с областью видимости request.
 * Создается для каждого HTTP-запроса, хранит уникальный идентификатор и время старта запроса.
 *
 * @author mts.tchtech
 * @version 1.0
 */
@Component
@Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class RequestScopedBean {

    private static final Logger log = LoggerFactory.getLogger(RequestScopedBean.class);

    private final String requestId;
    private final LocalDateTime startTime;

    public RequestScopedBean() {
        this.requestId = UUID.randomUUID().toString();
        this.startTime = LocalDateTime.now();
        log.info("RequestScopedBean создан: ID={}, Время старта={}", requestId, startTime);
    }

    public String getRequestId() {
        return requestId;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }
}
