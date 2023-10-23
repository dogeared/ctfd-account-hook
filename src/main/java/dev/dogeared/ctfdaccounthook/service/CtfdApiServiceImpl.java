package dev.dogeared.ctfdaccounthook.service;

import dev.dogeared.ctfdaccounthook.Exception.CtfdApiException;
import dev.dogeared.ctfdaccounthook.annotation.LogExecutionTime;
import dev.dogeared.ctfdaccounthook.model.CtfdApiErrorResponse;
import dev.dogeared.ctfdaccounthook.model.CtfdCreateUserRequest;
import dev.dogeared.ctfdaccounthook.model.CtfdUpdateAndEmailResponse;
import dev.dogeared.ctfdaccounthook.model.CtfdUser;
import dev.dogeared.ctfdaccounthook.model.CtfdUserPaginatedResponse;
import dev.dogeared.ctfdaccounthook.model.CtfdUserResponse;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.util.retry.Retry;
import reactor.util.retry.RetryBackoffSpec;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalTime;
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

    @Value("#{ @environment['ctfd.api.notify-override'] ?: false }")
    private Boolean notifyOverride;

    private final WebClient.Builder webClientBuilder;
    private WebClient webClient;

    public static final String API_URI = "/api/v1";

    private static final Logger log = LoggerFactory.getLogger(CtfdApiServiceImpl.class);

    @Autowired
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
        String notify = (notifyOverride || req.getNotify()) ? "?notify=true" : "";
        String uri = API_URI + "/users" + notify;
        ClientResponse res = this.webClient.post().uri(uri)
            .body(BodyInserters.fromValue(ctfdUser))
            .exchange()
            .block();
        if (res.statusCode().is2xxSuccessful()) {
            log.debug("Created new user with alias: {}. Notify is: {}", alias, (notifyOverride || req.getNotify()));
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
    public CtfdUser getUserByName(String name) {
        String uri = API_URI + "/users?field=name&q=" + name;
        ClientResponse res = this.webClient.get().uri(uri).exchange().block();
        if (!res.statusCode().is2xxSuccessful()) {
            CtfdApiErrorResponse error = new CtfdApiErrorResponse();
            error.getErrors().setMessage(String.format("Unable to get user for name: %s", name));
            throw new CtfdApiException(error);
        }
        CtfdUserPaginatedResponse ret = res.bodyToMono(CtfdUserPaginatedResponse.class).block();
        if (ret.getMeta().getPagination().getTotal() != 1) {
            CtfdApiErrorResponse error = new CtfdApiErrorResponse();
            error.getErrors().setMessage(String.format(
                "Wrong number of records found for name: %s. Should be exactly 1, not: %d",
                name, ret.getMeta().getPagination().getTotal()
            ));
            throw new CtfdApiException(error);
        }
        return ret.getData()[0];
    }

    @Async
    @Override
    @LogExecutionTime
    public void updateAndEmail(SseEmitter emitter, String affiliation) {
        Integer page = 1;
        int processed = 0;

        do {
            try {
                CtfdUserPaginatedResponse ctfdUserResponse =
                    getUsersByAffiliation(affiliation, page);
                for (CtfdUser ctfdUser : ctfdUserResponse.getData()) {
                    SseEmitter.SseEventBuilder  event = SseEmitter.event()
                        .data("Processing - " + ctfdUser.getId() + " - " + LocalTime.now().toString())
                        .id(String.valueOf(ctfdUser.getId()))
                        .name(ctfdUser.getId() + " - " + ctfdUser.getName());
                    emitter.send(event);

                    log.debug("Processing user id: {}, name: {}", ctfdUser.getId(), ctfdUser.getName());
                    ctfdUser = updatePassword(ctfdUser);
                    log.debug("Password updated for user id: {}", ctfdUser.getId());
                    emailUser(ctfdUser);
                    log.debug("Email sent for user id: {}", ctfdUser.getId());

                    event = SseEmitter.event()
                        .data("Finished Processing - " + ctfdUser.getId() + " - " + LocalTime.now().toString())
                        .id(String.valueOf(ctfdUser.getId()))
                        .name(ctfdUser.getId() + " - " + ctfdUser.getName());
                    emitter.send(event);
                }
                page = ctfdUserResponse.getMeta().getPagination().getNext();
                processed += ctfdUserResponse.getData().length;

                String processedMessage = "Processed a total of: " + processed + " users.";
                if (page != null) {
                    processedMessage += " Going to page " + page + " of users.";
                }
                emitter.send(processedMessage);
            } catch (CtfdApiException | IOException e) {
                log.error("Failure while update/email operation: {}", e.getMessage());
                emitter.completeWithError(e);
                return;
            }
        } while (page != null);
        try {
            emitter.send(new CtfdUpdateAndEmailResponse(processed));
            emitter.complete();
        } catch (IOException e) {
            log.error("Emitter send failed: {}", e.getMessage());
            emitter.completeWithError(e);
        }
    }

    // TODO - gross - heroku workaround
    @Async
    @Override
    public void emitterHeartBeat(SseEmitter emitter) {
        try {
            do {
                emitter.send("beat");
                Thread.sleep(5000);
            } while (true);
        } catch (Exception e) {
            log.debug("exception during emitter: {}", e.getMessage());
        }
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
}
