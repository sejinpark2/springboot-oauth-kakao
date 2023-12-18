package com.example.login_test.kakao;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;


@RequiredArgsConstructor
@Controller

public class KakaoController {

    private final KakaoService kakaoService;

    @GetMapping("/oauth/kakao")
    public String kakaoCallback(@RequestParam(value = "code", required = false) String code, HttpServletRequest req) throws JsonProcessingException {

            kakaoService.kakaoLogin(code, req.getSession());

            return "redirect:/choseJoin.html";

    }

    @PostMapping("/kakao/logout")
    public ResponseEntity<String> logout(HttpServletRequest req) {
        try {
            kakaoService.logout(req.getSession());
            return ResponseEntity.ok("로그아웃되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}



