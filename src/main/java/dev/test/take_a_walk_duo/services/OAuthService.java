//package dev.test.take_a_walk_duo.services;
//
//import com.google.gson.JsonParser;
//import org.json.simple.parser.JSONParser;
//import org.springframework.stereotype.Service;
//
//import com.google.gson.JsonElement;
//import java.io.*;
//import java.io.BufferedWriter;
//import java.io.IOException;
//import java.io.OutputStreamWriter;
//import java.net.HttpURLConnection;
//import java.net.URL;
//
//@Service
//public class OAuthService {
//
//    public String getKakaoAccessToken(String code) {
//        String accessToken = "";
//        String refreshToken = "";
//        String reqURL = "https://kauth.kakao.com/oauth.token";
//
//        try {
//            URL url = new URL(reqURL);
//            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//
//            conn.setRequestMethod("POST");
//            conn.setDoOutput(true);
//
//            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
//            StringBuilder stringBuilder = new StringBuilder();
//            stringBuilder.append("grant_type=authorization_code");
//            stringBuilder.append("client_id=ecccd1725ed813810c3752e8582735fe"); //TODO REST API KEY 입력
//            stringBuilder.append("redirect_uri=http://localhost:8080/member/loginhttp://localhost:8080/member/login"); //TODO 인가코드 받은 redirect_uri 입력
//            stringBuilder.append("&code=" + code);
//            bufferedWriter.write(stringBuilder.toString());
//            bufferedWriter.flush();
//
//            // 결과 코드가 200이면 성공
//            int responseCode = conn.getResponseCode();
//            System.out.println("responseCode : " + responseCode);
//
//            // 요청을 통해 얻은 JSON타입의 Response 메세지 읽어오기
//            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
//            String line = "";
//            String result = "";
//
//            while ((line = bufferedReader.readLine()) != null) {
//                result += line;
//            }
//            System.out.println("response body : " + result);
//
//            // JSON 파싱 객체 생성
////            JsonParser jsonParser = new JsonParser(); // old
////            JsonElement element = jsonParser.parse(result);
//
//            JsonElement jsonElement = JsonParser.parseString(result); // new
//
//            System.out.println("accessToken : " + accessToken);
//            System.out.println("refreshToken : " + refreshToken);
//
//            bufferedReader.close();
//            bufferedWriter.close();
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return accessToken;
//    }
//}
