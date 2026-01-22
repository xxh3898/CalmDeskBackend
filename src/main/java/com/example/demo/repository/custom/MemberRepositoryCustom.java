package com.example.demo.repository.custom;

import com.example.demo.entity.Member;

import java.util.Optional;

public interface MemberRepositoryCustom {
    Optional<Member> findByEmail(String email);
    boolean existsByEmail(String email);
    boolean existsByPhone(String phone);
}
