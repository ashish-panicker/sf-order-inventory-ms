package com.example.orderservice.webclient.logger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import reactor.core.publisher.Mono;

@Component
public class WebClientLogger {

    private static final Logger logger = LoggerFactory.getLogger(WebClientLogger.class);

    public ExchangeFilterFunction logRequest() {
        return ExchangeFilterFunction.ofRequestProcessor(request -> {
            logger.info("=== Outgoing WebClient Request ===");
            logger.info("Method : {}", request.method());
            logger.info("URL    : {}", request.url());
            logger.info("Headers: {}", request.headers());
            return Mono.just(request);
        });
    }

    public ExchangeFilterFunction logResponse() {
        return ExchangeFilterFunction.ofResponseProcessor(response -> {
            logger.info("=== Incoming WebClient Response ===");
            logger.info("Status Code: {}", response.statusCode());
            response.headers().asHttpHeaders()
                    .forEach((k, v) -> logger.info("{} : {}", k, v));
            return Mono.just(response);
        });
    }
}
