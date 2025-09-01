package com.hexagon.luckyBazar.dto;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class HexagonVisit {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private int id;

        @OneToOne
        @JoinColumn(name = "visit_id")
        private Visit visit;

        private int ratings;
        private String areaOfInterest;
        private String insightGained;
        private String suggestions;
        private String followUp;

}
