package com.mipt.angelikaliber.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
public class LoggingAspect {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoggingAspect.class);

    @Around("execution(* com.mipt.angelikaliber.service..*(..))")
    public Object logServiceCalls(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String method = signature.getDeclaringType().getSimpleName() + "." + signature.getName();
        LOGGER.info("--> {} args={}", method, Arrays.toString(joinPoint.getArgs()));
        try {
            Object result = joinPoint.proceed();
            if (signature.getReturnType().equals(Void.TYPE)) {
                LOGGER.info("<-- {} (void)", method);
            } else {
                LOGGER.info("<-- {} result={}", method, result);
            }
            return result;
        } catch (Throwable ex) {
            LOGGER.warn("<-- {} threw {}: {}", method, ex.getClass().getSimpleName(), ex.getMessage());
            throw ex;
        }
    }
}
