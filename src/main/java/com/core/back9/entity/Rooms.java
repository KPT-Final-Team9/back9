package com.core.back9.entity;

import com.core.back9.common.entity.BaseEntity;
import com.core.back9.entity.constant.RoomStatus;
import com.core.back9.entity.constant.Usage;
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

    @Column(name = "name")
    private String name;

    @Column(name = "floor")
    private String floor;

    @Column(name = "area")
    private float area; // 건물 면적(제곱 미터)

    @Enumerated(EnumType.STRING)
    @Column(name = "room_status")
    private RoomStatus roomStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "usage")
    private Usage usage; // 호실의 용도를 구분하는 Enum

    @Column(name = "rating")
    private float rating;

    @Builder
    public Rooms(String name, String floor, float area, RoomStatus roomStatus, Usage usage, float rating) {
        this.name = name;
        this.floor = floor;
        this.area = area;
        this.roomStatus = roomStatus;
        this.usage = usage;
        this.rating = rating;
    }
}
