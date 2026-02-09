package com.code808.calmdesk.domain.auth.repository;

import com.code808.calmdesk.domain.auth.entity.RefreshToken;
import com.code808.calmdesk.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByMember(Member member);

    Optional<RefreshToken> findByToken(String token);

    void deleteByToken(String token);

//    void deleteByMember(Member member);

    // 만료된 토큰 삭제 (배치 작업용, 선택)
    void deleteByExpiryDateBefore(LocalDateTime date);
}
