package com.core.back9.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(name = "management_scores")
public class ManagementScores {

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
    private ManagementScores(int score, String comment, boolean bookmark) {
        this.score = score;
        this.comment = comment;
        this.bookmark = bookmark;
    }

}
