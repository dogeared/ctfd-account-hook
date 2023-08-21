package io.snyk.devrel.ctfdaccounthook.unit;

import io.snyk.devrel.ctfdaccounthook.Exception.CtfdApiException;
import io.snyk.devrel.ctfdaccounthook.model.CtfdApiErrorResponse;
import io.snyk.devrel.ctfdaccounthook.model.CtfdCreateUserResponse;
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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CtfdApiServiceTest {

    @Mock
    WebClient.Builder webClientBuilder;

    @Mock
    WebClient webClient;

    @Mock
    ClientResponse clientResponse;

    private CtfdApiServiceImpl ctfdApiService;

    @BeforeEach
    public void setup() {
        ctfdApiService = new CtfdApiServiceImpl(webClientBuilder);
        ReflectionTestUtils.setField(ctfdApiService, "ctfdApiToken", "blerg");
        ReflectionTestUtils.setField(ctfdApiService, "ctfdApiBaseUrl", "http://blerg");
        when(webClientBuilder.baseUrl("http://blerg")).thenReturn(webClientBuilder);
        when(webClientBuilder.defaultHeader("Authorization", "Token " + "blerg"))
            .thenReturn(webClientBuilder);
        when(webClientBuilder.defaultHeader("Content-Type", "application/json"))
            .thenReturn(webClientBuilder);
        when(webClientBuilder.build()).thenReturn(webClient);
        ctfdApiService.setup();

        WebClient.RequestBodyUriSpec reqUriSpec = Mockito.mock(WebClient.RequestBodyUriSpec.class);
        when(webClient.post()).thenReturn(reqUriSpec);
        WebClient.RequestBodySpec reqBodySpec = Mockito.mock(WebClient.RequestBodySpec.class);
        when(reqUriSpec.uri(API_URI + "/users")).thenReturn(reqBodySpec);
        WebClient.RequestHeadersSpec reqHeaderSpec = Mockito.mock(WebClient.RequestHeadersSpec.class);
        when(reqBodySpec.body(Mockito.any())).thenReturn(reqHeaderSpec);
        Mono<ClientResponse> clientResponseMono = Mockito.mock(Mono.class);
        when(reqHeaderSpec.exchange()).thenReturn(clientResponseMono);
        when(clientResponseMono.block()).thenReturn(clientResponse);
    }

    @Test
    public void whenClientResponseIsOk_thenReturnCtfdCreateUserResponse() {
        HttpStatusCode httpStatusCode = HttpStatus.OK;
        when(clientResponse.statusCode()).thenReturn(httpStatusCode);
        CtfdCreateUserResponse ctfdCreateUserResponse = new CtfdCreateUserResponse();
        Mono<CtfdCreateUserResponse> resMono = Mono.just(ctfdCreateUserResponse);
        when(clientResponse.bodyToMono(CtfdCreateUserResponse.class)).thenReturn(resMono);

        CtfdCreateUserResponse res = ctfdApiService.createUser("whatevs@example.com", "fun-blue-waf");

        assertThat(res).isEqualTo(resMono.block());
    }

    @Test
    public void whenClientResponseIsNotOk_thenThrowCtfdApiException() {
        HttpStatusCode httpStatusCode = HttpStatus.BAD_REQUEST;
        when(clientResponse.statusCode()).thenReturn(httpStatusCode);
        CtfdApiErrorResponse ctfdApiErrorResponse = new CtfdApiErrorResponse();
        Mono<CtfdApiErrorResponse> resMono = Mono.just(ctfdApiErrorResponse);
        when(clientResponse.bodyToMono(CtfdApiErrorResponse.class)).thenReturn(resMono);

        try {
            ctfdApiService.createUser("whatevs@example.com", "fun-blue-waf");
            fail();
        } catch (CtfdApiException e) {
            assertThat(e.getCtfdApiError()).isEqualTo(resMono.block());
        }
    }
}
