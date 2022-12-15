package dev.test.take_a_walk_duo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class PetWalkApplication {

    public static void main(String[] args) {
        SpringApplication.run(PetWalkApplication.class, args);
    }
}
