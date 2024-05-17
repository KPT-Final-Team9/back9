package com.core.back9.entity;

import com.core.back9.common.entity.BaseEntity;
import com.core.back9.entity.constant.RatingType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(name = "management_scores")
public class Score extends BaseEntity {

	@Column(name = "score", nullable = false)
	private int score;

	@Column(name = "comment", nullable = false)
	private String comment;

	@Column(name = "bookmark", nullable = false)
	private boolean bookmark;

	@Enumerated(EnumType.STRING)
	@Column(name = "rating_type", nullable = false)
	private RatingType ratingType;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "room_id")
	private Room room;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "tenant_member_id")
	private Member member;

	@Builder
	private Score(int score, String comment, boolean bookmark, RatingType ratingType, Room room, Member member) {
		this.score = score;
		this.comment = comment;
		this.bookmark = bookmark;
		this.ratingType = ratingType;
		this.room = room;
		this.member = member;
	}
}