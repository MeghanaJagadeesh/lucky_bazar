package com.hexagon.luckyBazar.repository;

import com.hexagon.luckyBazar.dto.Delegate;
import com.hexagon.luckyBazar.dto.DelegateVisitDetailsDTO;
import com.hexagon.luckyBazar.dto.Visit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VisitRepo extends JpaRepository<Visit, Integer> {
    List<Visit> findByDelegate(Delegate delegate);

//    @Query("SELECT v.delegate FROM Visit v WHERE v.stall.id = :stallId")
//    List<Delegate> findDelegatesByStallId(@Param("stallId") int stallId);

//    @Query("SELECT DISTINCT new com.hexagon.luckyBazar.dto.DelegateVisitDetailsDTO(" +
//            "d.id, d.name, d.email, d.phoneNum, d.company, " +
//            "v.visitedAt, v.feedBack, d.createdAt) " +
//            "FROM Visit v " +
//            "JOIN v.delegate d " +
//            "WHERE v.stall.id = :stallId " +
//            "ORDER BY v.visitedAt DESC")
//    List<DelegateVisitDetailsDTO> findDelegatesByStallIdWithDetails(@Param("stallId") int stallId);

    @Query("SELECT new com.hexagon.luckyBazar.dto.DelegateVisitDetailsDTO(" +
            "d.id, d.name, d.email, d.company, " +
            "v.visitedAt, v.feedBack, d.createdAt) " +
            "FROM Visit v " +
            "JOIN v.delegate d " +
            "WHERE v.stall.id = :stallId " +
            "AND v.visitedAt = (" +
            "SELECT MAX(v2.visitedAt) " +
            "FROM Visit v2 " +
            "WHERE v2.delegate.id = d.id AND v2.stall.id = :stallId" +
            ") " +
            "ORDER BY v.visitedAt DESC")
    List<DelegateVisitDetailsDTO> findDelegatesByStallIdWithDetails(@Param("stallId") int stallId);

    @Query("""
                SELECT v.delegate
                FROM Visit v
                WHERE v.delegate.email NOT IN (SELECT w.email FROM Winners w)
                GROUP BY v.delegate
                HAVING COUNT(DISTINCT v.stall.id) = (SELECT COUNT(s) FROM Stalls s)
            """)
    List<Delegate> findEligibleDelegates();

    @Query("SELECT new com.hexagon.luckyBazar.dto.DelegateVisitDetailsDTO(" +
            "d.id, d.name, d.email, d.company, " +
            "v.visitedAt, v.feedBack, d.createdAt, " +
            "hv.ratings, hv.areaOfInterest, hv.insightGained, hv.suggestions, hv.followUp) " +
            "FROM Visit v " +
            "JOIN v.delegate d " +
            "JOIN HexagonVisit hv ON hv.visit.id = v.id " +
            "WHERE v.stall.id = :stallId " +
            "AND v.visitedAt = (" +
            "   SELECT MAX(v2.visitedAt) " +
            "   FROM Visit v2 " +
            "   WHERE v2.delegate.id = d.id AND v2.stall.id = :stallId" +
            ") " +
            "ORDER BY v.visitedAt DESC")
    List<DelegateVisitDetailsDTO> findHexagonDelegatesWithDetails(@Param("stallId") int stallId);

}
