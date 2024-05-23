package com.core.back9.entity;

import com.core.back9.common.entity.BaseEntity;
import com.core.back9.entity.constant.Status;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(name = "settings")
public class Setting extends BaseEntity {

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "room_id")
	private Room room;

	@Column(name = "rating_toggle")
	private boolean ratingToggle;

	@Column(name = "encourage_message")
	private String encourageMessage;

	@Enumerated(EnumType.STRING)
	@Column
	private Status status;

	@Builder
	public Setting(Room room, boolean ratingToggle, String encourageMessage, Status status) {
		this.room = room;
		this.ratingToggle = ratingToggle;
		this.encourageMessage = encourageMessage;
		this.status = status;
	}
}
