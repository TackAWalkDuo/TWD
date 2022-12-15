package dev.test.take_a_walk_duo.services;

import dev.test.take_a_walk_duo.recaptcha.ReCaptchaResponse;

public interface ReCaptchaService {
    ReCaptchaResponse verify(String response);
}
