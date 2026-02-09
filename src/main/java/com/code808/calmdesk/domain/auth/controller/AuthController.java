package com.code808.calmdesk.domain.auth.controller;

import com.code808.calmdesk.domain.auth.dto.SignupDto;
import com.code808.calmdesk.domain.auth.dto.LoginDto;
import com.code808.calmdesk.domain.auth.service.AuthService;
import com.code808.calmdesk.domain.member.entity.Member;

import com.code808.calmdesk.domain.member.repository.MemberRepository;
import jakarta.servlet.http.Cookie;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class AuthController {
    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<SignupDto.SignupResponse> signup(
            @Valid @RequestBody SignupDto.SignupRequest request) {
        return ResponseEntity.ok(authService.signup(request));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginDto.LoginResponse> login(
            @Valid @RequestBody LoginDto.LoginRequest request) {
        LoginDto.AuthContext context = authService.login(request);

        ResponseCookie cookie = ResponseCookie.from("refreshToken", context.getRefreshToken())
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(7 * 24 * 60 * 60)
                .sameSite("Strict")
                .build();

        LoginDto.LoginResponse response = LoginDto.LoginResponse.of(context.getMember(), context.getAccessToken());

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(response);
    }

    @PostMapping("/logout")
    public ResponseEntity logout(
            @CookieValue(name = "refreshToken", required = false) String refreshToken) {
        if(refreshToken != null) {
            authService.logout(refreshToken);
        }

        ResponseCookie cookie = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0)
                .sameSite("Strict")
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .build();
    }

    @PostMapping("/refresh")
    public ResponseEntity<LoginDto.AuthContext> refreshAccessToken(
            @CookieValue(name = "refreshToken", required = false) String refreshToken
    ){
        return  ResponseEntity.ok(authService.refreshAccessToken(refreshToken));
    }
}
