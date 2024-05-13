package com.core.back9.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(name = "settings")
public class Settings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "Score_toggle")
    private boolean ScoreToggle;

    @Column(name = "message")
    private String message;

    @Builder
    private Settings(boolean scoreToggle, String message) {
        ScoreToggle = scoreToggle;
        this.message = message;
    }

}
