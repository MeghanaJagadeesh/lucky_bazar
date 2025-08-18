package com.hexagon.luckyBazar.service;

import com.hexagon.luckyBazar.dto.Delegate;
import com.hexagon.luckyBazar.dto.Stalls;
import com.hexagon.luckyBazar.dto.Visit;
import com.hexagon.luckyBazar.dto.VisitRequest;
import com.hexagon.luckyBazar.repository.DelegateRepo;
import com.hexagon.luckyBazar.repository.StallRepo;
import com.hexagon.luckyBazar.repository.VisitRepo;
import jakarta.persistence.criteria.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


@Service
public class VisitService {

    @Autowired
    StallRepo stallRepo;

    @Autowired
    DelegateRepo delegateRepo;

    @Autowired
    VisitRepo visitRepo;

    public ResponseEntity<String> addStalls(Stalls stalls) {
        stallRepo.save(stalls);
        return new ResponseEntity<String>("Stall added successfully", HttpStatus.OK);
    }

    public ResponseEntity<Map<String, Object>> visit(VisitRequest request, int dayNum) {
        System.out.println(request);
        try {
            System.out.println("service");
            Delegate exdelegate = delegateRepo.findByEmail(request.getEmail());
            System.out.println("ex = " + exdelegate);
            Delegate delegate;
            if (exdelegate == null) {
                System.out.println("null");
                Delegate newdel = new Delegate();
                newdel.setCompany(request.getCompany());
                newdel.setName(request.getName());
                newdel.setPhoneNum(request.getPhoneNum());
                newdel.setCompany(request.getCompany());
                newdel.setCreatedAt(LocalDateTime.now());
                newdel.setEmail(request.getEmail());
                delegate = delegateRepo.save(newdel);
            } else {
                System.out.println("else");
                delegate = exdelegate;
            }

            Stalls stalls = stallRepo.findByStallCode(request.getStall_code());
            Visit visit = new Visit();
            visit.setDelegate(delegate);
            visit.setStall(stalls);
            visit.setDayNumber(dayNum);
            visit.setVisitedAt(LocalDateTime.now());
            visitRepo.save(visit);

            Map<String, Object> map = new HashMap<>();
            map.put("status", "success");
            map.put("message", "added successfully");
            map.put("code", HttpStatus.OK.value());
            map.put("created_at", LocalDateTime.now());
            return new ResponseEntity<>(map, HttpStatus.OK);
        } catch (Exception e) {
            Map<String, Object> map = new HashMap<>();
            map.put("status", "error");
            map.put("message", "Something went wrong");
            map.put("code", HttpStatus.INTERNAL_SERVER_ERROR.value());
            map.put("created_at", LocalDateTime.now());
            return new ResponseEntity<>(map, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private List<Stalls> getAllStalls() {
        return stallRepo.findAll();
    }

    public List<Delegate> getEligibleAnyOneDay() {
        List<Delegate> eligible = new ArrayList<>();
        List<Stalls> allStalls = getAllStalls();
        int totalStalls = allStalls.size();

        for (Delegate delegate : delegateRepo.findAll()) {
            // Group visits by day
            Map<Integer, Set<Integer>> dayToStalls = new HashMap<>();
            for (Visit visit : visitRepo.findByDelegate(delegate)) {
                dayToStalls
                        .computeIfAbsent(visit.getDayNumber(), k -> new HashSet<>())
                        .add(visit.getStall().getId());
            }

            // Check if any day has all stalls visited
            boolean allInOneDay = dayToStalls.values().stream()
                    .anyMatch(stallsVisited -> stallsVisited.size() == totalStalls);

            if (allInOneDay) {
                eligible.add(delegate);
            }
        }
        return eligible;
    }

    public List<Delegate> getEligibleAcrossTwoDays() {
        List<Delegate> eligible = new ArrayList<>();
        List<Stalls> allStalls = getAllStalls();
        int totalStalls = allStalls.size();

        for (Delegate delegate : delegateRepo.findAll()) {
            Set<Integer> stallsVisited = visitRepo.findByDelegate(delegate)
                    .stream()
                    .map(v -> v.getStall().getId())
                    .collect(Collectors.toSet());

            if (stallsVisited.size() == totalStalls) {
                eligible.add(delegate);
            }
        }
        return eligible;
    }
    static int totalEventDays=2;
    public List<Delegate> getEligibleEachDay() {
        List<Delegate> eligible = new ArrayList<>();
        List<Stalls> allStalls = getAllStalls();
        int totalStalls = allStalls.size();
        for (Delegate delegate : delegateRepo.findAll()) {
            Map<Integer, Set<Integer>> dayToStalls = new HashMap<>();
            for (Visit visit : visitRepo.findByDelegate(delegate)) {
                dayToStalls
                        .computeIfAbsent(visit.getDayNumber(), k -> new HashSet<>())
                        .add(visit.getStall().getId());
            }
            boolean allEachDay = true;
            for (int day = 1; day <= totalEventDays; day++) {
                Set<Integer> stallsVisited = dayToStalls.getOrDefault(day, Collections.emptySet());
                if (stallsVisited.size() != totalStalls) {
                    allEachDay = false;
                    break;
                }
            }
            if (allEachDay) {
                eligible.add(delegate);
            }
        }
        return eligible;
    }

    public List<Delegate> searchDelegates(String searchText) {
        Specification<Delegate> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            String likePattern = "%" + searchText.toLowerCase() + "%"; // sequence preserved
            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), likePattern));
            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("email")), likePattern));
            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("company")), likePattern));
            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("phoneNum")), likePattern));
            return criteriaBuilder.or(predicates.toArray(new Predicate[0]));
        };
        return delegateRepo.findAll(spec);
    }


    public List<Delegate> exportDelegates(int stallId) {
       return visitRepo.findDelegatesByStallId(stallId);
    }


}
