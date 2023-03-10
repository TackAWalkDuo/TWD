package dev.twd.take_a_walk_duo.controllers;

import dev.twd.take_a_walk_duo.entities.member.EmailAuthEntity;
import dev.twd.take_a_walk_duo.entities.member.KakaoUserEntity;
import dev.twd.take_a_walk_duo.entities.member.NaverUserEntity;
import dev.twd.take_a_walk_duo.entities.member.UserEntity;
import dev.twd.take_a_walk_duo.enums.CommonResult;
import dev.twd.take_a_walk_duo.services.MemberService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

@Controller(value = "dev.twd.study_member_bbs.controllers.MemberController")
@RequestMapping(value = "member")
public class MemberController extends GeneralController {
    private final MemberService memberService;

    @Autowired
    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    // 회원 정보수정
    @RequestMapping(value = "modifyUser",
            method = RequestMethod.GET,
            produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getModifyUser(@SessionAttribute(value = "user", required = false) UserEntity user) {
        ModelAndView modelAndView = new ModelAndView("member/modifyUser");
        modelAndView.addObject("modifyUser", this.memberService.getUser(user.getEmail()));
        return modelAndView;
    }

    // 회원 정보수정하기 patch
    @RequestMapping(value = "modifyUser",
            method = RequestMethod.PATCH,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String patchUser(@SessionAttribute(value = "user", required = false) UserEntity user,
                            UserEntity modifyUser) {
        modifyUser.setEmail(user.getEmail());
        Enum<?> result = this.memberService.modifyUser(modifyUser);
        JSONObject responseObject = new JSONObject();
        responseObject.put("result", result.name().toLowerCase());
        return responseObject.toString();
    }

    // 회원 탈퇴
    @RequestMapping(value = "secession",
            method = RequestMethod.GET,
            produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getSecession() {
        ModelAndView modelAndView = new ModelAndView("member/secession");
        return modelAndView;
    }

    @RequestMapping(value = "secession",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String deleteUser(@SessionAttribute(value = "user", required = false) UserEntity user,
                             HttpSession session) {
        Enum<?> result = this.memberService.deleteUser(user);
        JSONObject responseObject = new JSONObject();
        responseObject.put("result", result.name().toLowerCase());
        session.setAttribute("user", null);
        return responseObject.toString();
    }

    // 마이페이지
    @RequestMapping(value = "myPage",
            method = RequestMethod.GET,
            produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getMyPage(@SessionAttribute(value = "user", required = false) UserEntity user) {
        ModelAndView modelAndView = new ModelAndView("member/myPage");
        // user 검색해서 값 넘겨주기.
        modelAndView.addObject("myPage", this.memberService.getUser(user.getEmail()));
        return modelAndView;
    }

    // 네이버 로그인
    @GetMapping(value = "naver", produces = MediaType.TEXT_PLAIN_VALUE)
    public ModelAndView getNaverLogin(@RequestParam(value = "code") String code,
                                      @RequestParam(value = "error", required = false) String error,
                                      @RequestParam(value = "error_description", required = false) String errorDescription,
                                      HttpSession session,
                                      HttpServletRequest request) throws IOException {
        String accessToken = this.memberService.getNaverAccessToken(code, request);
        NaverUserEntity user = this.memberService.getNaverUserInfo(accessToken);
        if (!user.isUser()) {
            ModelAndView modelAndView = new ModelAndView("member/naverRegister");
            modelAndView.addObject("naverUser", this.memberService.getNaverUserInfo(accessToken));
            return modelAndView;
        }
        session.setAttribute("user", this.memberService.getUser(user.getEmail()));
        return new ModelAndView("member/naver");
    }

    // 카카오 로그인
    @GetMapping(value = "kakao", produces = MediaType.TEXT_PLAIN_VALUE)
    public ModelAndView getKakaoLogin(@RequestParam(value = "code") String code,
                                      @RequestParam(value = "error", required = false) String error,
                                      @RequestParam(value = "error_description", required = false) String errorDescription,
                                      HttpSession session,
                                      HttpServletRequest request) throws IOException {
        String accessToken = this.memberService.getKakaoAccessToken(code, request);
        // 2번 인증코드로 토큰 전달
        KakaoUserEntity user = this.memberService.getKakaoUserInfo(accessToken);
        if (!user.isUser()) {
            ModelAndView modelAndView = new ModelAndView("member/kakaoRegister");
            modelAndView.addObject("kakaoUser", this.memberService.getKakaoUserInfo(accessToken));
            return modelAndView;
        }

        session.setAttribute("user", this.memberService.getUser(user.getEmail()));
        return new ModelAndView("member/kakao");
    }

    // 카카오 로그아웃
    @GetMapping(value = "logout")
    public ModelAndView getKakaoLogout(HttpSession session) {
        session.setAttribute("user", null);
        session.invalidate();
        return new ModelAndView("redirect:/");
    }

    // 로그인 GET
    @RequestMapping(value = "login",
            method = RequestMethod.GET,
            produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getLogin(@SessionAttribute(value = "user", required = false) UserEntity user,
                                 HttpServletRequest request) {
        if (user != null) {
            return new ModelAndView("redirect:/");
        }
        ModelAndView modelAndView = new ModelAndView("member/login");
        modelAndView.addObject("domain", String.format("%s://%s",
                request.getScheme(),
                request.getServerName()));
        return modelAndView;
    }

    // 로그인 POST
    @RequestMapping(value = "login",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String postLogin(
            HttpSession session,
            UserEntity user) {
        Enum<?> result = this.memberService.login(user);
        if (result == CommonResult.SUCCESS) {
            session.setAttribute("user", user);
        }
        JSONObject responseObject = new JSONObject();
        responseObject.put("result", result.name().toLowerCase());
        return responseObject.toString();
    }

    // 로그아웃 GET
    @RequestMapping(value = "logout",
            method = RequestMethod.GET,
            produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getLogout(HttpSession session) {
        session.setAttribute("user", null);
        ModelAndView modelAndView = new ModelAndView("redirect:/");
        return modelAndView;
    }

    // 회원가입 GET
    @RequestMapping(value = "register",
            method = RequestMethod.GET,
            produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getRegister() {
        ModelAndView modelAndView = new ModelAndView("member/register");
        return modelAndView;
    }

    // 회원가입 POST
    @RequestMapping(value = "register",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String postRegister(UserEntity user, EmailAuthEntity emailAuth) {
        Enum<?> result = this.memberService.register(user, emailAuth);
        JSONObject responseObject = new JSONObject();
        responseObject.put("result", result.name().toLowerCase());
        return responseObject.toString();
    }

    // 이메일 POST
    @RequestMapping(value = "email",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String postEmail(UserEntity user, EmailAuthEntity emailAuth, HttpServletRequest request) throws MessagingException, NoSuchAlgorithmException {
        Enum<?> result = this.memberService.sendEmailAuth(user, emailAuth, request);
        JSONObject responseObject = new JSONObject();
        responseObject.put("result", result.name().toLowerCase());
        if (result == CommonResult.SUCCESS) {
            responseObject.put("salt", emailAuth.getSalt());
        }
        return responseObject.toString();
    }

    // 이메일 PATCH
    @RequestMapping(value = "email",
            method = RequestMethod.PATCH,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String patchEmail(EmailAuthEntity emailAuth) {
        Enum<?> result = this.memberService.verifyEmailAuth(emailAuth);
        JSONObject responseObject = new JSONObject();
        responseObject.put("result", result.name().toLowerCase());
        return responseObject.toString();
    }

    // 비밀번호 찾기 메인 페이지
    @RequestMapping(value = "recoverPassword",
            method = RequestMethod.GET,
            produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getRecoverPassword() {
        ModelAndView modelAndView = new ModelAndView("member/recoverPassword");
        return modelAndView;
    }

    // 비밀번호 찾기 POST
    @RequestMapping(value = "recoverPassword",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String postRecoverPassword(EmailAuthEntity emailAuth, HttpServletRequest request)
            throws MessagingException {
        Enum<?> result = this.memberService.recoverPasswordSend(emailAuth, request);
        JSONObject responseObject = new JSONObject();
        responseObject.put("result", result.name().toLowerCase());

        if (result == CommonResult.SUCCESS) {
            responseObject.put("index", emailAuth.getIndex());
        }
        return responseObject.toString();
    }

    // 비밀번호 찾기 PATCH
    @RequestMapping(value = "recoverPassword",
            method = RequestMethod.PATCH,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String patchRecoverPassword(
            EmailAuthEntity emailAuth,
            UserEntity user) {
        Enum<?> result = this.memberService.recoverPassword(emailAuth, user);
        JSONObject responseObject = new JSONObject();
        responseObject.put("result", result.name().toLowerCase());
        return responseObject.toString();
    }

    // 이메일 찾기 GET
    @RequestMapping(value = "recoverEmail",
            method = RequestMethod.GET,
            produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getRecoverEmail() {
        ModelAndView modelAndView = new ModelAndView("member/recoverEmail");
        return modelAndView;
    }

    // 이메일 찾기 POST
    @RequestMapping(value = "recoverEmail",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String postRecoverEmail(UserEntity user) {
        Enum<?> result = this.memberService.recoverEmail(user);
        JSONObject responseObject = new JSONObject();
        responseObject.put("result", result.name().toLowerCase());
        if (result == CommonResult.SUCCESS) {
            responseObject.put("email", user.getEmail());
        }
        return responseObject.toString();
    }

    // recoverPasswordEmail GET
    @RequestMapping(value = "recoverPasswordEmail",
            method = RequestMethod.GET,
            produces = MediaType.TEXT_HTML_VALUE)
    @ResponseBody
    public ModelAndView getRecoverPasswordEmail(EmailAuthEntity emailAuth) {
        Enum<?> result = this.memberService.recoverPasswordAuth(emailAuth);
        ModelAndView modelAndView = new ModelAndView("member/recoverPasswordEmail");
        modelAndView.addObject("result", result.name());
        return modelAndView;
    }

    // recoverRecoverPasswordEmail POST
    @RequestMapping(value = "recoverPasswordEmail",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String postRecoverPasswordEmail(EmailAuthEntity emailAuth) {
        Enum<?> result = this.memberService.recoverPasswordCheck(emailAuth);
        JSONObject responseObject = new JSONObject();
        responseObject.put("result", result.name().toLowerCase());
        if (result == CommonResult.SUCCESS) {
            responseObject.put("code", emailAuth.getCode());
            responseObject.put("salt", emailAuth.getSalt());
        }
        return responseObject.toString();
    }
}













