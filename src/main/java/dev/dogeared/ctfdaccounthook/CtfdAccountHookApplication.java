package dev.dogeared.ctfdaccounthook;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class CtfdAccountHookApplication {

    public static void main(String[] args) {
        SpringApplication.run(CtfdAccountHookApplication.class, args);
    }
}
