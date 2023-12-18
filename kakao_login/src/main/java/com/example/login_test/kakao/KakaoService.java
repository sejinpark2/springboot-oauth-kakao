package com.example.login_test.kakao;

import com.example.login_test.user.User;
import com.example.login_test.user.UserRepository;
import com.example.login_test.user.UserRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
public class KakaoService {

    private UserRepository userRepository;

    @Value("${kakao.api.key}")
    private String API_KEY;

    @Value("${kakao.api.authUri}")
    private String KAKAO_AUTH_URI;

    @Value("${kakao.api.redirectUri}")
    private String REDIRECT_URI;


    public void kakaoLogin(String code, HttpSession session) throws JsonProcessingException {
        // 1. 인가코드로 엑세스토큰 가져오기
        String accessToken = getAccessToken(code,session);
        // 2. 엑세스토큰으로 유저정보 가져오기
        KakaoLoginInfoDTO kakaoUserInfo = getKakaoUserInfo(accessToken);
    }

    // #1 - 인가코드로 엑세스토큰 가져오기
    public String getAccessToken(String code, HttpSession session) {

        // 헤더에 Content-type 지정
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        // 바디에 필요한 정보 담기
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", API_KEY);
        params.add("redirect_uri", REDIRECT_URI);
        params.add("code", code);

        // POST 요청 보내기
        HttpEntity<MultiValueMap<String, String>> kakaoTokenReg = new HttpEntity<>(params, headers);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(
                "https://kauth.kakao.com/oauth/token",
                HttpMethod.POST,
                kakaoTokenReg,
                String.class
        );

        // response에서 엑세스토큰 가져오기
        String tokenJson = response.getBody();
        JSONObject jsonObject = new JSONObject(tokenJson);
        String accessToken = jsonObject.getString("access_token");

        session.setAttribute("access_token", accessToken);
        return accessToken;
    }

    // #2. 엑세스토큰으로 유저정보 가져오기
    private KakaoLoginInfoDTO getKakaoUserInfo(String accessToken) throws JsonProcessingException {
        // HTTP Header 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        // HTTP 요청 보내기
        HttpEntity<MultiValueMap<String, String>> kakaoUserInfoRequest = new HttpEntity<>(headers);
        RestTemplate rt = new RestTemplate();
        ResponseEntity<String> response = rt.exchange(
                "https://kapi.kakao.com/v2/user/me",
                HttpMethod.POST,
                kakaoUserInfoRequest,
                String.class
        );

        String responseBody = response.getBody();

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(responseBody);
        Long id = jsonNode.get("id").asLong();
//        System.out.println("jsonNode: " + jsonNode); // 무슨 값이 들어오나 체크

        // jsonNode 체크후 필요한 정보 가져오기 (추가 가능)
        String nickname = jsonNode.get("properties")
                .get("nickname").asText();
        String userimage = jsonNode.get("properties")
                .get("profile_image").asText();
        String email = (jsonNode.get("kakao_account")
                .get("email") != null) ? jsonNode.get("kakao_account")
                .get("email").asText() : null;
        System.out.println("id: " + id);
        System.out.println("nickname: " + nickname);
        System.out.println("userimage: " + userimage);
        System.out.println("email: " + email);
        return new KakaoLoginInfoDTO(id, nickname, email, userimage);
    }


    public void logout(HttpSession session) {
        String accessToken = (String) session.getAttribute("access_token");

        if (accessToken != null) {
            HttpHeaders headers = new HttpHeaders();
            headers.add("Authorization", "Bearer " + accessToken);

            RestTemplate restTemplate = new RestTemplate();
            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<String> responseEntity;
            try {
                responseEntity = restTemplate.exchange("https://kapi.kakao.com/v1/user/logout",
                        HttpMethod.POST,
                        entity,
                        String.class);
            } catch (HttpClientErrorException e) {
                throw new HttpClientErrorException(e.getStatusCode(), "Failed to logout");
            }

            if (responseEntity.getStatusCode() == HttpStatus.OK) {
                // 로그아웃 성공 시 세션에서 access_token을 제거합니다.
                session.removeAttribute("access_token");
            } else {
                throw new HttpClientErrorException(responseEntity.getStatusCode(), "Failed to logout");
            }
        }
    }
}




