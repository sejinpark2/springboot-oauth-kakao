package com.example.login_test.kakao;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class KakaoLoginInfoDTO {
    private Long id;
    private String nickname;
    private String email;
    private String profileImgUrl;
}
