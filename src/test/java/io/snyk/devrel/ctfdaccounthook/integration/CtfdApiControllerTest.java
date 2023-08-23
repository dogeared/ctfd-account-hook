package io.snyk.devrel.ctfdaccounthook.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.snyk.devrel.ctfdaccounthook.CtfdAccountHookApplication;
import io.snyk.devrel.ctfdaccounthook.Exception.CtfdApiException;
import io.snyk.devrel.ctfdaccounthook.controller.CtfdApiController;
import io.snyk.devrel.ctfdaccounthook.model.CtfdApiErrorResponse;
import io.snyk.devrel.ctfdaccounthook.model.CtfdCreateUserRequest;
import io.snyk.devrel.ctfdaccounthook.model.CtfdCreateUserResponse;
import io.snyk.devrel.ctfdaccounthook.model.CtfdUserPaginatedResponse;
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

import static io.snyk.devrel.ctfdaccounthook.integration.CtfdApiControllerTest.AFFILIATION;
import static io.snyk.devrel.ctfdaccounthook.integration.CtfdApiControllerTest.HEADER;
import static io.snyk.devrel.ctfdaccounthook.integration.CtfdApiControllerTest.TOKEN_VALUE;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = CtfdAccountHookApplication.class)
@TestPropertySource(properties = {
    "api.auth.header-name=" + HEADER, "api.auth.token=" + TOKEN_VALUE,
    "alias.retries=1", "ctfd.api.affiliation=" + AFFILIATION
})
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
    private static final String SUCCESS = "success";
    public static final String AFFILIATION = "fetch";
    public static final String HEADER = "X-TEST-HEADER";
    public static final String TOKEN_VALUE = "blerg";

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
        expected.setSuccess(SUCCESS);

        when(ctfdApiService.createUser(
            argThat(user -> user.getEmail().equals(reqUser.getEmail())),
            eq(alias))
        ).thenReturn(expected);

        MockHttpServletResponse response = mvc.perform(
            post(CTFD_USERS_ENDPOINT)
                .header(HEADER, TOKEN_VALUE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(reqUser))
        )
        .andExpect(status().isOk()).andReturn().getResponse();

        CtfdCreateUserResponse actual = mapper.readValue(response.getContentAsString(), CtfdCreateUserResponse.class);
        assertThat(actual.getSuccess()).isEqualTo(expected.getSuccess());
    }

    @Test
    public void whenCreateUserRequest_WithNotify_thenSuccess() throws Exception {
        String alias = "abc-def-ghi";
        when(aliasService.getAlias()).thenReturn(alias);

        CtfdCreateUserRequest reqUser = new CtfdCreateUserRequest();
        reqUser.setEmail("blarg@example.com");
        reqUser.setNotify(true);

        CtfdCreateUserResponse expected = new CtfdCreateUserResponse();
        expected.setSuccess(SUCCESS);

        when(ctfdApiService.createUser(
            argThat(user -> user.getEmail().equals(reqUser.getEmail()) && user.getNotify() == true),
            eq(alias))
        ).thenReturn(expected);

        MockHttpServletResponse response = mvc.perform(
            post(CTFD_USERS_ENDPOINT)
                .header(HEADER, TOKEN_VALUE)
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

        when(ctfdApiService.createUser(
            argThat(user -> user.getEmail().equals(reqUser.getEmail())),
            eq(alias))
        ).thenThrow(exception);

        MockHttpServletResponse response = mvc.perform(
            post(CTFD_USERS_ENDPOINT)
                .header(HEADER, TOKEN_VALUE)
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
                "Attempted to find a unique alias 2 times and failed."
            ]
        }}
        """;

        CtfdApiErrorResponse expected = mapper.readValue(errorString, CtfdApiErrorResponse.class);
        CtfdApiException exception = new CtfdApiException(expected);

        when(ctfdApiService.createUser(
            argThat(user -> user.getEmail().equals(reqUser.getEmail())),
            eq(alias))
        ).thenThrow(exception);

        MockHttpServletResponse response = mvc.perform(
            post(CTFD_USERS_ENDPOINT)
                .header(HEADER, TOKEN_VALUE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(reqUser))
            )
            .andExpect(status().isBadRequest()).andReturn().getResponse();

        CtfdApiErrorResponse actual = mapper.readValue(response.getContentAsString(), CtfdApiErrorResponse.class);
        assertThat(actual.getErrors().getName()).isEqualTo(expected.getErrors().getName());
    }

    @Test
    public void whenCreateUserRequest_thenFail_ThenSucceed_Name() throws Exception {
        String alias = "abc-def-ghi";
        when(aliasService.getAlias()).thenReturn(alias);

        CtfdCreateUserRequest reqUser = new CtfdCreateUserRequest();
        reqUser.setEmail("blarg@example.com");

        String errorString = """
        {
            "errors": {
                "name": [
                    "User name has already been taken"
                ]
            }
        }
        """;

        CtfdApiErrorResponse expectedFirst = mapper.readValue(errorString, CtfdApiErrorResponse.class);
        CtfdApiException exception = new CtfdApiException(expectedFirst);

        CtfdCreateUserResponse expectedSecond = new CtfdCreateUserResponse();
        expectedSecond.setSuccess(SUCCESS);

        when(ctfdApiService.createUser(
            argThat(user -> user.getEmail().equals(reqUser.getEmail())),
            eq(alias))
        )
        .thenThrow(exception)
        .thenReturn(expectedSecond);

        MockHttpServletResponse response = mvc.perform(
            post(CTFD_USERS_ENDPOINT)
                .header(HEADER, TOKEN_VALUE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(reqUser))
            )
            .andExpect(status().isOk()).andReturn().getResponse();

        CtfdCreateUserResponse actual = mapper.readValue(response.getContentAsString(), CtfdCreateUserResponse.class);
        assertThat(actual.getSuccess()).isEqualTo(expectedSecond.getSuccess());
    }

    @Test
    public void whenGetUsers_thenSuccess() throws Exception {

        CtfdUserPaginatedResponse expected = new CtfdUserPaginatedResponse();
        expected.setSuccess(SUCCESS);
        when(ctfdApiService.getUsersByAffiliation(AFFILIATION, 1)).thenReturn(expected);

        MockHttpServletResponse response = mvc.perform(
            get(CTFD_USERS_ENDPOINT + "?page=1&affiliation=" + AFFILIATION)
                .header(HEADER, TOKEN_VALUE)
                .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isOk()).andReturn().getResponse();

        CtfdUserPaginatedResponse actual =
            mapper.readValue(response.getContentAsString(), CtfdUserPaginatedResponse.class);
        assertThat(actual.getSuccess()).isEqualTo(expected.getSuccess());
    }
}
