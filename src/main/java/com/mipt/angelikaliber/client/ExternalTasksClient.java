package com.mipt.angelikaliber.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mipt.angelikaliber.dto.gateway.GatewayTaskDto;
import com.mipt.angelikaliber.exception.ExternalApiException;
import com.mipt.angelikaliber.exception.TaskNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
public class ExternalTasksClient {

    private static final Logger log = LoggerFactory.getLogger(ExternalTasksClient.class);
    private static final int MAX_BODY_LOG = 256;

    private final RestClient restClient;
    private final ObjectMapper mapper;

    public ExternalTasksClient(RestClient externalRestClient, ObjectMapper mapper) {
        this.restClient = externalRestClient;
        this.mapper = mapper;
    }

    public CreateResult create(GatewayTaskDto body) {
        ResponseEntity<GatewayTaskDto> response = restClient.post()
                .uri("/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(body)
                .retrieve()
                .onStatus(status -> status.value() == 404, (req, res) -> handle404(res))
                .onStatus(status -> status.is5xxServerError(), (req, res) -> handle5xx(res))
                .toEntity(GatewayTaskDto.class);
        validateJson(response);
        URI location = response.getHeaders().getLocation();
        return new CreateResult(response.getBody(), location);
    }

    public GatewayTaskDto get(Long id) {
        ResponseEntity<GatewayTaskDto> response = restClient.get()
                .uri(uriBuilder -> uriBuilder.path("/tasks/{id}").build(id))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(status -> status.value() == 404, (req, res) -> handle404(res))
                .onStatus(status -> status.is5xxServerError(), (req, res) -> handle5xx(res))
                .toEntity(GatewayTaskDto.class);
        validateJson(response);
        return response.getBody();
    }

    public List<GatewayTaskDto> list(Boolean completed, Integer limit) {
        ResponseEntity<List<GatewayTaskDto>> response = restClient.get()
                .uri(uriBuilder -> {
                    uriBuilder.path("/tasks");
                    if (completed != null) {
                        uriBuilder.queryParam("completed", completed);
                    }
                    if (limit != null) {
                        uriBuilder.queryParam("limit", limit);
                    }
                    return uriBuilder.build();
                })
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(status -> status.is5xxServerError(), (req, res) -> handle5xx(res))
                .toEntity(new ParameterizedTypeReference<List<GatewayTaskDto>>() {
                });
        validateJson(response);
        return response.getBody();
    }

    public void delete(Long id) {
        ResponseEntity<Void> response = restClient.delete()
                .uri(uriBuilder -> uriBuilder.path("/tasks/{id}").build(id))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(status -> status.value() == 404, (req, res) -> handle404(res))
                .onStatus(status -> status.is5xxServerError(), (req, res) -> handle5xx(res))
                .toBodilessEntity();
        if (response.getStatusCode().value() != 204) {
            throw new ExternalApiException(response.getStatusCode().value(),
                    "Expected 204 No Content but got " + response.getStatusCode().value());
        }
    }

    private void handle404(org.springframework.http.client.ClientHttpResponse res) throws java.io.IOException {
        String body = readSafe(res);
        String detail = parseProblemDetail(body);
        throw new TaskNotFoundException(detail);
    }

    private void handle5xx(org.springframework.http.client.ClientHttpResponse res) throws java.io.IOException {
        String body = readSafe(res);
        log.warn("External 5xx status={} bodySnippet={}", res.getStatusCode().value(), snippet(body));
        throw new ExternalApiException(res.getStatusCode().value(),
                "External API returned " + res.getStatusCode().value());
    }

    private void validateJson(ResponseEntity<?> response) {
        MediaType contentType = response.getHeaders().getContentType();
        if (contentType == null) {
            return;
        }
        if (!contentType.includes(MediaType.APPLICATION_JSON)
                && !contentType.includes(MediaType.APPLICATION_PROBLEM_JSON)) {
            log.warn("Unexpected Content-Type={} status={}", contentType, response.getStatusCode().value());
            throw new ExternalApiException(response.getStatusCode().value(),
                    "Unexpected Content-Type from external service: " + contentType);
        }
    }

    private String readSafe(org.springframework.http.client.ClientHttpResponse res) throws java.io.IOException {
        byte[] bytes = res.getBody().readAllBytes();
        return new String(bytes, StandardCharsets.UTF_8);
    }

    private String parseProblemDetail(String body) {
        try {
            ProblemDetail pd = mapper.readValue(body, ProblemDetail.class);
            if (pd.getDetail() != null) {
                return pd.getDetail();
            }
        } catch (Exception ignored) {
        }
        return "Task not found";
    }

    private String snippet(String body) {
        if (body == null) {
            return "";
        }
        return body.length() <= MAX_BODY_LOG ? body : body.substring(0, MAX_BODY_LOG) + "...";
    }

    public record CreateResult(GatewayTaskDto task, URI location) {
    }

    public static HttpHeaders jsonHeaders() {
        HttpHeaders h = new HttpHeaders();
        h.setContentType(MediaType.APPLICATION_JSON);
        h.setAccept(List.of(MediaType.APPLICATION_JSON));
        return h;
    }
}
