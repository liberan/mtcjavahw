package com.mipt.angelikaliber.service;

import com.mipt.angelikaliber.client.ExternalTasksClient;
import com.mipt.angelikaliber.dto.gateway.GatewayTaskDto;
import com.mipt.angelikaliber.exception.ExternalApiException;
import com.mipt.angelikaliber.exception.TaskNotFoundException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.List;

@Service
public class TasksGatewayService {

    private static final Logger log = LoggerFactory.getLogger(TasksGatewayService.class);
    private static final String NAME = "externalApi";

    private final ExternalTasksClient client;

    public TasksGatewayService(ExternalTasksClient client) {
        this.client = client;
    }

    @RateLimiter(name = NAME)
    @CircuitBreaker(name = NAME, fallbackMethod = "createTaskFallback")
    public CreatedTask createTask(GatewayTaskDto body) {
        ExternalTasksClient.CreateResult res = client.create(body);
        return new CreatedTask(res.task(), res.location());
    }

    @RateLimiter(name = NAME)
    @CircuitBreaker(name = NAME, fallbackMethod = "getTaskFallback")
    public GatewayTaskDto getTask(Long id) {
        return client.get(id);
    }

    @RateLimiter(name = NAME)
    @CircuitBreaker(name = NAME, fallbackMethod = "listTasksFallback")
    public List<GatewayTaskDto> listTasks(Boolean completed, Integer limit) {
        return client.list(completed, limit);
    }

    @RateLimiter(name = NAME)
    @CircuitBreaker(name = NAME, fallbackMethod = "deleteTaskFallback")
    public void deleteTask(Long id) {
        client.delete(id);
    }

    public CreatedTask createTaskFallback(GatewayTaskDto body, Throwable t) {
        rethrowIfDomain(t);
        log.warn("createTask fallback reason={}", t.toString());
        GatewayTaskDto stub = new GatewayTaskDto(null, body.getTitle(), body.getDescription(), false);
        return new CreatedTask(stub, null);
    }

    public GatewayTaskDto getTaskFallback(Long id, Throwable t) {
        rethrowIfDomain(t);
        log.warn("getTask fallback id={} reason={}", id, t.toString());
        return new GatewayTaskDto(id, "unavailable", "external service degraded", false);
    }

    public List<GatewayTaskDto> listTasksFallback(Boolean completed, Integer limit, Throwable t) {
        rethrowIfDomain(t);
        log.warn("listTasks fallback reason={}", t.toString());
        return List.of();
    }

    public void deleteTaskFallback(Long id, Throwable t) {
        rethrowIfDomain(t);
        log.warn("deleteTask fallback id={} reason={}", id, t.toString());
    }

    private void rethrowIfDomain(Throwable t) {
        if (t instanceof TaskNotFoundException tnf) {
            throw tnf;
        }
        if (t instanceof ExternalApiException eae && eae.getStatus() == 404) {
            throw eae;
        }
    }

    public record CreatedTask(GatewayTaskDto task, URI location) {
    }
}
