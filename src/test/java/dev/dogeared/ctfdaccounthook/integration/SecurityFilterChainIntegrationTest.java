package dev.dogeared.ctfdaccounthook.integration;

import dev.dogeared.ctfdaccounthook.CtfdAccountHookApplication;
import dev.dogeared.ctfdaccounthook.model.ApiKey;
import dev.dogeared.ctfdaccounthook.service.ApiKeyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static dev.dogeared.ctfdaccounthook.integration.SecurityFilterChainIntegrationTest.HEADER;
import static dev.dogeared.ctfdaccounthook.integration.SecurityFilterChainIntegrationTest.TOKEN_VALUE;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = CtfdAccountHookApplication.class)
@TestPropertySource(properties = {"api.auth.header-name=" + HEADER})
public class SecurityFilterChainIntegrationTest {

    private static final String HELLO_ENDPOINT = "/hello-world";
    public static final String HEADER = "X-TEST-HEADER";
    public static final String TOKEN_VALUE = "blerg";

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private ApiKeyService apiKeyService;

    private MockMvc mvc;

    @BeforeEach
    public void setup() {
        mvc = MockMvcBuilders.webAppContextSetup(context)
            .apply(springSecurity())
            .build();
    }

    @Test
    public void whenAnonymousAnyEndpoint_thenIsUnauthorized() throws Exception {
        mvc.perform(get(HELLO_ENDPOINT))
            .andExpect(status().isUnauthorized());
    }

    @Test
    public void whenValidApiKeyAnyEndpoint_thenAuthorized() throws Exception {
        apiKeyService.generateApiKey(TOKEN_VALUE, 1);
        mvc.perform(get(HELLO_ENDPOINT).header(HEADER, TOKEN_VALUE))
            .andExpect(status().isOk());
    }
}
