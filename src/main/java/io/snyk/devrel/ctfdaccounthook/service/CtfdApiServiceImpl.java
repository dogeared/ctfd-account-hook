package io.snyk.devrel.ctfdaccounthook.service;

import io.snyk.devrel.ctfdaccounthook.Exception.CtfdApiException;
import io.snyk.devrel.ctfdaccounthook.model.CtfdApiErrorResponse;
import io.snyk.devrel.ctfdaccounthook.model.CtfdCreateUserResponse;
import io.snyk.devrel.ctfdaccounthook.model.CtfdUser;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

@Service
public class CtfdApiServiceImpl implements CtfdApiService {

    @Value("#{ @environment['ctfd.api.token'] }")
    private String ctfdApiToken;

    @Value("#{ @environment['ctfd.api.base-url'] }")
    private String ctfdApiBaseUrl;

    private final WebClient.Builder webClientBuilder;
    private WebClient webClient;

    public static final String API_URI = "/api/v1";

    public CtfdApiServiceImpl(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }

    @PostConstruct
    public void setup() {
        this.webClient = this.webClientBuilder
            .baseUrl(ctfdApiBaseUrl)
            .defaultHeader("Authorization", "Token " + ctfdApiToken)
            .defaultHeader("Content-Type", "application/json")
            .build();
    }

    @Override
    public Mono<Map> getChallenges() {
        return this.webClient.get().uri(API_URI + "/challenges").retrieve().bodyToMono(Map.class);
    }

    @Override
    public Mono<Map> getUsers() {
        return this.webClient.get().uri(API_URI + "/users").retrieve().bodyToMono(Map.class);
    }

    @Override
    public CtfdCreateUserResponse createUser(String email, String alias) throws CtfdApiException {
        CtfdUser ctfdUser = new CtfdUser();
        ctfdUser.setEmail(email);
        ctfdUser.setName(alias);
        ctfdUser.setPassword("123456aA$");
        ClientResponse res = this.webClient.post().uri(API_URI + "/users")
            .body(BodyInserters.fromValue(ctfdUser))
            .exchange()
            .block();
        if (res.statusCode().is2xxSuccessful()) {
            return res.bodyToMono(CtfdCreateUserResponse.class).block();
        }
        CtfdApiErrorResponse error = res.bodyToMono(CtfdApiErrorResponse.class).block();
        throw new CtfdApiException(error);
    }
}
