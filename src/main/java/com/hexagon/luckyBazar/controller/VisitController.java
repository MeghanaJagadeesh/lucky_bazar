package com.hexagon.luckyBazar.controller;

import com.hexagon.luckyBazar.dto.Delegate;
import com.hexagon.luckyBazar.dto.Stalls;
import com.hexagon.luckyBazar.dto.VisitRequest;
import com.hexagon.luckyBazar.service.VisitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
public class VisitController {

    @Autowired
    VisitService visitService;

    @PostMapping("/add/stalls")
    public ResponseEntity<String> addStalls(@RequestBody Stalls stalls) {
        return visitService.addStalls(stalls);
    }

    @PostMapping("/delegate/visited/{day}")
    public ResponseEntity<Map<String, Object>> visited(@RequestBody VisitRequest request, @PathVariable int day) {
        return visitService.visit(request, day);
    }

    @GetMapping("/eligible/any-one-day")
    public List<Delegate> eligibleAnyOneDay() {
        return visitService.getEligibleAnyOneDay();
    }

    @GetMapping("/eligible/across-two-days")
    public List<Delegate> eligibleAcrossTwoDays() {
        return visitService.getEligibleAcrossTwoDays();
    }

    @GetMapping("/eligible/each-day")
    public List<Delegate> eligibleEachDay() {
        return visitService.getEligibleEachDay();
    }

    @GetMapping("/search/delegate")
    public List<Delegate> searchDelegate(@RequestParam String searchText) {
        return visitService.searchDelegates(searchText);
    }

    @GetMapping("/export/delegate/byStall")
    public List<Delegate> export(@RequestParam int stallId) {
        return visitService.exportDelegates(stallId);
    }
}
