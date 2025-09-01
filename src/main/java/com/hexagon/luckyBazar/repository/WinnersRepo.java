package com.hexagon.luckyBazar.repository;

import com.hexagon.luckyBazar.dto.Winners;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WinnersRepo extends JpaRepository<Winners, Integer> {
    @Query("select w.email from Winners w")
    List<String> findAllWinnerEmails();
}
