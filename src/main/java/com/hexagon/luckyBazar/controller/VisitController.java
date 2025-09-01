package com.hexagon.luckyBazar.controller;

import com.hexagon.luckyBazar.dto.*;
import com.hexagon.luckyBazar.service.VisitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
public class VisitController {

    @Autowired
    VisitService visitService;


    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestParam String username, @RequestParam String password){
        return visitService.login(username, password);
    }

    @GetMapping("/exportAll/delegateData")
    public ResponseEntity<?> exportAllData(){
        return visitService.export();
    }

    @PostMapping("/add/stalls")
    public ResponseEntity<String> addStalls(@RequestBody Stalls stalls) {
        return visitService.addStalls(stalls);
    }

    @PostMapping("/delegate/visited/{day}")
    public ResponseEntity<Map<String, Object>> visited(@RequestBody VisitRequest request, @PathVariable int day) {
        return visitService.visit(request, day);
    }

    @GetMapping("/eligible/any-one-day")
    public Set<Delegate> eligibleAnyOneDay() {
        return visitService.getEligibleAnyOneDay();
    }

    @GetMapping("/eligible/across-two-days")
    public List<Delegate> eligibleAcrossTwoDays() {
        return visitService.getEligibleAcrossTwoDays();
    }

    @GetMapping("/eligible/each-day")
    public Set<Delegate> eligibleEachDay() {
        return visitService.getEligibleEachDay();
    }

    @GetMapping("/search/delegate")
    public List<Delegate> searchDelegate(@RequestParam String searchText) {
        return visitService.searchDelegates(searchText);
    }

    @GetMapping("/export/delegate/byStall")
    public List<DelegateVisitDetailsDTO> export(@RequestParam int stallId) {
        return visitService.exportDelegates(stallId);
    }

    @GetMapping("/get/allstalls")
    public List<Stalls> getStalls(){
        return visitService.getAllStalls();
    }

    @PostMapping("/declare/winners")
    public ResponseEntity<?> declare(@RequestBody Winners winners){
       return visitService.saveWinners(winners);
    }

    @GetMapping("/get/winners")
    public List<Winners> getWinners(){
        return visitService.getWinners();
    }
}
