package com.core.back9.entity;

import com.core.back9.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(name = "settings")
public class Settings extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "rating_toggle")
    private boolean ratingToggle;

    @Column(name = "message")
    private String message;

    @Builder
    private Settings(boolean ratingToggle, String message) {
        this.ratingToggle = ratingToggle;
        this.message = message;
    }

}
