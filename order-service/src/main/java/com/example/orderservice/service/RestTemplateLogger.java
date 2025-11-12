package com.example.orderservice.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestTemplate;

public class RestTemplateLogger {

    private static final Logger log = LoggerFactory.getLogger(RestTemplateLogger.class);

    public static RestTemplate getLoggingTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getInterceptors().add(loggingInterceptor());
        return restTemplate;
    }

    private static ClientHttpRequestInterceptor loggingInterceptor() {
        return (request, body, execution) -> {
            log.info("Request: {} {}", request.getMethod(), request.getURI());
            log.info("Headers: {}", request.getHeaders());
            if (body.length > 0) log.info("Body: {}", new String(body));

            var response = execution.execute(request, body);

            log.info("Response: {}", response.getStatusCode());
            return response;
        };
    }
}