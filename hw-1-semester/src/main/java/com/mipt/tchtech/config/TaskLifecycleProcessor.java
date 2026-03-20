package com.mipt.tchtech.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

/**
 * Процессор жизненного цикла бинов.
 * Логирует этапы до и после инициализации для бинов, связанных с задачами (сервисы и репозитории).
 *
 * @author mts.tchtech
 * @version 1.0
 */
@Component
public class TaskLifecycleProcessor implements BeanPostProcessor {

    private static final Logger log = LoggerFactory.getLogger(TaskLifecycleProcessor.class);

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (isTaskBean(beanName)) {
            log.info("TaskLifecycleProcessor [BeforeInit]: Бин '{}' с типом {} будет инициализирован", beanName, bean.getClass().getSimpleName());
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (isTaskBean(beanName)) {
            log.info("TaskLifecycleProcessor [AfterInit]: Бин '{}' с типом {} был инициализирован", beanName, bean.getClass().getSimpleName());
        }
        return bean;
    }

    private boolean isTaskBean(String beanName) {
        return beanName.toLowerCase().contains("taskservice") || beanName.toLowerCase().contains("taskrepository");
    }
}
