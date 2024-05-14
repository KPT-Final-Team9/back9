package com.core.back9.entity;

import com.core.back9.common.entity.BaseEntity;
import com.core.back9.entity.constant.RoomStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(name = "rooms")
public class Rooms extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "floor")
    private String floor;

    @Column(name = "area")
    private float area; // 건물 면적(제곱 미터)

    @Enumerated(EnumType.STRING)
    @Column(name = "room_status")
    private RoomStatus roomStatus;

    @Column(name = "rating")
    private float rating;

    @Builder
    private Rooms(String name, String floor, float area, RoomStatus roomStatus, float rating) {
        this.name = name;
        this.floor = floor;
        this.area = area;
        this.roomStatus = roomStatus;
        this.rating = rating;
    }

}
