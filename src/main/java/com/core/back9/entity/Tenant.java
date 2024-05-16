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
@Table(name = "tenants")
public class Tenant extends BaseEntity {

	@Column(name = "name")
	private String name;

	@Column(name = "company_number")
	private String companyNumber;

	@Builder
	private Tenant(String name, String companyNumber) {
		this.name = name;
		this.companyNumber = companyNumber;
	}

}
