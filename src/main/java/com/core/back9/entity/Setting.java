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
@Table(name = "settings")
public class Setting extends BaseEntity {

	@Column(name = "rating_toggle")
	private boolean ratingToggle;

	@Column(name = "message")
	private String message;

	@Builder
	private Setting(boolean ratingToggle, String message) {
		this.ratingToggle = ratingToggle;
		this.message = message;
	}

}
