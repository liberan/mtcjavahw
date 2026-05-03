package com.mipt.angelikaliber.aspect;

import com.mipt.angelikaliber.repository.TaskRepository;
import com.mipt.angelikaliber.service.TaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

@Component
public class TaskLifecycleProcessor implements BeanPostProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(TaskLifecycleProcessor.class);

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof TaskService || bean instanceof TaskRepository) {
            LOGGER.info("BeanPostProcessor: before init bean='{}' type={}",
                    beanName, bean.getClass().getSimpleName());
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof TaskService || bean instanceof TaskRepository) {
            LOGGER.info("BeanPostProcessor: after init  bean='{}' type={}",
                    beanName, bean.getClass().getSimpleName());
        }
        return bean;
    }
}
