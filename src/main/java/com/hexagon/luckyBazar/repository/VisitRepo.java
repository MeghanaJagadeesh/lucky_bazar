package com.hexagon.luckyBazar.repository;

import com.hexagon.luckyBazar.dto.Delegate;
import com.hexagon.luckyBazar.dto.Visit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VisitRepo extends JpaRepository<Visit, Integer> {
    List<Visit> findByDelegate(Delegate delegate);

    @Query("SELECT v.delegate FROM Visit v WHERE v.stall.id = :stallId")
    List<Delegate> findDelegatesByStallId(@Param("stallId") int stallId);
}
