package com.example.demo.repository.impl;

import com.example.demo.entity.Member;
import com.example.demo.repository.custom.MemberRepositoryCustom;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepositoryCustom {

    private final EntityManager entityManager;

    @Override
    public Optional<Member> findByEmail(String email) {
        String jpql = "SELECT m FROM MEMBER m WHERE m.email = :email";
        return entityManager.createQuery(jpql, Member.class)
                .setParameter("email", email)
                .getResultStream()
                .findFirst();
    }

    @Override
    public boolean existsByEmail(String email) {
        String jpql = "SELECT COUNT(m) > 0 FROM MEMBER m WHERE m.email = :email";
        return entityManager.createQuery(jpql, Boolean.class)
                .setParameter("email", email)
                .getSingleResult();
    }

    @Override
    public boolean existsByPhone(String phone) {
        String jpql = "SELECT COUNT(m) > 0 FROM MEMBER m WHERE m.phone = :phone";
        return entityManager.createQuery(jpql, Boolean.class)
                .setParameter("phone", phone)
                .getSingleResult();
    }
}
