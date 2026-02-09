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
}
