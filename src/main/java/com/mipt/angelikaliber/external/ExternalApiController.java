package com.mipt.angelikaliber.external;

import com.mipt.angelikaliber.dto.gateway.GatewayTaskDto;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@RestController
@RequestMapping("/external/v1")
public class ExternalApiController {

    private final Map<Long, GatewayTaskDto> store = new ConcurrentHashMap<>();
    private final AtomicLong sequence = new AtomicLong(0);

    @PostMapping("/tasks")
    public ResponseEntity<GatewayTaskDto> create(@RequestBody GatewayTaskDto body) {
        long id = sequence.incrementAndGet();
        GatewayTaskDto saved = new GatewayTaskDto(id, body.getTitle(), body.getDescription(), body.isCompleted());
        store.put(id, saved);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(id)
                .toUri();
        return ResponseEntity.created(location)
                .contentType(MediaType.APPLICATION_JSON)
                .body(saved);
    }

    @GetMapping("/tasks/{id}")
    public ResponseEntity<?> getOne(@PathVariable Long id) {
        GatewayTaskDto task = store.get(id);
        if (task == null) {
            return notFound(id);
        }
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(task);
    }

    @GetMapping("/tasks")
    public ResponseEntity<List<GatewayTaskDto>> list(@RequestParam(required = false) Boolean completed,
                                                     @RequestParam(required = false) Integer limit) {
        List<GatewayTaskDto> all = new ArrayList<>(store.values());
        if (completed != null) {
            all.removeIf(t -> t.isCompleted() != completed);
        }
        if (limit != null && limit >= 0 && all.size() > limit) {
            all = new ArrayList<>(all.subList(0, limit));
        }
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(all);
    }

    @PutMapping("/tasks/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody GatewayTaskDto body) {
        if (!store.containsKey(id)) {
            return notFound(id);
        }
        GatewayTaskDto updated = new GatewayTaskDto(id, body.getTitle(), body.getDescription(), body.isCompleted());
        store.put(id, updated);
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(updated);
    }

    @DeleteMapping("/tasks/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        GatewayTaskDto removed = store.remove(id);
        if (removed == null) {
            return notFound(id);
        }
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/unstable")
    public ResponseEntity<?> unstable(@RequestParam String mode) throws InterruptedException {
        switch (mode) {
            case "timeout" -> Thread.sleep(5000);
            case "500" -> {
                ProblemDetail pd = ProblemDetail.forStatusAndDetail(
                        org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR,
                        "External service failure");
                return ResponseEntity.internalServerError()
                        .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                        .body(pd);
            }
            case "429" -> {
                ProblemDetail pd = ProblemDetail.forStatusAndDetail(
                        org.springframework.http.HttpStatus.TOO_MANY_REQUESTS,
                        "Rate limited by external service");
                return ResponseEntity.status(429)
                        .header(HttpHeaders.RETRY_AFTER, "2")
                        .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                        .body(pd);
            }
            case "html" -> {
                String html = "<html><body><h1>502 Bad Gateway</h1></body></html>";
                return ResponseEntity.status(502)
                        .contentType(MediaType.TEXT_HTML)
                        .body(html);
            }
            default -> {
                return ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(Map.of("status", "ok"));
            }
        }
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(Map.of("status", "ok"));
    }

    private ResponseEntity<ProblemDetail> notFound(Long id) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(
                org.springframework.http.HttpStatus.NOT_FOUND,
                "Task " + id + " not found in external service");
        pd.setTitle("Not Found");
        return ResponseEntity.status(404)
                .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .body(pd);
    }
}
