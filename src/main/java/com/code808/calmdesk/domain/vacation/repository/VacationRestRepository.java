package com.code808.calmdesk.domain.vacation.repository;

import com.code808.calmdesk.domain.vacation.entity.VacationRest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface VacationRestRepository extends JpaRepository<VacationRest, Long> {

}
