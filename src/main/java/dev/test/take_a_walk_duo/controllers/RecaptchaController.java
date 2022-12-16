package dev.test.take_a_walk_duo.controllers;

import dev.test.take_a_walk_duo.entities.member.EmailAuthEntity;
import dev.test.take_a_walk_duo.entities.member.UserEntity;
import dev.test.take_a_walk_duo.enums.CommonResult;
import dev.test.take_a_walk_duo.recaptcha.ReCaptchaResponse;
import dev.test.take_a_walk_duo.services.MemberService;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

@RestController
public class RecaptchaController {
    private final MemberService memberService;

    private List<UserEntity> userEntityList;

    private void create(UserEntity userEntity) {
        if (userEntityList == null){
            userEntityList = new ArrayList<>();
        }
        userEntityList.add(userEntity);
    }

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

    @RequestMapping(value = "login", method = RequestMethod.GET)
    public ResponseEntity<Object> getRecaptchaLogin() {
        if (userEntityList == null) {
            userEntityList = new ArrayList<>();
        }
            return new ResponseEntity<>(userEntityList, HttpStatus.OK);
    }

    @RequestMapping(value = "login",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String postRecaptchaLogin(
            @RequestParam(value = "email", required = false) String email,
            @RequestParam(value = "password", required = false) String password,
            @RequestParam(name = "g-recaptcha-response") String captchaResponse) {

        String url = "https://www.google.com/recaptcha/api/siteverify";
        String params = "?secret=6Ley2n4jAAAAAM80KYUYLoH_7mTnLpBC2m_wg83T&response=" + captchaResponse;

        ReCaptchaResponse reCaptchaResponse = restTemplate.exchange(url + params, HttpMethod.POST, null, ReCaptchaResponse.class).getBody();

        if (reCaptchaResponse.isSuccess()) {
            UserEntity userEntity = new UserEntity();
            userEntity.setEmail(email);
            userEntity.setPassword(password);
            create(userEntity);
            System.out.println("성공");
            return "SUCCESS";
        } else {
            System.out.println("실패");
        }
        return "Invalid Captcha";
    }
}
