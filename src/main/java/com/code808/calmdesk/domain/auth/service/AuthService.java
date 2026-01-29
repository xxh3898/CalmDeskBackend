package com.code808.calmdesk.domain.auth.service;

import com.code808.calmdesk.domain.auth.dto.LoginDto;
import com.code808.calmdesk.domain.auth.dto.SignupDto;

public interface AuthService {
    SignupDto.SignupResponse signup(SignupDto.SignupRequest request);
    LoginDto.LoginResponse login(LoginDto.LoginRequest response);
}
