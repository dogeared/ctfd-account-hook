package dev.dogeared.ctfdaccounthook.unit;

import dev.dogeared.ctfdaccounthook.model.CtfdCreateUserRequest;
import dev.dogeared.ctfdaccounthook.Exception.CtfdApiException;
import dev.dogeared.ctfdaccounthook.model.CtfdApiErrorResponse;
import dev.dogeared.ctfdaccounthook.model.CtfdUser;
import dev.dogeared.ctfdaccounthook.model.CtfdUserPaginatedResponse;
import dev.dogeared.ctfdaccounthook.model.CtfdUserResponse;
import dev.dogeared.ctfdaccounthook.service.CtfdApiServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Mono;

import static dev.dogeared.ctfdaccounthook.service.CtfdApiServiceImpl.API_URI;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CtfdApiServiceTest {

    @Mock
    WebClient.Builder webClientBuilder;

    @Mock
    WebClient webClient;

    @Mock
    ClientResponse clientResponse;

    @Mock
    WebClient.RequestHeadersUriSpec reqHeaderUriSpec;

    @Mock
    WebClient.RequestHeadersSpec reqHeaderSpec;

    @Mock
    WebClient.RequestBodyUriSpec reqUriSpec;

    @Mock
    WebClient.RequestBodySpec reqBodySpec;

    @Mock
    WebClient.ResponseSpec resSpec;

    private CtfdApiServiceImpl ctfdApiService;

    private CtfdCreateUserRequest ctfdCreateUserRequest;

    private static final String BASE_URL = "http://blerg";
    private static final String TOKEN_VALUE = "blerg";
    private static final String AFFILIATION = "fetch";
    private static final Integer PAGE = 20;
    private static final String USERS_ENDPOINT = "/users";

    private static final String EMAIL_TEMPLATE = """
    {"text": "A new account has been created for you for {ctf-name} at {url}\\n\\nUsername: {name}\\nPassword: {password}"}
    """;

    private static final String CTFD_NAME = "Fetch It!";
    private static final String CTFD_URL = "http://fetchit";

    @BeforeEach
    public void setup() {
        ctfdCreateUserRequest = new CtfdCreateUserRequest();
        ctfdCreateUserRequest.setEmail("whatevs@example.com");

        ctfdApiService = new CtfdApiServiceImpl(webClientBuilder);
        ReflectionTestUtils.setField(ctfdApiService, "ctfdApiToken", TOKEN_VALUE);
        ReflectionTestUtils.setField(ctfdApiService, "ctfdApiBaseUrl", BASE_URL);
        ReflectionTestUtils.setField(ctfdApiService, "affiliation", AFFILIATION);
        ReflectionTestUtils.setField(ctfdApiService, "emailTemplate", EMAIL_TEMPLATE);
        ReflectionTestUtils.setField(ctfdApiService, "ctfdName", CTFD_NAME);
        ReflectionTestUtils.setField(ctfdApiService, "ctfdUrl", CTFD_URL);
        ReflectionTestUtils.setField(ctfdApiService, "maxAttempts", 2);
        ReflectionTestUtils.setField(ctfdApiService, "backoffSeconds", 60);
    }

    public void generalSetup() {
        when(webClientBuilder.baseUrl(BASE_URL)).thenReturn(webClientBuilder);
        when(webClientBuilder.defaultHeader("Authorization", "Token " + TOKEN_VALUE))
            .thenReturn(webClientBuilder);
        when(webClientBuilder.defaultHeader("Content-Type", "application/json"))
            .thenReturn(webClientBuilder);
        when(webClientBuilder.build()).thenReturn(webClient);
        ctfdApiService.setup();
    }

    public void exchangeSetup() {
        Mono<ClientResponse> clientResponseMono = mock(Mono.class);
        when(reqHeaderSpec.exchange()).thenReturn(clientResponseMono);
        when(clientResponseMono.block()).thenReturn(clientResponse);
    }

    public void setupCreateUser() {
        when(webClient.post()).thenReturn(reqUriSpec);
        when(reqUriSpec.uri(API_URI + USERS_ENDPOINT)).thenReturn(reqBodySpec);
        when(reqBodySpec.body(any())).thenReturn(reqHeaderSpec);
    }

    public void setupGetUsers() {
        when(webClient.get()).thenReturn(reqHeaderUriSpec);
    }

    public void setupPatchUser() {
        when(webClient.patch()).thenReturn(reqUriSpec);
        when(reqUriSpec.uri(API_URI + USERS_ENDPOINT + "/1")).thenReturn(reqBodySpec);
        when(reqBodySpec.body(any())).thenReturn(reqHeaderSpec);
    }

    public void emailUserSetup(Integer id) {
        when(webClient.post()).thenReturn(reqUriSpec);
        when(reqUriSpec.uri(API_URI + USERS_ENDPOINT + "/" + id + "/email")).thenReturn(reqBodySpec);
    }

    @Test
    public void whenClientResponseIsOk_thenReturnCtfdCreateUserResponse() {
        generalSetup();
        exchangeSetup();
        setupCreateUser();
        HttpStatusCode httpStatusCode = HttpStatus.OK;
        when(clientResponse.statusCode()).thenReturn(httpStatusCode);
        CtfdUserResponse ctfdUserResponse = new CtfdUserResponse();
        Mono<CtfdUserResponse> resMono = Mono.just(ctfdUserResponse);
        when(clientResponse.bodyToMono(CtfdUserResponse.class)).thenReturn(resMono);

        CtfdUserResponse res = ctfdApiService.createUser(ctfdCreateUserRequest, "fun-blue-waf");

        assertThat(res).isEqualTo(resMono.block());
    }

    @Test
    public void whenClientResponseIsNotOk_thenThrowCtfdApiException() {
        generalSetup();
        exchangeSetup();
        setupCreateUser();
        HttpStatusCode httpStatusCode = HttpStatus.BAD_REQUEST;
        when(clientResponse.statusCode()).thenReturn(httpStatusCode);
        CtfdApiErrorResponse ctfdApiErrorResponse = new CtfdApiErrorResponse();
        Mono<CtfdApiErrorResponse> resMono = Mono.just(ctfdApiErrorResponse);
        when(clientResponse.bodyToMono(CtfdApiErrorResponse.class)).thenReturn(resMono);

        try {
            ctfdApiService.createUser(ctfdCreateUserRequest, "fun-blue-waf");
            fail();
        } catch (CtfdApiException e) {
            assertThat(e.getCtfdApiError()).isEqualTo(resMono.block());
        }
    }

    @Test
    public void whenGetUsersByAffiliation_Default_Success() {
        generalSetup();
        exchangeSetup();
        setupGetUsers();
        when(reqHeaderUriSpec.uri(API_URI + USERS_ENDPOINT + "?page=1&affiliation=" + AFFILIATION))
            .thenReturn(reqHeaderSpec);
        HttpStatusCode httpStatusCode = HttpStatus.OK;
        when(clientResponse.statusCode()).thenReturn(httpStatusCode);
        CtfdUserPaginatedResponse expected = new CtfdUserPaginatedResponse();
        Mono<CtfdUserPaginatedResponse> resMono = Mono.just(expected);
        when(clientResponse.bodyToMono(CtfdUserPaginatedResponse.class)).thenReturn(resMono);

        CtfdUserPaginatedResponse actual = ctfdApiService.getUsersByAffiliation(null, null);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void whenGetUsersByAffiliation_WithPage_Fail() {
        generalSetup();
        exchangeSetup();
        setupGetUsers();
        when(reqHeaderUriSpec.uri(API_URI + USERS_ENDPOINT + "?page=" + PAGE + "&affiliation=" + AFFILIATION))
            .thenReturn(reqHeaderSpec);
        HttpStatusCode httpStatusCode = HttpStatus.BAD_REQUEST;
        when(clientResponse.statusCode()).thenReturn(httpStatusCode);

        try {
            ctfdApiService.getUsersByAffiliation(null, PAGE);
            fail();
        } catch (CtfdApiException e) {
            CtfdApiErrorResponse actual = e.getCtfdApiError();
            assertThat(actual.getErrors().getMessage())
                .isEqualTo("Unable to get page " + PAGE + " for affiliation: " + AFFILIATION);
        }
    }

    @Test
    public void when_Patch_CtfdUser_IsNull_Fail() {
        try {
            ctfdApiService.updateUser(null);
            fail();
        } catch (CtfdApiException e) {
            CtfdApiErrorResponse actual = e.getCtfdApiError();
            assertThat(actual.getErrors().getMessage()).isEqualTo("CtfdUser param must not be null");
        }
    }

    @Test
    public void when_Patch_CtfdUser_Id_IsNull_Fail() {
        try {
            CtfdUser user = new CtfdUser();
            assertThat(user.getId()).isNull();
            ctfdApiService.updateUser(user);
            fail();
        } catch (CtfdApiException e) {
            CtfdApiErrorResponse actual = e.getCtfdApiError();
            assertThat(actual.getErrors().getMessage()).isEqualTo("CtfdUser param must not be null");
        }
    }

    @Test
    public void when_Patch_CtfdUser_Success() {
        generalSetup();
        exchangeSetup();
        setupPatchUser();
        HttpStatusCode httpStatusCode = HttpStatus.OK;
        when(clientResponse.statusCode()).thenReturn(httpStatusCode);
        CtfdUserResponse expected = new CtfdUserResponse();
        Mono<CtfdUserResponse> resMono = Mono.just(expected);
        when(clientResponse.bodyToMono(CtfdUserResponse.class)).thenReturn(resMono);

        CtfdUser ctfdUser = new CtfdUser();
        ctfdUser.setId(1);
        CtfdUserResponse actual = ctfdApiService.updateUser(ctfdUser);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void when_Patch_CtfdUser_Fail() {
        generalSetup();
        exchangeSetup();
        setupPatchUser();
        HttpStatusCode httpStatusCode = HttpStatus.BAD_REQUEST;
        when(clientResponse.statusCode()).thenReturn(httpStatusCode);
        CtfdApiErrorResponse ctfdApiErrorResponse = new CtfdApiErrorResponse();
        Mono<CtfdApiErrorResponse> expected = Mono.just(ctfdApiErrorResponse);
        when(clientResponse.bodyToMono(CtfdApiErrorResponse.class)).thenReturn(expected);

        CtfdUser ctfdUser = new CtfdUser();
        ctfdUser.setId(1);
        try {
            ctfdApiService.updateUser(ctfdUser);
            fail();
        } catch (CtfdApiException actual) {
            assertThat(actual.getCtfdApiError()).isEqualTo(expected.block());
        }
    }

    @Test
    public void whenUpdatePassword_Success() {
        generalSetup();
        exchangeSetup();
        setupPatchUser();
        HttpStatusCode httpStatusCode = HttpStatus.OK;
        when(clientResponse.statusCode()).thenReturn(httpStatusCode);
        CtfdUserResponse ctfdUserResponse = new CtfdUserResponse();
        Mono<CtfdUserResponse> resMono = Mono.just(ctfdUserResponse);
        when(clientResponse.bodyToMono(CtfdUserResponse.class)).thenReturn(resMono);

        CtfdUser ctfdUser = new CtfdUser();
        ctfdUser.setId(1);
        assertThat(ctfdUser.getPassword()).isNull();
        CtfdUser actual = ctfdApiService.updatePassword(ctfdUser);
        assertThat(actual.getPassword()).isNotEmpty();
    }

    @Test
    public void when_EmailUser_Success() {
        generalSetup();

        CtfdUser ctfdUser = new CtfdUser();
        ctfdUser.setId(1);
        ctfdUser.setName("name");
        ctfdUser.setPassword("password");

        CtfdUserResponse expected = new CtfdUserResponse();
        expected.setUser(ctfdUser);
        expected.setSuccess("success");

        Mono<CtfdUserResponse> resMono = Mono.just(expected);
        Mono<CtfdUserResponse> resMonoSpy = spy(resMono);

        emailUserSetup(1);

        String emailText = EMAIL_TEMPLATE
            .replace("{ctf-name}", CTFD_NAME)
            .replace("{url}", CTFD_URL)
            .replace("{name}", ctfdUser.getName())
            .replace("{password}", ctfdUser.getPassword());
        String uri = API_URI + "/users/" + ctfdUser.getId() + "/email";

        when(reqUriSpec.uri(API_URI + "/users/1/email")).thenReturn(reqBodySpec);
        when(reqBodySpec.bodyValue(emailText)).thenReturn(reqHeaderSpec);
        when(reqHeaderSpec.retrieve()).thenReturn(resSpec);
        when(resSpec.onStatus(any(), any())).thenReturn(resSpec);
        when(resSpec.bodyToMono(CtfdUserResponse.class)).thenReturn(resMonoSpy);
        when(resMonoSpy.retryWhen(ctfdApiService.getRetryBackoffSpec())).thenReturn(resMonoSpy);
        when(resMonoSpy.block()).thenReturn(expected);

        CtfdUserResponse actual = ctfdApiService.emailUser(ctfdUser);
        assertThat(actual).isEqualTo(expected);
    }

//    @Test
//    public void t() {
//        generalSetup();
//        exchangeSetup();
//        setupGetUsers();
//        when(reqHeaderUriSpec.uri(API_URI + USERS_ENDPOINT + "?page=1&affiliation=" + AFFILIATION))
//            .thenReturn(reqHeaderSpec);
//        HttpStatusCode httpStatusCode = HttpStatus.OK;
//        when(clientResponse.statusCode()).thenReturn(httpStatusCode);
//        // TODO - need to add or mock values here
//        CtfdUserPaginatedResponse expected = new CtfdUserPaginatedResponse();
//        Mono<CtfdUserPaginatedResponse> resMono = Mono.just(expected);
//        when(clientResponse.bodyToMono(CtfdUserPaginatedResponse.class)).thenReturn(resMono);
//
//        SseEmitter mockSse = mock(SseEmitter.class);
//
//        ctfdApiService.updateAndEmail(mockSse, AFFILIATION);
//
//
//    }
}
