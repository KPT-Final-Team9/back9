package com.core.back9.entity;

import com.core.back9.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(name = "buildings")
public class Building extends BaseEntity {

	@Column
	private String name;

	@Column
	private String address;

	@Column
	private String zipCode;

	@Builder
	private Building(String name, String address, String zipCode) {
		this.name = name;
		this.address = address;
		this.zipCode = zipCode;
	}

}
