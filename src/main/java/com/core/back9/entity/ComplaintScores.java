package com.core.back9.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(name = "complaint_scores")
public class ComplaintScores {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "score", nullable = false)
    private int score;

    @Column(name = "comment", nullable = false)
    private String comment;

    @Column(name = "bookmark")
    private boolean bookmark;

    @Builder
    private ComplaintScores(int score, String comment, boolean bookmark) {
        this.score = score;
        this.comment = comment;
        this.bookmark = bookmark;
    }

}
