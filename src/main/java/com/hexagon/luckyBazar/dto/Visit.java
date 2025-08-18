package com.hexagon.luckyBazar.dto;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
public class Visit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "delegate_id")
    private Delegate delegate;

    @ManyToOne
    @JoinColumn(name = "stall_id")
    private Stalls stall;

    private int dayNumber;
    private LocalDateTime visitedAt;
}
