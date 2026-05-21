package com.mipt.angelikaliber.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.time.Duration;

@Configuration
public class RestClientConfig {

    @Bean
    public RestClient externalRestClient(@Value("${app.external.base-url}") String baseUrl,
                                         @Value("${app.external.connect-timeout-ms}") int connectTimeoutMs,
                                         @Value("${app.external.read-timeout-ms}") int readTimeoutMs,
                                         @Value("${app.external.user-agent}") String userAgent) {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(Duration.ofMillis(connectTimeoutMs));
        factory.setReadTimeout(Duration.ofMillis(readTimeoutMs));
        return RestClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader(HttpHeaders.USER_AGENT, userAgent)
                .defaultHeader(HttpHeaders.ACCEPT, "application/json")
                .requestFactory(factory)
                .build();
    }
}
