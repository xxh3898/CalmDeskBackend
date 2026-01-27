package com.code808.calmdesk.domain.gifticon.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.code808.calmdesk.domain.member.entity.Account;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {

    Optional<Account> findByMember_MemberId(Long memberId);

}
