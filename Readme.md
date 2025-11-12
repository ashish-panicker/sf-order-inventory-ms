# Simple Order Inventory

## Description

This is a sample repository containing order and inventory microservice.
Thses microservices are built using Spring Boot v3 and Java 21.

## Understanding `RestTemplate` in Spring

## What is `RestTemplate`?

`RestTemplate` is a **synchronous HTTP client** provided by the **Spring Framework** for making RESTful web service calls from within a Spring application.

It simplifies interaction with external APIs by handling:

* HTTP connections
* Serialization/deserialization of request/response bodies
* Error handling

Essentially, it acts as the **client-side equivalent of a REST controller**, allowing your application to **consume REST APIs** easily.

**Example:**

```java
RestTemplate restTemplate = new RestTemplate();
String result = restTemplate.getForObject("https://api.example.com/data", String.class);
```

---

## What Type of API Calls Are Being Made?

`RestTemplate` supports **all standard HTTP operations**, each mapped to a convenient method:

| HTTP Method | RestTemplate Method                   | Description                                         |
| ----------- | ------------------------------------- | --------------------------------------------------- |
| `GET`       | `getForObject()` / `getForEntity()`   | Fetch resource data                                 |
| `POST`      | `postForObject()` / `postForEntity()` | Create new resource                                 |
| `PUT`       | `put()`                               | Update resource                                     |
| `DELETE`    | `delete()`                            | Remove resource                                     |
| `PATCH`     | `patchForObject()`                    | Partially update a resource                         |
| `EXCHANGE`  | `exchange()`                          | Custom method for any HTTP method with full control |

**Example (GET + Query Params):**

```java
String url = "http://localhost:8082/api/inventory/check?code=BOOK-001&qty=2";
Boolean available = restTemplate.getForObject(url, Boolean.class);
```

Here, the call is:

* **Type:** `GET`
* **Nature:** **Synchronous**
* **Target:** Another REST API (Inventory Service)

---

## How to Log `RestTemplate` Calls

You can log requests and responses by attaching a **ClientHttpRequestInterceptor**.

### Example:

```java
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
```

You can then use:

```java
RestTemplate restTemplate = RestTemplateLogger.getLoggingTemplate();
```

This approach is excellent for **debugging inter-service communication**.

---

## Why Was `RestTemplate` Discontinued (Deprecated Path)?

Spring marked `RestTemplate` as ***deprecated in favor of non-blocking alternatives*** starting from **Spring 5** because:

| Reason                            | Explanation                                                                                                                                   |
| --------------------------------- | --------------------------------------------------------------------------------------------------------------------------------------------- |
| **Blocking I/O Model**            | `RestTemplate` uses traditional synchronous/blocking I/O — each call occupies a thread until completion, which limits scalability under load. |
| **Does Not Fit Reactive Stack**   | It doesn’t integrate with **Reactor / WebFlux**, making it unsuitable for reactive microservices.                                             |
|  **No Active Feature Development** | While still usable, Spring announced that **no new features will be added** — only maintenance fixes.                                         |
|  **Modern Alternatives Exist**  | The newer `WebClient` supports both synchronous and asynchronous patterns efficiently.                                                        |

---

## Inbuilt Alternatives (Without Feign)

### **`WebClient` (Spring WebFlux)**

The **modern replacement** for `RestTemplate`.
Supports **reactive, non-blocking**, and **asynchronous** communication.

**Example:**

```java
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

WebClient webClient = WebClient.create("http://localhost:8082");

Boolean available = webClient.get()
        .uri(uriBuilder -> uriBuilder.path("/api/inventory/check")
                .queryParam("code", "BOOK-001")
                .queryParam("qty", 2)
                .build())
        .retrieve()
        .bodyToMono(Boolean.class)
        .block(); // blocks only if needed
```

**Benefits:**

* Non-blocking and scalable
* Supports both reactive and traditional use
* Easy to configure and extend with filters, interceptors, and retry policies

---

## Summary Table

| Feature      | RestTemplate                            | WebClient                                |
| ------------ | --------------------------------------- | ---------------------------------------- |
| Type         | Blocking (Synchronous)                  | Non-blocking (Reactive or Sync)          |
| API Style    | Imperative                              | Reactive (Mono/Flux)                     |
| Thread Usage | One thread per request                  | Event-loop model                         |
| Suitable for | Simple, legacy, or synchronous services | Reactive, high-performance microservices |
| Status       | Maintenance mode                        | Actively developed                       |

