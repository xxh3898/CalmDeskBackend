package com.code808.calmdesk.domain.auth.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.code808.calmdesk.domain.auth.dto.LoginDto;
import com.code808.calmdesk.domain.auth.dto.SignupDto;
import com.code808.calmdesk.domain.auth.service.AuthService;
import com.code808.calmdesk.global.security.JwtTokenProvider;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "Auth", description = "인증 관련 API (로그인, 회원가입, 로그아웃, 토큰 갱신)")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class AuthController {

    private final AuthService authService;
    private final JwtTokenProvider jwtTokenProvider;

    @Operation(summary = "회원가입", description = "새로운 사용자를 등록합니다.")
    @PostMapping("/signup")
    public ResponseEntity<SignupDto.SignupResponse> signup(
            @Valid @RequestBody SignupDto.SignupRequest request) {
        return ResponseEntity.ok(authService.signup(request));
    }

    @Operation(summary = "로그인", description = "이메일과 비밀번호로 로그인하고 토큰을 발급받습니다.")
    @PostMapping("/login")
    public ResponseEntity<LoginDto.LoginResponse> login(
            @Valid @RequestBody LoginDto.LoginRequest request) {
        LoginDto.AuthContext context = authService.login(request);

        ResponseCookie cookie = ResponseCookie.from("refreshToken", context.getRefreshToken())
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(7 * 24 * 60 * 60)
                .sameSite("Strict")
                .build();

        LoginDto.LoginResponse response = LoginDto.LoginResponse.of(context.getMember(), context.getAccessToken());

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(response);
    }

    @Operation(summary = "로그아웃", description = "현재 세션을 종료하고 리프레시 토큰을 무효화합니다.")
    @PostMapping("/logout")
    public ResponseEntity logout(
            @CookieValue(name = "refreshToken", required = false) String refreshToken) {
        if (refreshToken != null) {
            String email = jwtTokenProvider.getEmailFromToken(refreshToken);
            authService.logout(email);
        }

        ResponseCookie cookie = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(0)
                .sameSite("Strict")
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .build();
    }

    @Operation(summary = "토큰 갱신", description = "리프레시 토큰을 사용하여 새로운 액세스 토큰을 발급받습니다.")
    @PostMapping("/refresh")
    public ResponseEntity<LoginDto.AuthContext> refreshAccessToken(
            @CookieValue(name = "refreshToken", required = false) String refreshToken
    ) {
        return ResponseEntity.ok(authService.refreshAccessToken(refreshToken));
    }
}
