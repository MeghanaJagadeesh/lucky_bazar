package com.hexagon.luckyBazar.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.time.ZonedDateTime;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DelegateVisitDetailsDTO {
    private int delegateId;
    private String name;
    private String email;
    private String company;
    private ZonedDateTime visitedAt;
    private String feedBack;
    private ZonedDateTime createdAt;

    // Extra only for Hexagon
    private Integer ratings;
    private String areaOfInterest;
    private String insightGained;
    private String suggestions;
    private String followUp;

    // Constructor for normal stalls
    public DelegateVisitDetailsDTO(int delegateId, String name, String email, String company,
                                   ZonedDateTime visitedAt, String feedBack,
                                   ZonedDateTime createdAt) {
        this.delegateId = delegateId;
        this.name = name;
        this.email = email;
        this.company = company;
        this.visitedAt = visitedAt;
        this.feedBack = feedBack;
        this.createdAt = createdAt;
    }

    // Constructor for Hexagon stalls
    public DelegateVisitDetailsDTO(int delegateId, String name, String email, String company,
                                   ZonedDateTime visitedAt, String feedBack,
                                   ZonedDateTime createdAt,
                                   Integer ratings, String areaOfInterest,
                                   String insightGained, String suggestions, String followUp) {
        this(delegateId, name, email, company, visitedAt, feedBack, createdAt);
        this.ratings = ratings;
        this.areaOfInterest = areaOfInterest;
        this.insightGained = insightGained;
        this.suggestions = suggestions;
        this.followUp = followUp;
    }
}
