package dev.test.take_a_walk_duo.vos.recaptcha;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "google.recatcha.key")
//  properties 파일 값들을 호출해 해당 변수에 매핑
public class CaptchaSettingsVO {
    private String site;
    private String secret;
    private String url;

    public String getSite() {
        return site;
    }

    public CaptchaSettingsVO setSite(String site) {
        this.site = site;
        return this;
    }

    public String getSecret() {
        return secret;
    }

    public CaptchaSettingsVO setSecret(String secret) {
        this.secret = secret;
        return this;
    }

    public String getUrl() {
        return url;
    }

    public CaptchaSettingsVO setUrl(String url) {
        this.url = url;
        return this;
    }
}
