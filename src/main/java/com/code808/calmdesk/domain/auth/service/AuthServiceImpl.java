package com.code808.calmdesk.domain.auth.service;

import com.code808.calmdesk.domain.auth.dto.LoginDto;
import com.code808.calmdesk.domain.auth.dto.SignupDto;
import com.code808.calmdesk.domain.common.enums.CommonEnums;
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

        String token = jwtTokenProvider.generateToken(
                savedMember.getEmail(),
                "TEMP"
        );
        return SignupDto.SignupResponse.of(savedMember, token);
    }

    @Override
    @Transactional
    public LoginDto.LoginResponse login(LoginDto.LoginRequest request){
        Member member = memberRepository.findEmailWithDetails(request.getEmail())
                .orElseThrow(()-> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        if(!passwordEncoder.matches(request.getPassword(), member.getPassword())){
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        if (member.getCompany() != null) {
            if (member.getStatus() == CommonEnums.Status.N) {
                throw new IllegalArgumentException("입사 승인 대기 중입니다. 관리자 승인 후 로그인할 수 있습니다.");
            }
        }

        String role = (member.getRole() != null)
            ? member.getRole().name()
            : "TEMP";

        String token = jwtTokenProvider.generateToken(
                member.getEmail(),
                role
        );

        return LoginDto.LoginResponse.of(member, token);
    }

}
