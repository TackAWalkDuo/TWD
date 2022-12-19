package dev.twd.take_a_walk_duo.recaptcha;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "google.recaptcha.key")
public class ReCaptchaKeys {
    private String site;
    private String secret;
    private float threshold;

    public String getSite() {
        return site;
    }

    public ReCaptchaKeys setSite(String site) {
        this.site = site;
        return this;
    }

    public String getSecret() {
        return secret;
    }

    public ReCaptchaKeys setSecret(String secret) {
        this.secret = secret;
        return this;
    }

    public float getThreshold() {
        return threshold;
    }

    public ReCaptchaKeys setThreshold(float threshold) {
        this.threshold = threshold;
        return this;
    }
}
