package dev.twd.take_a_walk_duo.services;

import dev.twd.take_a_walk_duo.entities.member.EmailAuthEntity;
import dev.twd.take_a_walk_duo.entities.member.KakaoUserEntity;
import dev.twd.take_a_walk_duo.entities.member.NaverUserEntity;
import dev.twd.take_a_walk_duo.entities.member.UserEntity;
import dev.twd.take_a_walk_duo.enums.CommonResult;
import dev.twd.take_a_walk_duo.enums.member.*;
import dev.twd.take_a_walk_duo.interfaces.IResult;
import dev.twd.take_a_walk_duo.mappers.IMemberMapper;
import dev.twd.take_a_walk_duo.utils.CryptoUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

@Service(value = "dev.twd.take_a_walk_duo.services.MemberService")
public class MemberService {
    private final JavaMailSender mailSender;

    private final TemplateEngine templateEngine;

    private final IMemberMapper memberMapper;

    @Autowired
    public MemberService(JavaMailSender mailSender, TemplateEngine templateEngine, IMemberMapper MemberMapper) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
        this.memberMapper = MemberMapper;
    }


    public UserEntity getUser(String email) {
        return this.memberMapper.selectUserByEmail(email);
    }


    // 회원정보 수정하기
    public Enum<? extends IResult> modifyUser(UserEntity user) {
        UserEntity existingUser = this.memberMapper.selectUserByEmail(
                user.getEmail());
        if (existingUser == null) {
            // 유저가 존재하지 않는 경우
            return ModifyUserResult.NO_SUCH_USER;
        }
        if (!existingUser.getEmail().equals(user.getEmail())) {
            return ModifyUserResult.NOT_ALLOWED;
        }

        //부족한 부분 채우는 작업
        user.setRegisteredOn(existingUser.getRegisteredOn());
        user.setPassword(existingUser.getPassword());

        return this.memberMapper.updateUser(user) > 0
                ? CommonResult.SUCCESS
                : CommonResult.FAILURE;
    }

    // 회원 탈퇴하기
    @Transactional
    public Enum<? extends IResult> deleteUser(UserEntity user) {
        UserEntity existingUser = this.memberMapper.selectUserByEmail(user.getEmail());
        KakaoUserEntity existingKakaoUser = this.memberMapper.selectKakaoUserByEmail(user.getEmail());
        NaverUserEntity existingNaverUser = this.memberMapper.selectNaverUserByEmail(user.getEmail());

        if (existingUser == null) {
            // 유저가 존재하지 않는 경우
            return CommonResult.FAILURE;
        }

        if (existingKakaoUser != null && this.memberMapper.deleteKakaoUserByEmail(user.getEmail()) <= 0)
            return CommonResult.FAILURE;

        if (existingNaverUser != null && this.memberMapper.deleteNaverUserByEmail(user.getEmail()) <= 0) {
            return CommonResult.FAILURE;
        }

        return (this.memberMapper.deleteUserByEmail(user.getEmail()) > 0)
                ? CommonResult.SUCCESS
                : CommonResult.FAILURE;
    }

    // 네이버 access token 발급 받는 getNaverAccessToken
    public String getNaverAccessToken(String code, HttpServletRequest request) throws IOException {

        URL url = new URL("https://nid.naver.com/oauth2.0/token");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        int responseCode;
        try (OutputStreamWriter outputStreamWriter = new OutputStreamWriter(connection.getOutputStream())) {
            try (BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter)) {
                StringBuilder requestBuilder = new StringBuilder();
                requestBuilder.append("grant_type=authorization_code");
                requestBuilder.append("&client_id=bkuhxnOKDZAYExqHJzN1");
                requestBuilder.append("&client_secret=lHgUA8xkse");
                requestBuilder.append(String.format("&redirect_uri=%s://%s:%d/member/naver",
                        request.getScheme(),
                        request.getServerName(),
                        request.getServerPort()));
                requestBuilder.append("&code=").append(code);
                requestBuilder.append("&state=state");
                bufferedWriter.write(requestBuilder.toString());
                bufferedWriter.flush();
                responseCode = connection.getResponseCode();
            }
        }
        StringBuilder responseBuilder = new StringBuilder();
        try (InputStreamReader inputStreamReader = new InputStreamReader(connection.getInputStream())) {
            try (BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    responseBuilder.append(line);
                }
            }
        }
        JSONObject responseObject = new JSONObject(responseBuilder.toString());
        return responseObject.getString("access_token");
    }

    // 카카오 userinfo 발급 받는 getKakaoUserInfo
    public NaverUserEntity getNaverUserInfo(String accessToken) throws IOException {
        URL url = new URL("https://openapi.naver.com/v1/nid/me");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("Authorization", String.format("Bearer %s", accessToken));
        connection.setRequestMethod("GET");
        int responseCode = connection.getResponseCode();
        StringBuilder responseBuilder = new StringBuilder();
        try (InputStreamReader inputStreamReader = new InputStreamReader(connection.getInputStream())) {
            try (BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    responseBuilder.append(line);
                }
            }
        }
        JSONObject responseObject = new JSONObject(responseBuilder.toString());
        JSONObject naverObject = responseObject.getJSONObject("response");
        String id = String.valueOf(naverObject.getString("id"));
        NaverUserEntity naverUser = this.memberMapper.selectNaverUserById(id);
        if (naverUser == null) {
            naverUser = new NaverUserEntity();
            naverUser.setId(id);
            naverUser.setEmail(naverObject.getString("email"));
            naverUser.setNickname(naverObject.getString("nickname"));
            naverUser.setUser(false);
            // 이메일 인증
            this.memberMapper.insertNaverUser(naverUser);
        }
        return naverUser;
    }

    // 카카오 access token 발급 받는 getKakaoAccessToken
    public String getKakaoAccessToken(String code, HttpServletRequest request) throws IOException {
        URL url = new URL("https://kauth.kakao.com/oauth/token");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        int responseCode;
        try (OutputStreamWriter outputStreamWriter = new OutputStreamWriter(connection.getOutputStream())) {
            try (BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter)) {
                StringBuilder requestBuilder = new StringBuilder();
                requestBuilder.append("grant_type=authorization_code");
                requestBuilder.append("&client_id=6da80eef1101bb3318ba1f6bde584ab1");

                requestBuilder.append(String.format("&redirect_uri=%s://%s:%d/member/kakao",
                        request.getScheme(),
                        request.getServerName(),
                        request.getServerPort()));
                requestBuilder.append("&code=").append(code);
                bufferedWriter.write(requestBuilder.toString());
                bufferedWriter.flush();
                responseCode = connection.getResponseCode();
            }
        }
        StringBuilder responseBuilder = new StringBuilder();
        try (InputStreamReader inputStreamReader = new InputStreamReader(connection.getInputStream())) {
            try (BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    responseBuilder.append(line);
                }
            }
        }
        JSONObject responseObject = new JSONObject(responseBuilder.toString());
        return responseObject.getString("access_token");
    }

    // 카카오 userinfo 발급 받는 getKakaoUserInfo
    public KakaoUserEntity getKakaoUserInfo(String accessToken) throws IOException {
        URL url = new URL("https://kapi.kakao.com/v2/user/me");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("Authorization", String.format("Bearer %s", accessToken));
        connection.setRequestMethod("GET");
        int responseCode = connection.getResponseCode();
        StringBuilder responseBuilder = new StringBuilder();
        try (InputStreamReader inputStreamReader = new InputStreamReader(connection.getInputStream())) {
            try (BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    responseBuilder.append(line);
                }
            }
        }
        JSONObject responseObject = new JSONObject(responseBuilder.toString());
        JSONObject propertyObject = responseObject.getJSONObject("properties");
        JSONObject kakaoObject = responseObject.getJSONObject("kakao_account");
        String id = String.valueOf(responseObject.getLong("id"));
        KakaoUserEntity kakaoUser = this.memberMapper.selectKakaoUserById(id);
        if (kakaoUser == null) {
            kakaoUser = new KakaoUserEntity();
            kakaoUser.setId(id);
            kakaoUser.setEmail(kakaoObject.getString("email"));
            kakaoUser.setNickname(propertyObject.getString("nickname"));
            kakaoUser.setUser(false);
            // 이메일 인증
            this.memberMapper.insertKakaoUser(kakaoUser);
        }
        return kakaoUser;
    }

    // 로그인
    @Transactional
    public Enum<? extends IResult> login(UserEntity user) {
        UserEntity existingUser = this.memberMapper.selectUserByEmail(user.getEmail());
        if (existingUser == null) {
            return CommonResult.FAILURE;
        }
        user.setPassword(CryptoUtils.hashSha512(user.getPassword()));
        if (!user.getPassword().equals(existingUser.getPassword())) {
            return CommonResult.FAILURE;
        }
        user.setAdmin(existingUser.getAdmin());
        user.setAddressPostal(existingUser.getAddressPostal());
        user.setAddressPrimary(existingUser.getAddressPrimary());
        user.setAddressSecondary(existingUser.getAddressSecondary());
        user.setNickname(existingUser.getNickname());
        return CommonResult.SUCCESS;
    }

    // 회원가입
    public Enum<? extends IResult> register(
            UserEntity user,
            EmailAuthEntity emailAuth) {
        EmailAuthEntity existingEmailAuth = this.memberMapper.selectEmailAuthByEmailCodeSalt(
                emailAuth.getEmail(),
                emailAuth.getCode(),
                emailAuth.getSalt());

        // kakao 계정 검색
        KakaoUserEntity existingKakaoUser = this.memberMapper.selectKakaoUserByEmail(
                user.getEmail());

        NaverUserEntity existingNaverUser = this.memberMapper.selectNaverUserByEmail(
                user.getEmail());

        if (existingKakaoUser == null && existingNaverUser == null) {
            if (existingEmailAuth == null || !existingEmailAuth.isExpired()) {
                // || kakaoUser == null
                return RegisterResult.EMAIL_NOT_VERIFIED;
            }
        }

        // haveDog 결과가 notHave 이면 none 을 넣어준다.
        if (user.getHaveDog().equals("notHave")) {
            user.setSpecies("예비견주");
        }
        // 비밀번호 해싱
        user.setPassword(CryptoUtils.hashSha512(user.getPassword()));
        if (this.memberMapper.insertUser(user) == 0) {
            return CommonResult.FAILURE;
        }

        if (existingKakaoUser != null) {
            existingKakaoUser.setUser(true);
            if (this.memberMapper.updateKakaoUser(existingKakaoUser) <= 0) {
                return CommonResult.FAILURE;
            }
        }

        if (existingNaverUser != null) {
            existingNaverUser.setUser(true);
            if (this.memberMapper.updateNaverUser(existingNaverUser) <= 0) {
                return CommonResult.FAILURE;
            }
        }

        return CommonResult.SUCCESS;
    }


    @Transactional
    public Enum<? extends IResult> sendEmailAuth(UserEntity user, EmailAuthEntity emailAuth, HttpServletRequest request)
            throws NoSuchAlgorithmException, MessagingException {
        UserEntity existingUser = this.memberMapper.selectUserByEmail(user.getEmail());
        if (existingUser != null) {
            return SendEmailAuthResult.EMAIL_DUPLICATED;
        }
        String authCode = RandomStringUtils.randomNumeric(6);
        String authSalt = String.format("%s%s%f%f",
                user.getEmail(),
                authCode,
                Math.random(),
                Math.random());
        // 솔트 해싱
        StringBuilder authSaltHashBuilder = new StringBuilder();
        MessageDigest md = MessageDigest.getInstance("SHA-512");
        md.update(authSalt.getBytes(StandardCharsets.UTF_8));
        for (byte hashByte : md.digest()) {
            authSaltHashBuilder.append(String.format("%02x", hashByte));
        }
        authSalt = authSaltHashBuilder.toString();

        Date createdOn = new Date();
        Date expiresOn = DateUtils.addMinutes(createdOn, 5);

        emailAuth.setEmail(user.getEmail());
        emailAuth.setCode(authCode);
        emailAuth.setSalt(authSalt);
        emailAuth.setCreatedOn(createdOn);
        emailAuth.setExpiresOn(expiresOn);
        emailAuth.setExpired(false);

        if (this.memberMapper.insertEmailAuth(emailAuth) == 0) {
            return CommonResult.FAILURE;
        }
        Context context = new Context();
        context.setVariable("code", emailAuth.getCode());
        context.setVariable("domain", String.format("%s://%s:%d",
                request.getScheme(),
                request.getServerName(),
                request.getServerPort()));


        String text = this.templateEngine.process("member/registerEmailAuth", context);
        MimeMessage mail = this.mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mail, "UTF-8");
        helper.setFrom("rmsgh1202@gmail.com");
        helper.setTo(user.getEmail());
        helper.setSubject("회원가입 인증 번호");
        helper.setText(text, true);
        this.mailSender.send(mail);

        return CommonResult.SUCCESS;
    }

    @Transactional
    public Enum<? extends IResult> verifyEmailAuth(EmailAuthEntity emailAuth) {
        EmailAuthEntity existingEmailAuth = this.memberMapper.selectEmailAuthByEmailCodeSalt(
                emailAuth.getEmail(),
                emailAuth.getCode(),
                emailAuth.getSalt());
        if (existingEmailAuth == null) {
            return CommonResult.FAILURE;
        }
        if (existingEmailAuth.getExpiresOn().compareTo(new Date()) < 0) {
            return VerifyEmailAuthResult.EXPIRED;
        }
        existingEmailAuth.setExpired(true);
        if (this.memberMapper.updateEmailAuth(existingEmailAuth) == 0) {
            return CommonResult.FAILURE;
        }
        return CommonResult.SUCCESS;
    }

    // recoverEmail
    @Transactional
    public Enum<? extends IResult> recoverEmail(UserEntity user) {
        UserEntity existingUser = this.memberMapper.selectUserByNameContact(
                user.getName(),
                user.getContact());
        if (existingUser == null) {
            return CommonResult.FAILURE;
        }
        user.setEmail(existingUser.getEmail());
        return CommonResult.SUCCESS;
    }


    // recoverPassword
    @Transactional
    public Enum<? extends IResult> recoverPassword(EmailAuthEntity emailAuth, UserEntity user) {
        EmailAuthEntity existingEmailAuth = this.memberMapper.selectEmailAuthByEmailCodeSalt(
                emailAuth.getEmail(),
                emailAuth.getCode(),
                emailAuth.getSalt());

        if (existingEmailAuth == null || !existingEmailAuth.isExpired()) {
            return CommonResult.FAILURE;
        }
        UserEntity existingUser = this.memberMapper.selectUserByEmail(existingEmailAuth.getEmail());
        // 비밀번호 해싱
        existingUser.setPassword(CryptoUtils.hashSha512(user.getPassword()));
        if (this.memberMapper.updateUser(existingUser) == 0) {
            return CommonResult.FAILURE;
        }
        return CommonResult.SUCCESS;
    }

    @Transactional
    public Enum<? extends IResult> recoverPasswordSend(EmailAuthEntity emailAuth, HttpServletRequest req) throws MessagingException {
        UserEntity existingUser = this.memberMapper.selectUserByEmail(emailAuth.getEmail());
        if (existingUser == null) {
            return CommonResult.FAILURE;
        }
        String authCode = RandomStringUtils.randomNumeric(6);
        String authSalt = String.format("%s%s%f%f",
                authCode,
                emailAuth.getEmail(),
                Math.random(),
                Math.random());
        // 비밀번호 해싱
        authSalt = CryptoUtils.hashSha512(authSalt);
        Date createdOn = new Date();
        Date expiresOn = DateUtils.addMinutes(createdOn, 5);
        // 5분 미래
        emailAuth.setCode(authCode);
        emailAuth.setSalt(authSalt);
        emailAuth.setCreatedOn(createdOn);
        emailAuth.setExpiresOn(expiresOn);
        emailAuth.setExpired(false);
        if (this.memberMapper.insertEmailAuth(emailAuth) == 0) {
            return CommonResult.FAILURE;
        }
        Context context = new Context();
        context.setVariable("email", emailAuth.getEmail());
        context.setVariable("code", emailAuth.getCode());
        context.setVariable("salt", emailAuth.getSalt());
        context.setVariable("domain", String.format("%s://%S:%d",
                req.getScheme(),
                req.getServerName(),
                req.getServerPort()));

        String text = this.templateEngine.process("member/recoverPasswordEmailAuth", context);
        MimeMessage mail = this.mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mail, "UTF-8");
        helper.setFrom("rootgo1@twd.com");
        helper.setTo(emailAuth.getEmail());
        helper.setSubject("[twd]비밀번호 재설정 인증 링크");
        helper.setText(text, true);
        this.mailSender.send(mail);

        return CommonResult.SUCCESS;
    }

    // 비밀번호 찾기 인증
    @Transactional
    public Enum<? extends IResult> recoverPasswordAuth(EmailAuthEntity emailAuth) {
        EmailAuthEntity existingEmailAuth = this.memberMapper.selectEmailAuthByEmailCodeSalt(
                emailAuth.getEmail(),
                emailAuth.getCode(),
                emailAuth.getSalt());

        if (existingEmailAuth == null) {
            return CommonResult.FAILURE;
        }
        if (new Date().compareTo(existingEmailAuth.getExpiresOn()) > 0) {
            return CommonResult.FAILURE;
        }
        existingEmailAuth.setExpired(true);
        if (this.memberMapper.updateEmailAuth(existingEmailAuth) == 0) {
            return CommonResult.FAILURE;
        }
        return CommonResult.SUCCESS;
    }

    // recoverPasswordCheck
    public Enum<? extends IResult> recoverPasswordCheck(EmailAuthEntity emailAuth) {
        EmailAuthEntity existingEmailAuth = this.memberMapper.selectEmailAuthByIndex(emailAuth.getIndex());
        if (existingEmailAuth == null || !existingEmailAuth.isExpired()) {
            return CommonResult.FAILURE;
        }
        emailAuth.setCode(existingEmailAuth.getCode());
        emailAuth.setSalt(existingEmailAuth.getSalt());
        return CommonResult.SUCCESS;
    }

}
