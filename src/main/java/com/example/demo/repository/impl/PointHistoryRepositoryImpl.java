package com.example.demo.repository.impl;

import com.example.demo.entity.Member;
import com.example.demo.entity.PointHistory;
import com.example.demo.repository.custom.PointHistoryRepositoryCustom;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class PointHistoryRepositoryImpl implements PointHistoryRepositoryCustom {

    private final EntityManager entityManager;

    @Override
    public List<PointHistory> findByMember(Member member) {
        String jpql = "SELECT ph FROM POINT_HISTORY ph WHERE ph.missionList.member = :member ORDER BY ph.createdDate DESC";
        return entityManager.createQuery(jpql, PointHistory.class)
                .setParameter("member", member)
                .getResultList();
    }
}
