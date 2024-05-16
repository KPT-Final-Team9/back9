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
@Table(name = "buildings")
public class Buildings extends BaseEntity {

    @Column(name = "name")
    private String name;

    @Column(name = "address")
    private String address;

    @Column(name = "zip_code")
    private String zipCode;

    @Builder
    private Buildings(String name, String address, String zipCode) {
        this.name = name;
        this.address = address;
        this.zipCode = zipCode;
    }

}
