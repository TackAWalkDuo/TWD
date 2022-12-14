package dev.test.take_a_walk_duo.recaptcha;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class ReCaptchaResponse {
    private boolean success;
    private float score;
    private String action;
    @JsonProperty("challenge_ts")
    private String challengeTs;
    private String hostname;
    @JsonProperty("error-codes")
    List<String> errorCodes;

    public boolean isSuccess() {
        return success;
    }

    public ReCaptchaResponse setSuccess(boolean success) {
        this.success = success;
        return this;
    }

    public float getScore() {
        return score;
    }

    public ReCaptchaResponse setScore(float score) {
        this.score = score;
        return this;
    }

    public String getAction() {
        return action;
    }

    public ReCaptchaResponse setAction(String action) {
        this.action = action;
        return this;
    }

    public String getChallengeTs() {
        return challengeTs;
    }

    public ReCaptchaResponse setChallengeTs(String challengeTs) {
        this.challengeTs = challengeTs;
        return this;
    }

    public String getHostname() {
        return hostname;
    }

    public ReCaptchaResponse setHostname(String hostname) {
        this.hostname = hostname;
        return this;
    }

    public List<String> getErrorCodes() {
        return errorCodes;
    }

    public ReCaptchaResponse setErrorCodes(List<String> errorCodes) {
        this.errorCodes = errorCodes;
        return this;
    }

    @Override
    public String toString() {
        return "ReCaptchaResponse{" +
                "success=" + success +
                ", score=" + score +
                ", action='" + action + '\'' +
                ", challengeTs='" + challengeTs + '\'' +
                ", hostname='" + hostname + '\'' +
                ", errorCodes=" + errorCodes +
                '}';
    }
}
