package com.core.back9.entity;

import com.core.back9.common.entity.BaseEntity;
import com.core.back9.dto.BuildingDTO;
import com.core.back9.entity.constant.Status;
import jakarta.persistence.*;
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

	@Enumerated(EnumType.STRING)
	@Column
	private Status status;

	@Builder
	private Building(String name, String address, String zipCode) {
		this.name = name;
		this.address = address;
		this.zipCode = zipCode;
		this.status = Status.REGISTER;
	}

	public void update(BuildingDTO.Request request) {
		this.name = request.getName();
		this.address = request.getAddress();
		this.zipCode = request.getZipCode();
	}

	public void delete() {
		this.status = Status.UNREGISTER;
	}

}
