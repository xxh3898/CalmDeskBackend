package com.code808.calmdesk.domain.callrecord.repository;

import com.code808.calmdesk.domain.callrecord.entity.ProfanityWord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProfanityWordRepository extends JpaRepository<ProfanityWord, Long> {
    
    /**
     * 활성화된 욕설 단어 목록 조회
     */
    List<ProfanityWord> findByActiveTrue();
}




