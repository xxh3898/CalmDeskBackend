package com.example.demo.repository.custom;

import com.example.demo.entity.Member;
import com.example.demo.entity.PointHistory;

import java.util.List;

public interface PointHistoryRepositoryCustom {
    List<PointHistory> findByMember(Member member);
}
