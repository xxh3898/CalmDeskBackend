package com.code808.calmdesk.domain.company.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.code808.calmdesk.domain.member.entity.Rank;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RankRepository extends JpaRepository<Rank, Long> {
    Optional<Rank> findByRankName(String rankName);
}
