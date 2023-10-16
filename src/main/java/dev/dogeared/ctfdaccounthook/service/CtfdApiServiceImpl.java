package dev.dogeared.ctfdaccounthook.service;

import dev.dogeared.ctfdaccounthook.model.CtfdCreateUserRequest;
import dev.dogeared.ctfdaccounthook.Exception.CtfdApiException;
import dev.dogeared.ctfdaccounthook.model.CtfdApiErrorResponse;
import dev.dogeared.ctfdaccounthook.model.CtfdUserResponse;
import dev.dogeared.ctfdaccounthook.model.CtfdUser;
import dev.dogeared.ctfdaccounthook.model.CtfdUserPaginatedResponse;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.util.retry.Retry;
import reactor.util.retry.RetryBackoffSpec;

import java.time.Duration;
import java.util.UUID;

@Service
public class CtfdApiServiceImpl implements CtfdApiService {

    @Value("#{ @environment['ctfd.api.token'] }")
    private String ctfdApiToken;

    @Value("#{ @environment['ctfd.api.base-url'] }")
    private String ctfdApiBaseUrl;

    @Value("#{ @environment['ctfd.api.affiliation'] }")
    private String affiliation;

    @Value("#{ @environment['ctfd.api.email-template'] }")
    private String emailTemplate;

    @Value("#{ @environment['ctfd.info.name'] }")
    private String ctfdName;

    @Value("#{ @environment['ctfd.info.url'] }")
    private String ctfdUrl;

    @Value("#{ @environment['ctfd.api.max-attempts'] ?: 2 }")
    private Integer maxAttempts;

    @Value("#{ @environment['ctfd.api.backoff-seconds'] ?: 60 }")
    private Integer backoffSeconds;


    private final WebClient.Builder webClientBuilder;
    private WebClient webClient;

    public static final String API_URI = "/api/v1";

    private static final Logger log = LoggerFactory.getLogger(CtfdApiServiceImpl.class);

    public CtfdApiServiceImpl(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }

    private RetryBackoffSpec retryBackoffSpec;

    @PostConstruct
    public void setup() {
        this.webClient = this.webClientBuilder
            .baseUrl(ctfdApiBaseUrl)
            .defaultHeader("Authorization", "Token " + ctfdApiToken)
            .defaultHeader("Content-Type", "application/json")
            .build();
        this.retryBackoffSpec = Retry.backoff(maxAttempts, Duration.ofSeconds(backoffSeconds))
            .filter(this::isTooManyRequestsException)
            .doBeforeRetry(retrySignal -> log.debug(
                "Waiting {} seconds. Retry #{} of {} after exception: {}",
                backoffSeconds, (retrySignal.totalRetriesInARow()+1), maxAttempts,
                retrySignal.failure().getLocalizedMessage()
            ))
            .onRetryExhaustedThrow((retryBackoffSpec, retrySignal) -> retrySignal.failure());
    }

    @Override
    public CtfdUserResponse createUser(CtfdCreateUserRequest req, String alias) throws CtfdApiException {
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
            return res.bodyToMono(CtfdUserResponse.class).block();
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

    @Override
    public CtfdUserResponse updateUser(CtfdUser ctfdUser) {
        if (ctfdUser == null || ctfdUser.getId() == null) {
            log.debug("passed in CtfdUser is null or its id is null.");
            CtfdApiErrorResponse error = new CtfdApiErrorResponse();
            error.getErrors().setMessage("CtfdUser param must not be null");
            throw new CtfdApiException(error);
        }
        ClientResponse res = this.webClient.patch().uri(API_URI + "/users/" + ctfdUser.getId())
            .body(BodyInserters.fromValue(ctfdUser))
            .exchange()
            .block();
        if (res.statusCode().is2xxSuccessful()) {
            return res.bodyToMono(CtfdUserResponse.class).block();
        }
        CtfdApiErrorResponse error = res.bodyToMono(CtfdApiErrorResponse.class).block();
        throw new CtfdApiException(error);
    }

    @Override
    public CtfdUser updatePassword(CtfdUser ctfdUser) {
        String newPassword = UUID.randomUUID().toString();
        ctfdUser.setPassword(newPassword);
        updateUser(ctfdUser);
        return ctfdUser;
    }

    @Override
    public CtfdUserResponse emailUser(CtfdUser ctfdUser) {
        String emailText = emailTemplate
            .replace("{ctf-name}", ctfdName)
            .replace("{url}", ctfdUrl)
            .replace("{name}", ctfdUser.getName())
            .replace("{password}", ctfdUser.getPassword());
        String uri = API_URI + "/users/" + ctfdUser.getId() + "/email";
        return this.webClient.post().uri(uri)
            .bodyValue(emailText)
            .retrieve()
            .onStatus(
                HttpStatus.BAD_REQUEST::equals,
                response -> {
                    CtfdApiErrorResponse error = response.bodyToMono(CtfdApiErrorResponse.class).block();
                    throw new CtfdApiException(error);
                }
            )
            .bodyToMono(CtfdUserResponse.class)
            .retryWhen(retryBackoffSpec)
            .block();
    }

    public RetryBackoffSpec getRetryBackoffSpec() {
        return retryBackoffSpec;
    }

    private boolean isTooManyRequestsException(final Throwable throwable) {
        return throwable instanceof WebClientResponseException.TooManyRequests;
    }
}
