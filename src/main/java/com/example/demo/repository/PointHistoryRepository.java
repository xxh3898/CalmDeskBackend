package com.example.demo.repository;

import com.example.demo.entity.PointHistory;
import com.example.demo.repository.custom.PointHistoryRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PointHistoryRepository extends JpaRepository<PointHistory, Long>, PointHistoryRepositoryCustom {
}
