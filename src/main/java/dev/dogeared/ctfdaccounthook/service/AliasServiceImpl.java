package dev.dogeared.ctfdaccounthook.service;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class AliasServiceImpl implements AliasService {

    @Value("#{ @environment['api.dictionary'] }")
    private Dictionary dictionary;

    final private Random random;

    private String[] thirdDictionary;

    public AliasServiceImpl() {
        this.random = new Random();
    }

    @PostConstruct
    public void setup() {
        // until null switch support is out of preview
        if (dictionary == null) {
            dictionary = Dictionary.CYBER;
        }
        thirdDictionary =
            switch(dictionary) {
                case CYBER -> CYBER_SECURITY_DICT;
                case DOGS -> DOG_BREEDS_DICT;
                default -> CYBER_SECURITY_DICT;
            };
    }


    @Override
    public String getAlias() {
        String randAdjective  = ADJECTIVES_DICT[random.nextInt(ADJECTIVES_DICT.length)];
        String randColor = COLORS_DICT[random.nextInt(COLORS_DICT.length)];
        String randThird = thirdDictionary[random.nextInt(thirdDictionary.length)];
        return String.format("%s-%s-%s", randAdjective, randColor, randThird);
    }
}
