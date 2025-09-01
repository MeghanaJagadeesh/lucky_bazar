package com.hexagon.luckyBazar.dto;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;

@Data
@Entity
public class Visit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "delegate_id")
    private Delegate delegate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stall_id")
    private Stalls stall;

    private int dayNumber;

    @Column(length = 3000)
    private String feedBack;

    private ZonedDateTime visitedAt;

}
