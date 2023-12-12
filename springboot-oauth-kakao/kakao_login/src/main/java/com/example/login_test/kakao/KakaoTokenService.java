package com.example.login_test.kakao;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Service
public class KakaoTokenService {

    @Value("${kakao.api.key}")
    private String API_KEY;

    public boolean isAccessTokenValid(String accessToken) {
        RestTemplate restTemplate = new RestTemplate();

        String requestUrl = "https://kapi.kakao.com/v1/user/access_token_info";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);

        HttpEntity<?> requestEntity = new HttpEntity<>(headers);

        try {
            ResponseEntity<String> responseEntity = restTemplate.exchange(requestUrl, HttpMethod.GET, requestEntity, String.class);
            if (responseEntity.getStatusCode() == HttpStatus.OK) {
                return true; // 액세스 토큰이 유효한 경우
            } else {
                return false; // 액세스 토큰이 만료된 경우
            }
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                return false; // 액세스 토큰이 만료된 경우
            } else {
                throw e;
            }
        }
    }

    public String getNewAccessToken(String refreshToken) {
        RestTemplate restTemplate = new RestTemplate();

        String requestUrl = "https://kauth.kakao.com/oauth/token";

        MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();
        parameters.add("grant_type", "refresh_token");
        parameters.add("client_id", API_KEY);
        parameters.add("refresh_token", refreshToken);

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(parameters, null);

        ResponseEntity<KakaoApiResponse> responseEntity = restTemplate.postForEntity(requestUrl, requestEntity, KakaoApiResponse.class);

        if (responseEntity.getStatusCode() == HttpStatus.OK) {
            KakaoApiResponse response = responseEntity.getBody();
            return response.getAccess_token();
        } else {
            throw new HttpClientErrorException(responseEntity.getStatusCode(), "Failed to refresh token");
        }
    }

    public String getValidAccessToken(String accessToken, String refreshToken) {
        if (isAccessTokenValid(accessToken)) {
            return accessToken;
        } else {
            return getNewAccessToken(refreshToken);
        }
    }
}

