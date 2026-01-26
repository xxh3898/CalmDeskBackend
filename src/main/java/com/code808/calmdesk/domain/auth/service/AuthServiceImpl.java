package com.code808.calmdesk.domain.auth.service;

import com.code808.calmdesk.domain.auth.dto.LoginDto;
import com.code808.calmdesk.domain.auth.dto.SignupDto;
import com.code808.calmdesk.domain.member.entity.Member;

import com.code808.calmdesk.domain.member.repository.MemberRepository;

import com.code808.calmdesk.global.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.security.crypto.password.PasswordEncoder;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthServiceImpl implements AuthService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    @Transactional
    public SignupDto.SignupResponse signup(SignupDto.SignupRequest request){
        //에외처리 아직
        String encodedPassword = passwordEncoder.encode(request.getPassword());

        Member member = Member.builder()
                .email(request.getEmail())
                .password(encodedPassword)
                .name(request.getName())
                .phone(request.getPhone())
                .build();

        Member savedMember = memberRepository.save(member);

        return SignupDto.SignupResponse.of(savedMember);
    }
}
