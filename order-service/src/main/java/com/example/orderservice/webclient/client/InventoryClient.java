package com.example.orderservice.webclient.client;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

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
}