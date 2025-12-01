package com.bccard.qrpay.config;

import com.bccard.qrpay.config.dto.Post;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Slf4j
public class RestClientTest {

    //    @Autowired
    private static RestClient restClient = RestClient.create();

    //    @Test
    @DisplayName("RestClient create 정적팩토리, builder")
    void test_create() {
        RestClient defaultRestClient = RestClient.create();
        log.info("{}", defaultRestClient);
        //        log.info("{}", defaultRestClient.mutate().ge);

        RestClient customClient = RestClient.builder()
                .requestFactory(new JdkClientHttpRequestFactory())
                //                .requestFactory(new HttpComponentsClientHttpRequestFactory())
                //                .messageConverters(converters -> converters.add(new MyCustomMessageConverter()))
                .baseUrl("https://example.com")
                .defaultUriVariables(Map.of("variable", "foo"))
                .defaultHeader("My-Header", "Foo")
                .defaultCookie("My-Cookie", "Bar")
                //                .requestInterceptor(myCustomInterceptor)
                //                .requestInitializer(myCustomInitializer)
                .build();

        log.info("{}", customClient);
    }

    //    @Test
    void test_get_request_response_handling() {

        String post_1 = restClient
                .get()
                .uri("https://jsonplaceholder.typicode.com/posts/1")
                .retrieve() // 최종연산이 아님.
                .body(String.class);

        ResponseEntity<String> post_1_entity = restClient
                .get()
                .uri("https://jsonplaceholder.typicode.com/posts/1")
                .retrieve()
                .toEntity(String.class);

        log.info("GET post_1 result={}", post_1);
        log.info("GET post_1 result={}", post_1_entity);

        RestClient baseClient = RestClient.builder()
                .baseUrl("https://jsonplaceholder.typicode.com")
                .build();

        int id = 2;
        Post post_2 = baseClient
                .get()

                //                .uri("https://jsonplaceholder.typicode.com/posts/{id}", id)
                .uri(uriBuilder -> uriBuilder.path("/posts/" + id).build())
                .header("Test-Header", "test")
                .headers(httpHeaders -> {
                    httpHeaders.add(HttpHeaders.CONTENT_TYPE, "application/json");
                })
                .accept(MediaType.APPLICATION_JSON)
                .acceptCharset(StandardCharsets.UTF_8)
                .retrieve()
                .body(Post.class);

        ResponseEntity<Post> post_2_entity = restClient
                .get()
                .uri("https://jsonplaceholder.typicode.com/posts/1")
                .retrieve()
                .toEntity(Post.class);
        log.info("GET post_2 result={}", post_2);

        ParameterizedTypeReference<List<Post>> postListTypeRef = new ParameterizedTypeReference<>() {};

        List<Post> pstList = restClient
                .get()
                .uri("https://jsonplaceholder.typicode.com/posts")
                .retrieve()
                .body(postListTypeRef);

        ResponseEntity<List<Post>> postList_entity = restClient
                .get()
                .uri("https://jsonplaceholder.typicode.com/posts")
                .retrieve()
                .toEntity(postListTypeRef);
        log.info("GET postList_entity size={}, result={}", pstList.size(), pstList.getFirst());
        log.info(
                "GET postList_entity size={}, headers={}",
                postList_entity.getBody().size(),
                postList_entity.getHeaders());
    }

    //    @Test
    void test_get_errorHandingAndExchange() {
        String post_1_onStatus = restClient
                .get()
                .uri("https://jsonplaceholder.typicode.com/posts/1")
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                    throw new RuntimeException(response.getStatusText());
                })
                .body(String.class);

        String post_exchange = restClient
                .get()
                .uri("https://jsonplaceholder.typicode.com/posts/1")
                .exchange((request, response) -> {
                    if (response.getStatusCode().is4xxClientError()) {
                        throw new RuntimeException(response.getStatusText());
                    } else if (response.getStatusCode().is5xxServerError()) {
                        throw new RuntimeException(response.getStatusText());
                    } else {
                        return response.bodyTo(String.class);
                    }
                });

        ResponseEntity<Post> exchange = restClient
                .get()
                .uri("https://jsonplaceholder.typicode.com/posts/1")
                .exchange((request, response) -> {
                    if (response.getStatusCode().isError()) {
                        return new ResponseEntity<>(
                                response.bodyTo(Post.class), response.getHeaders(), response.getStatusCode());
                    } else {
                        return new ResponseEntity<>(
                                response.bodyTo(Post.class), response.getHeaders(), response.getStatusCode());
                    }
                });
    }

    //    @Test
    void test_post_request_response_handling() {

        restClient
                .post()
                .uri("https://jsonplaceholder.typicode.com/posts/{id}")
                .header("Test-Header", "test")
                .headers(httpHeaders -> {
                    httpHeaders.add(HttpHeaders.CONTENT_TYPE, "application/json");
                })
                .accept(MediaType.APPLICATION_JSON)
                .acceptCharset(StandardCharsets.UTF_8);
        //                .bod
        ;
    }
}
