package io.snyk.devrel.ctfdaccounthook.service;

import io.snyk.devrel.ctfdaccounthook.Exception.CtfdApiException;
import io.snyk.devrel.ctfdaccounthook.model.CtfdApiErrorResponse;
import io.snyk.devrel.ctfdaccounthook.model.CtfdCreateUserRequest;
import io.snyk.devrel.ctfdaccounthook.model.CtfdCreateUserResponse;
import io.snyk.devrel.ctfdaccounthook.model.CtfdUser;
import io.snyk.devrel.ctfdaccounthook.model.CtfdUserPaginatedResponse;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.UUID;

@Service
public class CtfdApiServiceImpl implements CtfdApiService {

    @Value("#{ @environment['ctfd.api.token'] }")
    private String ctfdApiToken;

    @Value("#{ @environment['ctfd.api.base-url'] }")
    private String ctfdApiBaseUrl;

    @Value("#{ @environment['ctfd.api.affiliation'] }")
    private String affiliation;

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
    public CtfdCreateUserResponse createUser(CtfdCreateUserRequest req, String alias) throws CtfdApiException {
        CtfdUser ctfdUser = new CtfdUser();
        ctfdUser.setEmail(req.getEmail());
        ctfdUser.setName(alias);
        ctfdUser.setPassword(UUID.randomUUID().toString());
        ctfdUser.setAffiliation(affiliation);
        String uri = API_URI + "/users" + (req.getNotify()?"?notify=true":"");
        ClientResponse res = this.webClient.post().uri(uri)
            .body(BodyInserters.fromValue(ctfdUser))
            .exchange()
            .block();
        if (res.statusCode().is2xxSuccessful()) {
            return res.bodyToMono(CtfdCreateUserResponse.class).block();
        }
        CtfdApiErrorResponse error = res.bodyToMono(CtfdApiErrorResponse.class).block();
        throw new CtfdApiException(error);
    }

    @Override
    public CtfdUserPaginatedResponse getUsersByAffiliation(String affiliation, Integer page) {
        if (affiliation == null) {
            affiliation = this.affiliation;
        }
        if (page == null) {
            page = 1;
        }
        String uri = API_URI + "/users?page=" + page + ((affiliation != null)?"&affiliation=" + affiliation:"");
        ClientResponse res = this.webClient.get().uri(uri).exchange().block();
        if (res.statusCode().is2xxSuccessful()) {
            return res.bodyToMono(CtfdUserPaginatedResponse.class).block();
        }
        CtfdApiErrorResponse error = new CtfdApiErrorResponse();
        error.getErrors().setMessage(String.format("Unable to get page %d for affiliation: %s", page, affiliation));
        throw new CtfdApiException(error);
    }
}
