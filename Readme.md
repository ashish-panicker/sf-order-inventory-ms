# Simple Order Inventory

## Description

This is a sample repository containing order and inventory microservice.
These microservices are built using Spring Boot v3 and Java 21.

## 1. WebClient Overview

`WebClient` is the **modern reactive HTTP client** introduced in Spring 5, intended to replace the older `RestTemplate`.
It supports both **synchronous** (blocking) and **asynchronous** (non-blocking, reactive) operations.

> Key advantage: It is fully non-blocking, making it ideal for microservices that need to scale with fewer threads.

---

## 2. Setup

### Add Dependency (Spring Boot 3+)

```xml
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-webflux</artifactId>
</dependency>
```

### Define a WebClient Bean

```java
@Bean
public WebClient webClient(WebClient.Builder builder) {
    return builder
        .baseUrl("http://localhost:8082/api/inventory")
        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .build();
}
```

Or create ad hoc:

```java
WebClient webClient = WebClient.create("http://localhost:8082/api/inventory");
```

---

## 3. Sending a POST Request

### Use Case

Send a new product to Inventory Service:

```java
public record Product(String code, String name, int quantity) {}
```

### Reactive (Recommended)

```java
public Mono<Product> addProduct(Product product) {
    return webClient.post()
        .uri("/add") // Full URL: http://localhost:8082/api/inventory/add
        .bodyValue(product) // JSON body
        .retrieve() // handle 2xx by default
        .bodyToMono(Product.class) // map response to Product
        .doOnNext(p -> log.info("Product added: {}", p))
        .onErrorResume(e -> {
            log.error("Error adding product", e);
            return Mono.empty(); // fallback
        });
}
```

### Blocking (for tests/demos only)

```java
public Product addProductBlocking(Product product) {
    return webClient.post()
        .uri("/add")
        .bodyValue(product)
        .retrieve()
        .bodyToMono(Product.class)
        .block(); // avoid in reactive flows
}
```

### Adding Custom Headers

```java
webClient.post()
    .uri("/add")
    .header("X-Request-Id", "abc123")
    .bodyValue(product)
    .retrieve()
    .bodyToMono(Product.class);
```

---

## 4. Sending a PUT Request

### Use Case

Update the stock of a product in the inventory.

```java
public record StockUpdateRequest(String code, int quantity) {}
```

### Reactive PUT Request

```java
public Mono<Product> updateStock(String code, int qty) {
    StockUpdateRequest update = new StockUpdateRequest(code, qty);

    return webClient.put()
        .uri("/update")
        .bodyValue(update)
        .retrieve()
        .bodyToMono(Product.class)
        .doOnNext(p -> log.info("Stock updated for {}", p.code()))
        .onErrorResume(e -> {
            log.error("Error updating stock", e);
            return Mono.empty();
        });
}
```

### Blocking Version

```java
public Product updateStockBlocking(String code, int qty) {
    return webClient.put()
        .uri("/update")
        .bodyValue(new StockUpdateRequest(code, qty))
        .retrieve()
        .bodyToMono(Product.class)
        .block();
}
```

---

## 5. Advanced Status Handling with `exchangeToMono()`

For fine-grained control over HTTP statuses:

```java
public Mono<Product> updateStockSafe(String code, int qty) {
    return webClient.put()
        .uri("/update")
        .bodyValue(new StockUpdateRequest(code, qty))
        .exchangeToMono(response -> {
            if (response.statusCode().is2xxSuccessful()) {
                return response.bodyToMono(Product.class);
            } else if (response.statusCode().is4xxClientError()) {
                return Mono.error(new RuntimeException("Client error: " + response.statusCode()));
            } else {
                return Mono.error(new RuntimeException("Server error: " + response.statusCode()));
            }
        });
}
```

---

## 6. Sending Other Body Types

### Form Data

```java
webClient.post()
    .uri("/submit")
    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
    .body(BodyInserters.fromFormData("name", "Ashish").with("qty", "10"))
    .retrieve()
    .bodyToMono(String.class);
```

### Multipart (File Upload)

```java
webClient.post()
    .uri("/upload")
    .contentType(MediaType.MULTIPART_FORM_DATA)
    .body(BodyInserters.fromMultipartData("file", new FileSystemResource(file)))
    .retrieve()
    .bodyToMono(String.class);
```

---

## 7. Understanding WebClient’s Core Methods

| Method                           | Purpose                                  |
| -------------------------------- | ---------------------------------------- |
| `WebClient.builder()`            | Create a configurable WebClient instance |
| `baseUrl(String)`                | Set the default base URL                 |
| `get(), post(), put(), delete()` | Specify HTTP method                      |
| `uri(...)`                       | Define endpoint and query params         |
| `bodyValue(Object)`              | Send JSON body automatically             |
| `body(BodyInserter)`             | Send form or multipart body              |
| `retrieve()`                     | Simplified success/error decoding        |
| `exchangeToMono()`               | Low-level access to `ClientResponse`     |
| `bodyToMono(Class<T>)`           | Convert response to Mono<T>              |
| `bodyToFlux(Class<T>)`           | Convert response to Flux<T>              |
| `onStatus(...)`                  | Handle status-specific errors            |
| `header(...)`                    | Add custom headers                       |
| `filter(...)`                    | Add logging, tracing, auth filters       |

---

## 8. Reactive Concepts Recap

| Concept                               | Description                                                    |
| ------------------------------------- | -------------------------------------------------------------- |
| **Mono<T>**                           | Emits 0 or 1 element asynchronously (like `Future<T>`)         |
| **Flux<T>**                           | Emits 0 to N elements asynchronously (like a stream)           |
| **map()**                             | Transform data synchronously                                   |
| **flatMap()**                         | Transform asynchronously (returns another Mono/Flux)           |
| **onErrorReturn() / onErrorResume()** | Handle or recover from errors                                  |
| **subscribe()**                       | Triggers execution (reactive pipelines are lazy)               |
| **Schedulers**                        | Define threads for execution (useful for blocking integration) |

---

## 9. Logging Requests and Responses

You can attach a logging filter to inspect requests/responses:

```java
@Bean
public WebClient webClientWithLogging(WebClient.Builder builder) {
    return builder
        .filter((request, next) -> {
            log.info("Request: {} {}", request.method(), request.url());
            return next.exchange(request)
                       .doOnNext(response -> log.info("Response: {}", response.statusCode()));
        })
        .build();
}
```

For detailed structured logs, use a dedicated `ExchangeFilterFunction` or integrate with tools like **Spring Sleuth** or **Micrometer Tracing**.

---

## 10. Error Handling Patterns

```java
webClient.post()
    .uri("/add")
    .bodyValue(product)
    .retrieve()
    .onStatus(HttpStatus::is4xxClientError, response ->
        Mono.error(new RuntimeException("Invalid request!"))
    )
    .onStatus(HttpStatus::is5xxServerError, response ->
        Mono.error(new RuntimeException("Inventory Service unavailable!"))
    )
    .bodyToMono(Product.class)
    .retryWhen(Retry.backoff(3, Duration.ofMillis(200))); // optional retry
```

---

## 11. Quick Reference Table

| Use Case | Method     | Request Type | Response Type         | Notes                 |
| -------- | ---------- | ------------ | --------------------- | --------------------- |
| Create   | `post()`   | JSON         | `Mono<T>`             | Add new entity        |
| Update   | `put()`    | JSON         | `Mono<T>`             | Update entity         |
| Get      | `get()`    | Query params | `Mono<T>` / `Flux<T>` | Retrieve data         |
| Delete   | `delete()` | Path param   | `Mono<Void>`          | Delete entity         |
| Batch    | `post()`   | JSON array   | `Flux<T>`             | Concurrent processing |

---

## 12. Best Practices

* Prefer **reactive** (`Mono`/`Flux`) over `.block()` for scalability.
* Centralize your WebClient configuration (timeouts, headers, logging).
* Use `exchangeToMono()` for advanced response handling.
* Avoid blocking operations inside reactive chains.
* Add correlation IDs for cross-service tracing.
* Integrate `Resilience4j` for circuit breakers and retries.

---

## 13. Example in Order Service Context

```java
@Service
public class OrderService {
    private final WebClient webClient;

    public OrderService(WebClient webClient) {
        this.webClient = webClient;
    }

    public Mono<Order> placeOrder(Order order) {
        return webClient.post()
            .uri("/checkAndReserve")
            .bodyValue(order)
            .retrieve()
            .bodyToMono(Order.class)
            .map(response -> new Order(
                    response.id(),
                    response.productCode(),
                    response.quantity(),
                    response.status()
            ));
    }
}
```

---

### In Summary

* **POST** → send new resources
* **PUT** → update existing resources
* Use `.bodyValue()` for sending JSON
* Use `.retrieve()` for simple response handling, `.exchangeToMono()` for complex ones
* Handle errors and timeouts gracefully
* Keep your code non-blocking — that’s where WebClient shines!
