package com.example.orderservice.webclient.config;

import com.example.orderservice.webclient.logger.WebClientLogger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    private final WebClientLogger webClientLogger;

    public WebClientConfig(WebClientLogger webClientLogger) {
        this.webClientLogger = webClientLogger;
    }

    @Bean
    public WebClient webClient() {
        return WebClient.builder()
                .baseUrl("http://localhost:8082") // Inventory service port
                .filter(webClientLogger.logRequest())
                .filter(webClientLogger.logResponse())
                .build();
    }
}

