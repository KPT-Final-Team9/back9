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

	@Column(name = "rating_toggle")
	private boolean ratingToggle;

	@Column(name = "encourage_message")
	private String encourageMessage;

	@Enumerated(EnumType.STRING)
	@Column
	private Status status;

	@Builder
	public Setting(String encourageMessage) {
		this.ratingToggle = true;
		this.encourageMessage = encourageMessage == null
		  ? "안녕하세요\n\n평가를 통해\n오피스에서 함께하는 시간을\n더 즐겁고, 더 편리하게 만들어보아요!"
		  : encourageMessage;
		this.status = Status.REGISTER;
	}

	public void updateToggle(boolean value) {
		this.ratingToggle = value;
	}

	public void updateEncourageMessage(String encourageMessage) {
		this.encourageMessage = encourageMessage;
	}

}
