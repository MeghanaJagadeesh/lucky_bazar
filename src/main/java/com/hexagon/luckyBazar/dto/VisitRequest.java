package com.hexagon.luckyBazar.dto;

import lombok.Data;

@Data
public class VisitRequest {
    private String stallId;
    private String stallName;
    private String stall_code;
    private String id;
    private String name;
    private String email;
    private String company;
    private String feedBack;

    private Integer ratings;
    private String areaOfInterest;
    private String insightGained;
    private String suggestions;
    private String followUp;
}
