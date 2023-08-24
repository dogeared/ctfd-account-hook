package io.snyk.devrel.ctfdaccounthook.unit;

import io.snyk.devrel.ctfdaccounthook.Exception.CtfdApiException;
import io.snyk.devrel.ctfdaccounthook.model.CtfdApiErrorResponse;
import io.snyk.devrel.ctfdaccounthook.model.CtfdCreateUserRequest;
import io.snyk.devrel.ctfdaccounthook.model.CtfdUser;
import io.snyk.devrel.ctfdaccounthook.model.CtfdUserResponse;
import io.snyk.devrel.ctfdaccounthook.model.CtfdUserPaginatedResponse;
import io.snyk.devrel.ctfdaccounthook.service.CtfdApiServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import static io.snyk.devrel.ctfdaccounthook.service.CtfdApiServiceImpl.API_URI;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
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

    private CtfdApiServiceImpl ctfdApiService;

    private CtfdCreateUserRequest ctfdCreateUserRequest;

    private static final String BASE_URL = "http://blerg";
    private static final String TOKEN_VALUE = "blerg";
    private static final String AFFILIATION = "fetch";
    private static final Integer PAGE = 20;
    private static final String USERS_ENDPOINT = "/users";

    @BeforeEach
    public void setup() {
        ctfdCreateUserRequest = new CtfdCreateUserRequest();
        ctfdCreateUserRequest.setEmail("whatevs@example.com");

        ctfdApiService = new CtfdApiServiceImpl(webClientBuilder);
        ReflectionTestUtils.setField(ctfdApiService, "ctfdApiToken", TOKEN_VALUE);
        ReflectionTestUtils.setField(ctfdApiService, "ctfdApiBaseUrl", BASE_URL);
        ReflectionTestUtils.setField(ctfdApiService, "affiliation", AFFILIATION);
    }

    public void generalSetup() {
        when(webClientBuilder.baseUrl(BASE_URL)).thenReturn(webClientBuilder);
        when(webClientBuilder.defaultHeader("Authorization", "Token " + TOKEN_VALUE))
            .thenReturn(webClientBuilder);
        when(webClientBuilder.defaultHeader("Content-Type", "application/json"))
            .thenReturn(webClientBuilder);
        when(webClientBuilder.build()).thenReturn(webClient);
        ctfdApiService.setup();

        Mono<ClientResponse> clientResponseMono = Mockito.mock(Mono.class);
        when(reqHeaderSpec.exchange()).thenReturn(clientResponseMono);
        when(clientResponseMono.block()).thenReturn(clientResponse);
    }

    public void setupCreateUser() {
        WebClient.RequestBodyUriSpec reqUriSpec = Mockito.mock(WebClient.RequestBodyUriSpec.class);
        when(webClient.post()).thenReturn(reqUriSpec);
        WebClient.RequestBodySpec reqBodySpec = Mockito.mock(WebClient.RequestBodySpec.class);
        when(reqUriSpec.uri(API_URI + USERS_ENDPOINT)).thenReturn(reqBodySpec);
        when(reqBodySpec.body(any())).thenReturn(reqHeaderSpec);
    }

    public void setupGetUsers() {
        when(webClient.get()).thenReturn(reqHeaderUriSpec);
    }

    public void setupPatchUser() {
        WebClient.RequestBodyUriSpec reqUriSpec = Mockito.mock(WebClient.RequestBodyUriSpec.class);
        when(webClient.patch()).thenReturn(reqUriSpec);
        WebClient.RequestBodySpec reqBodySpec = Mockito.mock(WebClient.RequestBodySpec.class);
        when(reqUriSpec.uri(API_URI + USERS_ENDPOINT + "/1")).thenReturn(reqBodySpec);
        when(reqBodySpec.body(any())).thenReturn(reqHeaderSpec);
    }

    @Test
    public void whenClientResponseIsOk_thenReturnCtfdCreateUserResponse() {
        generalSetup();
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
}
