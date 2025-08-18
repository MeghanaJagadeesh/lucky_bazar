package com.hexagon.luckyBazar.repository;

import com.hexagon.luckyBazar.dto.Delegate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DelegateRepo extends JpaRepository<Delegate, Integer>, JpaSpecificationExecutor<Delegate> {
    Delegate findByEmail(String email);
}

