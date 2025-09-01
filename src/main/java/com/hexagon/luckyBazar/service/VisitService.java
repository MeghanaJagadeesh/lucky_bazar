package com.hexagon.luckyBazar.service;

import com.hexagon.luckyBazar.dto.*;
import com.hexagon.luckyBazar.repository.*;
import jakarta.persistence.criteria.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
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

    @Autowired
    WinnersRepo winnersRepo;

    @Autowired
    HexagonVisitRepo hexagonVisitRepo;

    static String adminEmail = "admin@gmail.com";
    static String adminPassword = "Admin@0700";

    public ResponseEntity<String> addStalls(Stalls stalls) {
        stallRepo.save(stalls);
        return new ResponseEntity<String>("Stall added successfully", HttpStatus.OK);
    }

    public ResponseEntity<Map<String, Object>> visit(VisitRequest request, int dayNum) {
        try {
            Delegate exdelegate = delegateRepo.findByEmail(request.getEmail());
            Delegate delegate;
            if (exdelegate == null) {
                Delegate newdel = new Delegate();
                newdel.setCompany(request.getCompany());
                newdel.setName(request.getName());
                newdel.setCompany(request.getCompany());
                newdel.setCreatedAt(ZonedDateTime.now(ZoneId.of("Asia/Kolkata")));
                newdel.setEmail(request.getEmail());
                delegate = delegateRepo.save(newdel);
            } else {
                delegate = exdelegate;
            }

            Stalls stalls = stallRepo.findByStallCode(request.getStall_code());
            Visit visit = new Visit();
            visit.setDelegate(delegate);
            visit.setStall(stalls);
            visit.setDayNumber(dayNum);
            visit.setVisitedAt(ZonedDateTime.now(ZoneId.of("Asia/Kolkata")));
            visit.setFeedBack(request.getFeedBack());
            visitRepo.save(visit);

            if (stalls.getStallCode().equalsIgnoreCase("sHexa011")) {
                HexagonVisit hexagonVisit = new HexagonVisit();
                hexagonVisit.setVisit(visit);
                hexagonVisit.setRatings(request.getRatings());
                hexagonVisit.setAreaOfInterest(request.getAreaOfInterest());
                hexagonVisit.setInsightGained(request.getInsightGained());
                hexagonVisit.setSuggestions(request.getSuggestions());
                hexagonVisit.setFollowUp(request.getFollowUp());
                hexagonVisitRepo.save(hexagonVisit);
            }

            Map<String, Object> map = new HashMap<>();
            map.put("status", "success");
            map.put("message", "added successfully");
            map.put("code", HttpStatus.OK.value());
            map.put("created_at", LocalDateTime.now());
            return new ResponseEntity<>(map, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> map = new HashMap<>();
            map.put("status", "error");
            map.put("message", "Something went wrong");
            map.put("code", HttpStatus.INTERNAL_SERVER_ERROR.value());
            map.put("created_at", LocalDateTime.now());
            return new ResponseEntity<>(map, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public List<Stalls> getAllStalls() {
        return stallRepo.findAll();
    }

    public Set<Delegate> getEligibleAnyOneDay() {
        Set<Delegate> eligible = new HashSet<>();
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
//        Set<Delegate> eligible = new HashSet<>();
//        List<Stalls> allStalls = getAllStalls();
//        int totalStalls = allStalls.size();
//        for (Delegate delegate : delegateRepo.findAll()) {
//            Set<Integer> stallsVisited = visitRepo.findByDelegate(delegate).stream().map(v -> v.getStall().getId()).collect(Collectors.toSet());
//            if (stallsVisited.size() == totalStalls) {
//                eligible.add(delegate);
//            }
//        }
//        return eligible;

//        Set<Delegate> eligible = new HashSet<>();
//        int totalStalls = getAllStalls().size();
//
//        // Fetch all visits in one go
//        List<Visit> allVisits = visitRepo.findAll();
//
//        // Group visits by delegate
//        Map<Integer, Set<Integer>> delegateStalls = allVisits.stream()
//                .collect(Collectors.groupingBy(
//                        v -> v.getDelegate().getId(),
//                        Collectors.mapping(v -> v.getStall().getId(), Collectors.toSet())
//                ));
//
//        // Now check eligibility
//        for (Map.Entry<Integer, Set<Integer>> entry : delegateStalls.entrySet()) {
//            if (entry.getValue().size() == totalStalls) {
//                delegateRepo.findById(entry.getKey()).ifPresent(eligible::add);
//            }
//        }
//
//        return eligible;
        return visitRepo.findEligibleDelegates();
    }

    static int totalEventDays = 2;

    public Set<Delegate> getEligibleEachDay() {
        Set<Delegate> eligible = new TreeSet<>();
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
            String likePattern = "%" + searchText.toLowerCase() + "%"; // exact substring, not scattered
            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), likePattern));
            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("email")), likePattern));
            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("company")), likePattern));
            return criteriaBuilder.or(predicates.toArray(new Predicate[0]));
        };
        return delegateRepo.findAll(spec);
    }


//    public List<DelegateVisitDetailsDTO> exportDelegates(int stallId) {
//        Stalls stall = stallRepo.findById(stallId).orElseGet(null);
//        if (stall != null) {
//            if (stall.getStallCode().equalsIgnoreCase("sHexa011")) {
//                return visitRepo.findHexagonDelegatesWithDetails(stallId);
//            } else {
//                return visitRepo.findDelegatesByStallIdWithDetails(stallId);
//            }
//        }
//        return new ArrayList<>();
//    }

    @Cacheable(value = "stallDelegates", key = "#stallId")
    public List<DelegateVisitDetailsDTO> exportDelegates(int stallId) {
        Optional<Stalls> stallOpt = stallRepo.findById(stallId);
        if (stallOpt.isEmpty()) {
            return new ArrayList<>();
        }

        Stalls stall = stallOpt.get();
        if ("sHexa011".equalsIgnoreCase(stall.getStallCode())) {
            return visitRepo.findHexagonDelegatesWithDetails(stallId);
        } else {
            return visitRepo.findDelegatesByStallIdWithDetails(stallId);
        }
    }


    public ResponseEntity<Map<String, Object>> login(String username, String password) {
        Map<String, Object> map = new HashMap<>();
        if (username.equalsIgnoreCase(adminEmail) && password.equalsIgnoreCase(adminPassword)) {
            map.put("message", "admin login");
            map.put("code", HttpStatus.OK.value());
            map.put("status", "success");
            map.put("role", "admin");
            return ResponseEntity.ok().body(map);
        } else if (stallRepo.findByNameAndStallCode(username, password) != null) {
            Stalls stall = stallRepo.findByNameAndStallCode(username, password);
            map.put("message", "Stall Head login");
            map.put("code", HttpStatus.OK.value());
            map.put("status", "success");
            map.put("role", "stall");
            map.put("data", stall);
            return ResponseEntity.ok().body(map);
        } else {
            map.put("message", "Invalid Email or Password");
            map.put("code", HttpStatus.BAD_REQUEST.value());
            map.put("status", "fail");
            return ResponseEntity.badRequest().body(map);
        }
    }

    public ResponseEntity<?> export() {
        return ResponseEntity.ok(delegateRepo.findAll());
    }

    public ResponseEntity<?> saveWinners(Winners winners) {
        winnersRepo.save(winners);
        return ResponseEntity.ok("winners data saved successfully");
    }

    public List<Winners> getWinners() {
        return winnersRepo.findAll();
    }

}
