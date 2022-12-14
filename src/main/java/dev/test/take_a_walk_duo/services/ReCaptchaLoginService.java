package dev.test.take_a_walk_duo.services;

import dev.test.take_a_walk_duo.recaptcha.ReCaptchaKeys;
import dev.test.take_a_walk_duo.recaptcha.ReCaptchaResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

@Service
public class ReCaptchaLoginService implements ReCaptchaService {
    private final ReCaptchaKeys reCaptchaKeys;
    private final RestTemplate restTemplate;
    private static final Logger log = LoggerFactory.getLogger(ReCaptchaLoginService.class);

    @Autowired
    public ReCaptchaLoginService(ReCaptchaKeys reCaptchaKeys, RestTemplate restTemplate) {
        this.reCaptchaKeys = reCaptchaKeys;
        this.restTemplate = restTemplate;
    }

    @Override
    public ReCaptchaResponse verify(String response) {
        // API request
        URI verifyURI = URI.create(
                String.format("https://www.google.com/recaptcha/api/siteverify?secret=%s&response=%s",
                        reCaptchaKeys.getSecret(), response));
        // make HTTP call using RestTemplate
        ReCaptchaResponse reCaptchaResponse = restTemplate.getForObject(verifyURI, ReCaptchaResponse.class);
        // log the returned reCaptchaResponse object
        log.info(">>>>>>>>>>>> response after verifying : {}", reCaptchaResponse);

        if (reCaptchaResponse != null) {
            if (reCaptchaResponse.isSuccess() && (reCaptchaResponse.getScore() < reCaptchaKeys.getThreshold()
            || !reCaptchaResponse.getAction().equals("login"))){
                reCaptchaResponse.setSuccess(false);
            }
        }
        return reCaptchaResponse;
    }
}
