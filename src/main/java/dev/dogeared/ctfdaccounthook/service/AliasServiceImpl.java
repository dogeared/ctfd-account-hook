package dev.dogeared.ctfdaccounthook.service;

import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class AliasServiceImpl implements AliasService {

    private Random random;

    public AliasServiceImpl() {
        this.random = new Random();
    }
    @Override
    public String getAlias() {
        String randAdjective  = ADJECTIVES_DICT[random.nextInt(ADJECTIVES_DICT.length)];
        String randColor = COLORS_DICT[random.nextInt(COLORS_DICT.length)];
        String randCyber = CYBER_SECURITY_DICT[random.nextInt(CYBER_SECURITY_DICT.length)];
        return String.format("%s-%s-%s", randAdjective, randColor, randCyber);
    }
}
