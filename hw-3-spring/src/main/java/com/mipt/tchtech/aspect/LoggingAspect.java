package com.mipt.tchtech.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {

    private static final Logger log = LoggerFactory.getLogger(LoggingAspect.class);

    @Pointcut("within(com.mipt.tchtech.service..*)")
    public void serviceLayer() {
    }

    @Around("serviceLayer()")
    public Object logServiceAccess(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();

        log.info("АСПЕКТ [Начало]: Выполнение {}.{}()", className, methodName);
        try {
            Object result = joinPoint.proceed();
            log.info("АСПЕКТ [Конец]: Завершено {}.{}(), Результат: {}", className, methodName, result);
            return result;
        } catch (Throwable e) {
            log.error("АСПЕКТ [Ошибка]: Исключение в {}.{}() - {}", className, methodName, e.getMessage());
            throw e;
        }
    }
}
