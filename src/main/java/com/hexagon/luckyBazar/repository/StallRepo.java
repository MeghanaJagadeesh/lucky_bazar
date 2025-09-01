package com.hexagon.luckyBazar.repository;

import com.hexagon.luckyBazar.dto.Stalls;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StallRepo extends JpaRepository<Stalls, Integer> {
    Stalls findByStallCode(String stallCode);
    Stalls findByNameAndStallCode(String name, String stallCode);
}
