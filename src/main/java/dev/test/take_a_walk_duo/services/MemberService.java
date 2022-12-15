package dev.test.take_a_walk_duo.services;

import dev.test.take_a_walk_duo.entities.member.EmailAuthEntity;
import dev.test.take_a_walk_duo.entities.member.UserEntity;
import dev.test.take_a_walk_duo.enums.CommonResult;
import dev.test.take_a_walk_duo.enums.member.RegisterResult;
import dev.test.take_a_walk_duo.enums.member.SendEmailAuthResult;
import dev.test.take_a_walk_duo.enums.member.VerifyEmailAuthResult;
import dev.test.take_a_walk_duo.interfaces.IResult;
import dev.test.take_a_walk_duo.mappers.IMemberMapper;
import dev.test.take_a_walk_duo.utils.CryptoUtils;
import org.apache.catalina.User;
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
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

@Service(value = "dev.test.take_a_walk_duo.services.MemberService")
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

    // 카카오 access token 발급 받는 getKakaoAccessToken
    public String getKakaoAccessToken(String code) throws IOException {

        URL url = new URL("https://kauth.kakao.com/oauth/token");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        int responseCode;
        try (OutputStreamWriter outputStreamWriter = new OutputStreamWriter(connection.getOutputStream())) {
            try (BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter)) {
                StringBuilder requestBuilder = new StringBuilder();
                requestBuilder.append("grant_type=authorization_code");
                requestBuilder.append("&client_id=ecccd1725ed813810c3752e8582735fe");
                // TODO REST_API_KEY 입력
                requestBuilder.append("&redirect_uri=http://localhost:8080/member/kakao");
                // TODO redirect_uri
                requestBuilder.append("&code=").append(code);
                bufferedWriter.write(requestBuilder.toString());
                bufferedWriter.flush();
                responseCode = connection.getResponseCode();
            }
            System.out.println("응답 코드 : " + responseCode);
        }
        StringBuilder responseBuilder = new StringBuilder();
        try (InputStreamReader inputStreamReader = new InputStreamReader(connection.getInputStream())) {
            try (BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    responseBuilder.append(line);
                }
            }
            System.out.println("응답 내용 : " + responseBuilder);
        }
        JSONObject responseObject = new JSONObject(responseBuilder.toString());
        return responseObject.getString("access_token");
    }

    // 카카오 userinfo 발급 받는 getKakaoUserInfo
    public UserEntity getKakaoUserInfo(String accessToken) throws IOException {
        URL url = new URL("https://kapi.kakao.com/v2/user/me");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("Authorization", String.format("Bearer %s", accessToken));
        connection.setRequestMethod("GET");
        int responseCode = connection.getResponseCode();
        System.out.println("응답 코드 : " + responseCode);
        StringBuilder responseBuilder = new StringBuilder();
        try (InputStreamReader inputStreamReader = new InputStreamReader(connection.getInputStream())) {
            try (BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    responseBuilder.append(line);
                }
            }
        }
        System.out.println("응답 내용 : " + responseBuilder);
        JSONObject responseObject = new JSONObject(responseBuilder.toString());
        JSONObject propertyObject = responseObject.getJSONObject("properties");
        String email = String.valueOf(responseObject.getLong("email"));
        // TODO 이부분 맞는지 체크해야함
        UserEntity user = this.memberMapper.selectUserByEmail(email);
        // TODO 이부분 맞는지 체크해야함
        if (user == null) {
            user = new UserEntity();
            user.setEmail(email);
            // TODO 이부분 맞는지 체크해야함
            user.setNickname(propertyObject.getString("nickname"));
            this.memberMapper.insertUser(user);
        }
        return user;
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
        return CommonResult.SUCCESS;
    }

    // 회원가입
    // 1. 'emailAuth'가 가진 'email', 'code', 'salt' 값 기준으로 새로운 'EmailAuthEntity' SELECT 해서 가져옴
    // 2. <1>에서 가져온 새로운 객체가 null 이거나 이가 가진 inExpired() 호출 결과가 false 인 경우 'RegisterResult.EMAIL_NOT_VERIFIED'를 결과로 반환.
    // 3. 'user' 객체를 'IMemberMapper' 객체의 'insertUser' 메서드 호출시 전달인자로 하여 INSERT 하기.
    // 4. <3>의 결과가 0이면 'CommonResult.FAILURE' 반환하기.
    // 5. 위 과정 전체를 거친 후 'CommonResult.SUCCESS' 반환하기.
    public Enum<? extends IResult> register(
            UserEntity user,
            EmailAuthEntity emailAuth) {
        EmailAuthEntity existingEmailAuth = this.memberMapper.selectEmailAuthByEmailCodeSalt(
                emailAuth.getEmail(),
                emailAuth.getCode(),
                emailAuth.getSalt());
        if (existingEmailAuth == null || !existingEmailAuth.isExpired()) {
            System.out.println(existingEmailAuth == null);
            System.out.println(existingEmailAuth.isExpired());
            return RegisterResult.EMAIL_NOT_VERIFIED;
        }
        // 비밀번호 해싱
        user.setPassword(CryptoUtils.hashSha512(user.getPassword()));
        if (this.memberMapper.insertUser(user) == 0) {
            return CommonResult.FAILURE;
        }
        return CommonResult.SUCCESS;
    }


    @Transactional
    public Enum<? extends IResult> sendEmailAuth(UserEntity user, EmailAuthEntity emailAuth)
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
    public Enum<? extends IResult> recoverPasswordSend(EmailAuthEntity emailAuth) throws MessagingException {
        UserEntity existingUser = this.memberMapper.selectUserByEmail(emailAuth.getEmail());
        if (existingUser == null) {
            return CommonResult.FAILURE;
        }
        String authCode = RandomStringUtils.randomNumeric(6);
        String authSalt = String.format("$s%s%f%f",
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

        String text = this.templateEngine.process("member/recoverPasswordEmailAuth", context);
        MimeMessage mail = this.mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mail, "UTF-8");
        helper.setFrom("rmsgh1202@gmail.com");
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
