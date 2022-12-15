package dev.test.take_a_walk_duo.controllers;

import dev.test.take_a_walk_duo.entities.member.UserEntity;
import dev.test.take_a_walk_duo.enums.CommonResult;
import dev.test.take_a_walk_duo.recaptcha.ReCaptchaResponse;
import dev.test.take_a_walk_duo.services.MemberService;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpSession;

@RestController
public class RecaptchaController {
    private final MemberService memberService;

    @Autowired
    public RecaptchaController(MemberService memberService) {
        this.memberService = memberService;
    }

    @Autowired
    RestTemplate restTemplate;

    /**
     * recaptcha
     * rootgo
     */
    @RequestMapping(value = "login",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public String recaptchaLogin(
            HttpSession session,
            UserEntity user,
            @RequestParam(name = "g-recaptcha-response")
            String captchaResponse) {

        String url = "https://www.google.com/recaptcha/api/siteverify";
        String params = "?secret=6Ley2n4jAAAAAM80KYUYLoH_7mTnLpBC2m_wg83T&response=" + captchaResponse;

        ReCaptchaResponse reCaptchaResponse = restTemplate.exchange(url + params, HttpMethod.POST, null, ReCaptchaResponse.class).getBody();

        if (reCaptchaResponse.isSuccess()) {
            return "SUCCESS";
        }
        return "Invalid Captcha";

    }
}
