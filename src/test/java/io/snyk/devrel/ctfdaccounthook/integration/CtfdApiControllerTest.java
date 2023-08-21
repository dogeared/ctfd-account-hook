package io.snyk.devrel.ctfdaccounthook.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.snyk.devrel.ctfdaccounthook.CtfdAccountHookApplication;
import io.snyk.devrel.ctfdaccounthook.Exception.CtfdApiException;
import io.snyk.devrel.ctfdaccounthook.controller.CtfdApiController;
import io.snyk.devrel.ctfdaccounthook.model.CtfdApiErrorResponse;
import io.snyk.devrel.ctfdaccounthook.model.CtfdCreateUserRequest;
import io.snyk.devrel.ctfdaccounthook.model.CtfdCreateUserResponse;
import io.snyk.devrel.ctfdaccounthook.service.AliasService;
import io.snyk.devrel.ctfdaccounthook.service.CtfdApiService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = CtfdAccountHookApplication.class)
@TestPropertySource(properties = {"api.auth.header-name=X-TEST-HEADER","api.auth.token=blerg"})
public class CtfdApiControllerTest {

    @InjectMocks
    CtfdApiController ctfdApiController;

    @MockBean
    private AliasService aliasService;

    @MockBean
    private CtfdApiService ctfdApiService;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    ObjectMapper mapper;

    private MockMvc mvc;

    private static final String CTFD_USERS_ENDPOINT = "/api/v1/users";

    @BeforeEach
    public void setup() {
        mvc = MockMvcBuilders.webAppContextSetup(context)
            .apply(springSecurity())
            .build();
    }

    @Test
    public void whenCreateUserRequest_thenSuccess() throws Exception {
        String alias = "abc-def-ghi";
        when(aliasService.getAlias()).thenReturn(alias);

        CtfdCreateUserRequest reqUser = new CtfdCreateUserRequest();
        reqUser.setEmail("blarg@example.com");

        CtfdCreateUserResponse expected = new CtfdCreateUserResponse();
        expected.setSuccess("success");

        when(ctfdApiService.createUser(reqUser.getEmail(), alias)).thenReturn(expected);

        MockHttpServletResponse response = mvc.perform(
            post(CTFD_USERS_ENDPOINT)
                .header("X-TEST-HEADER", "blerg")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(reqUser))
        )
        .andExpect(status().isOk()).andReturn().getResponse();

        CtfdCreateUserResponse actual = mapper.readValue(response.getContentAsString(), CtfdCreateUserResponse.class);
        assertThat(actual.getSuccess()).isEqualTo(expected.getSuccess());
    }

    @Test
    public void whenCreateUserRequest_thenFail_Email() throws Exception {
        String alias = "abc-def-ghi";
        when(aliasService.getAlias()).thenReturn(alias);

        CtfdCreateUserRequest reqUser = new CtfdCreateUserRequest();
        reqUser.setEmail("blarg@example.com");

        String errorString = """
        {"errors": {
            "email": [
                "email already exists"
            ]
        }}
        """;

        CtfdApiErrorResponse expected = mapper.readValue(errorString, CtfdApiErrorResponse.class);
        CtfdApiException exception = new CtfdApiException(expected);

        when(ctfdApiService.createUser(reqUser.getEmail(), alias)).thenThrow(exception);

        MockHttpServletResponse response = mvc.perform(
            post(CTFD_USERS_ENDPOINT)
                .header("X-TEST-HEADER", "blerg")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(reqUser))
        )
        .andExpect(status().isBadRequest()).andReturn().getResponse();

        CtfdApiErrorResponse actual = mapper.readValue(response.getContentAsString(), CtfdApiErrorResponse.class);
        assertThat(actual.getErrors().getEmail()).isEqualTo(expected.getErrors().getEmail());
    }

    @Test
    public void whenCreateUserRequest_thenFail_Name() throws Exception {
        String alias = "abc-def-ghi";
        when(aliasService.getAlias()).thenReturn(alias);

        CtfdCreateUserRequest reqUser = new CtfdCreateUserRequest();
        reqUser.setEmail("blarg@example.com");

        String errorString = """
        {"errors": {
            "name": [
                "name already exists"
            ]
        }}
        """;

        CtfdApiErrorResponse expected = mapper.readValue(errorString, CtfdApiErrorResponse.class);
        CtfdApiException exception = new CtfdApiException(expected);

        when(ctfdApiService.createUser(reqUser.getEmail(), alias)).thenThrow(exception);

        MockHttpServletResponse response = mvc.perform(
            post(CTFD_USERS_ENDPOINT)
                .header("X-TEST-HEADER", "blerg")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(reqUser))
            )
            .andExpect(status().isBadRequest()).andReturn().getResponse();

        CtfdApiErrorResponse actual = mapper.readValue(response.getContentAsString(), CtfdApiErrorResponse.class);
        assertThat(actual.getErrors().getName()).isEqualTo(expected.getErrors().getName());
    }

}
