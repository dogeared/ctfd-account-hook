package io.snyk.devrel.ctfdaccounthook.integration;

import io.snyk.devrel.ctfdaccounthook.CtfdAccountHookApplication;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = CtfdAccountHookApplication.class)
@TestPropertySource(properties = {"api.auth.header-name=X-TEST-HEADER","api.auth.token=blerg", "alias.retries=1"})
public class SecurityFilterChainIntegrationTest {

    private static final String HELLO_ENDPOINT = "/hello-world";

    @Autowired
    private WebApplicationContext context;

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
        mvc.perform(get(HELLO_ENDPOINT).header("X-TEST-HEADER", "blerg"))
            .andExpect(status().isOk());
    }
}
