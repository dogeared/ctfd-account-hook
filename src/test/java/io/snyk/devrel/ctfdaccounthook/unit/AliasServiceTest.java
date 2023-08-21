package io.snyk.devrel.ctfdaccounthook.unit;

import io.snyk.devrel.ctfdaccounthook.service.AliasServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Random;

import static io.snyk.devrel.ctfdaccounthook.service.AliasService.ADJECTIVES_DICT;
import static io.snyk.devrel.ctfdaccounthook.service.AliasService.COLORS_DICT;
import static io.snyk.devrel.ctfdaccounthook.service.AliasService.CYBER_SECURITY_DICT;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class AliasServiceTest {

    @Mock
    Random random;
    private AliasServiceImpl aliasService;

    @BeforeEach
    public void setup() {
        aliasService = new AliasServiceImpl();
        ReflectionTestUtils.setField(aliasService, "random", random);
    }

    @Test
    public void whenZeroIndexFromRandom_ThenExpectedAlias() {
        String adjective = ADJECTIVES_DICT[0];
        String color = COLORS_DICT[0];
        String cyber = CYBER_SECURITY_DICT[0];

        when(random.nextInt(ADJECTIVES_DICT.length)).thenReturn(0);
        when(random.nextInt(COLORS_DICT.length)).thenReturn(0);
        when(random.nextInt(CYBER_SECURITY_DICT.length)).thenReturn(0);

        String alias = aliasService.getAlias();

        assertThat(alias).isEqualTo(String.format("%s-%s-%s", adjective, color, cyber));
    }
}
