package com.core.back9.entity;

import com.core.back9.common.entity.BaseEntity;
import com.core.back9.dto.RoomDTO;
import com.core.back9.entity.constant.Status;
import com.core.back9.entity.constant.Usage;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(name = "rooms")
public class Room extends BaseEntity {

	@Column(name = "name")
	private String name;

	@Column(name = "floor")
	private String floor;

	@Column(name = "area")
	private float area; // 건물 면적(제곱 미터)

	@Enumerated(EnumType.STRING)
	@Column(name = "room_usage")
	private Usage usage; // 호실의 용도를 구분하는 Enum

	@Enumerated(EnumType.STRING)
	@Column(name = "status")
	private Status status;

	@Column(name = "rating")
	private float rating;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "building_id")
	private Building building;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id")
	private Member member;

	@OneToMany(mappedBy = "room", cascade = CascadeType.REMOVE)
	private List<Contract> contracts = new ArrayList<>();

	@OneToOne(
	  cascade = {CascadeType.PERSIST, CascadeType.REMOVE},
	  orphanRemoval = true
	)
	private Setting setting;

	@Builder
	private Room(String name, String floor, float area, Usage usage, float rating, Building building, Member member, List<Contract> contracts, Setting setting) {
		this.name = name;
		this.floor = floor;
		this.area = area;
		this.usage = usage;
		this.rating = rating;
		this.building = building;
		this.member = member;
		this.contracts = contracts;
		this.status = Status.REGISTER;
		this.setting = setting;
	}

	public void update(RoomDTO.Request request) {
		this.name = request.getName();
		this.floor = request.getFloor();
		this.area = request.getArea();
		this.usage = request.getUsage();
	}

	public void delete() {
		this.status = Status.UNREGISTER;
	}

	public void setOwner(Member member) {
		this.member = member;
	}

	public void addContract(Contract contract) {
		this.contracts.add(contract);
	}

	public List<Contract> getContractList() {
		return this.contracts;
	}

}
