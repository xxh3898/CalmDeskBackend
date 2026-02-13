package com.code808.calmdesk.domain.auth.service;

import com.code808.calmdesk.domain.auth.dto.LoginDto;
import com.code808.calmdesk.domain.auth.dto.SignupDto;
import com.code808.calmdesk.domain.common.enums.CommonEnums;
import com.code808.calmdesk.domain.member.entity.Account;
import com.code808.calmdesk.domain.member.entity.Member;
import com.code808.calmdesk.domain.member.repository.AccountRepository;
import com.code808.calmdesk.domain.member.repository.MemberRepository;
import com.code808.calmdesk.global.exception.token.ExpiredTokenException;
import com.code808.calmdesk.global.exception.token.TokenNotFoundException;
import com.code808.calmdesk.global.security.JwtTokenProvider;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthServiceImpl implements AuthService {
    private final MemberRepository memberRepository;
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenService refreshTokenService;

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

        // 포인트몰 이용을 위해 회원당 계좌(Account) 1건 생성 (잔액 0)
        Account account = Account.builder()
                .member(savedMember)
                .accountLeave(0)
                .totalEarnedPoint(0)
                .totalSpentPoint(0)
                .build();
        accountRepository.save(account);

        String token = jwtTokenProvider.generateToken(
                savedMember.getEmail(),
                "TEMP"

        );
        return SignupDto.SignupResponse.of(savedMember, token);
    }

    @Override
    @Transactional
    public LoginDto.AuthContext login(LoginDto.LoginRequest request){
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

//        Long companyId = (member.getCompany() != null) ? member.getCompany().getCompanyId() : null;

        log.info("회사 존재 여부: {}", member.getCompany() != null);
//        log.info("추출된 CompanyId: {}", companyId);

        String accessToken = jwtTokenProvider.generateToken(
                member.getEmail(),
                role
//                companyId // ✨ 추가: 세 번째 인자로 companyId 전달
        );

        String refreshToken = jwtTokenProvider.generateRefreshToken(
                member.getEmail()
        );

        refreshTokenService.save(member.getEmail(), refreshToken);

        return new LoginDto.AuthContext(member, accessToken, refreshToken);
    }

//    private void createRefreshToken(Member member, String token ){
//        refreshTokenRepository.findByMember(member)
//                .ifPresent(refreshTokenRepository::delete);
//
//        RefreshToken refreshToken = RefreshToken.builder()
//                .token(token)
//                .expiryDate(jwtTokenProvider.getRefreshTokenExpiryDate())
//                .member(member)
//                .build();
//
//        refreshTokenRepository.save(refreshToken);
//    }

    @Override
    @Transactional
    public void logout(String email) {
        refreshTokenService.delete(email);
    }

//    public LoginDto.AuthContext refreshAccessToken(String refreshToken) {
//
//        Optional<Claims> claimsOpt = jwtTokenProvider.validateToken(refreshToken);
//        if(claimsOpt.isEmpty()){
//            throw new IllegalArgumentException("유효하지 않은 RefreshToken입니다.");
//        }
//
//        RefreshToken storedToken = refreshTokenRepository.findByToken(refreshToken)
//                .orElseThrow(()-> new TokenNotFoundException("RefreshToken을 찾을 수 없습니다."));
//
//        if(storedToken.isExpired()){
//            refreshTokenRepository.delete(storedToken);
//            throw new ExpiredTokenException("만료된 RefreshToken입니다.");
//        }
//
//        Member member = memberRepository.findByEmail(storedToken.getMember().getEmail())
//                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없는니다."));
//
//        String newAccessToken = jwtTokenProvider.generateToken(
//                member.getEmail(),
//                member.getRole().name()
//        );
//
//        return LoginDto.AuthContext.builder()
//                .accessToken(newAccessToken)
//                .build();
//    }

    @Override
    @Transactional
    public LoginDto.AuthContext refreshAccessToken(String refreshToken) {
        Optional<Claims> claimsOpt = jwtTokenProvider.validateToken(refreshToken);
        if (claimsOpt.isEmpty()) {
            throw new IllegalArgumentException("유효하지 않은 Refresh Token입니다.");
        }

        String email = claimsOpt.get().getSubject();

        String storedToken = refreshTokenService.get(email);
        if (storedToken == null) {
            throw new TokenNotFoundException("Refresh Token을 찾을 수 없습니다.");
        }

        if (!storedToken.equals(refreshToken)) {
            throw new IllegalArgumentException("Refresh Token이 일치하지 않습니다.");
        }

        if (jwtTokenProvider.isTokenExpired(refreshToken)) {
            refreshTokenService.delete(email);
            throw new ExpiredTokenException("만료된 Refresh Token입니다.");
        }

        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));


        Long companyId = (member.getCompany() != null) ? member.getCompany().getCompanyId() : null;

        String newAccessToken = jwtTokenProvider.generateToken(
                member.getEmail(),
                member.getRole().name()

        );

        return LoginDto.AuthContext.builder()
                .accessToken(newAccessToken)
                .build();
    }
}
