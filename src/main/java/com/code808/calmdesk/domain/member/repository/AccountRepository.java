package com.code808.calmdesk.domain.member.repository;

import com.code808.calmdesk.domain.member.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {

    Optional<Account> findByMemberMemberId(Long memberId);

    Optional<Account> findByMember_MemberId(Long memberId);

}


