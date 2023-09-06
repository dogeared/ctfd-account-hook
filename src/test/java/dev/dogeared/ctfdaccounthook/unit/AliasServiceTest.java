package dev.dogeared.ctfdaccounthook.unit;

import dev.dogeared.ctfdaccounthook.service.AliasService;
import dev.dogeared.ctfdaccounthook.service.AliasServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

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
        aliasService.setup();
        String adjective = AliasService.ADJECTIVES_DICT[0];
        String color = AliasService.COLORS_DICT[0];
        String cyber = AliasService.CYBER_SECURITY_DICT[0];

        when(random.nextInt(AliasService.ADJECTIVES_DICT.length)).thenReturn(0);
        when(random.nextInt(AliasService.COLORS_DICT.length)).thenReturn(0);
        when(random.nextInt(AliasService.CYBER_SECURITY_DICT.length)).thenReturn(0);

        String alias = aliasService.getAlias();

        assertThat(alias).isEqualTo(String.format("%s-%s-%s", adjective, color, cyber));
    }

    @Test
    public void whenZeroIndexFromRandom_WithDogs_ThenExpectedAlias() {
        ReflectionTestUtils.setField(aliasService, "dictionary", AliasService.Dictionary.DOGS);
        aliasService.setup();
        String adjective = AliasService.ADJECTIVES_DICT[0];
        String color = AliasService.COLORS_DICT[0];
        String dog = AliasService.DOG_BREEDS_DICT[0];

        when(random.nextInt(AliasService.ADJECTIVES_DICT.length)).thenReturn(0);
        when(random.nextInt(AliasService.COLORS_DICT.length)).thenReturn(0);
        when(random.nextInt(AliasService.DOG_BREEDS_DICT.length)).thenReturn(0);


        String alias = aliasService.getAlias();

        assertThat(alias).isEqualTo(String.format("%s-%s-%s", adjective, color, dog));
    }
}
