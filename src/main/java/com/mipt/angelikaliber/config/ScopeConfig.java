package com.mipt.angelikaliber.config;

import com.mipt.angelikaliber.service.PrototypeScopedBean;
import com.mipt.angelikaliber.service.RequestScopedBean;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.annotation.RequestScope;

/**
 * Wires beans with non-singleton scopes used to demonstrate Spring's bean
 * lifecycle behaviours.
 *
 * <p>{@code requestScopedBean} is created once per HTTP request, while
 * {@code prototypeScopedBean} is created on every injection point or lookup.
 */
@Configuration
public class ScopeConfig {

    /**
     * Per-request bean. Tied to the {@link WebApplicationContext#SCOPE_REQUEST}
     * scope, so a fresh instance backs every HTTP request.
     *
     * @return a new {@link RequestScopedBean} per request
     */
    @Bean
    @RequestScope
    public RequestScopedBean requestScopedBean() {
        return new RequestScopedBean();
    }

    /**
     * Prototype-scoped bean. A new instance is produced for every injection or
     * {@code getBean} call.
     *
     * @return a new {@link PrototypeScopedBean} per lookup
     */
    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public PrototypeScopedBean prototypeScopedBean() {
        return new PrototypeScopedBean();
    }
}
