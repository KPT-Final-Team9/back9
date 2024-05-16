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
public class Setting extends BaseEntity {

	@Column(name = "rating_toggle")
	private boolean ratingToggle;

	@Column(name = "message")
	private String message;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "room_id")
	private Room room;

	@Builder
	public Setting(boolean ratingToggle, String message, Room room) {
		this.ratingToggle = ratingToggle;
		this.message = message;
		this.room = room;
	}
}
