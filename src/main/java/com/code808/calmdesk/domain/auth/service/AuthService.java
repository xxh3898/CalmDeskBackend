package com.code808.calmdesk.domain.auth.service;

import com.code808.calmdesk.domain.auth.dto.LoginDto;
import com.code808.calmdesk.domain.auth.dto.SignupDto;

public interface AuthService {
    SignupDto.SignupResponse signup(SignupDto.SignupRequest request);
    LoginDto.AuthContext login(LoginDto.LoginRequest response);
    void logout(String refreshToken);
    LoginDto.AuthContext refreshAccessToken(String refreshToken);
}
