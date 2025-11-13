package com.example.orderservice.webclient.client;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class InventoryClient {

    private final WebClient webClient;

    public InventoryClient(WebClient webClient) {
        this.webClient = webClient;
    }

    public boolean checkStock(String code, int qty) {
        Boolean result = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/inventory/check")
                        .queryParam("code", code)
                        .queryParam("qty", qty)
                        .build())
                .retrieve()
                .bodyToMono(Boolean.class)
                .block(); // blocking only for demo purposes
        return result != null && result;
    }

    public Mono<Boolean> checkStockReactive(String code, int qty) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/inventory/check")
                        .queryParam("code", code)
                        .queryParam("qty", qty)
                        .build())
                .retrieve()
                // checking for 4xx and 5xx HTTP status codes
                .onStatus(HttpStatusCode:: is4xxClientError, resp -> Mono.error(new RuntimeException("Client error")))
                .onStatus(HttpStatusCode::is5xxServerError, resp -> Mono.error(new RuntimeException("Server error")))
                .bodyToMono(Boolean.class)
                .onErrorReturn(false);
    }
}