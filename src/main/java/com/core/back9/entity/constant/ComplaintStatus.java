package com.core.back9.entity.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ComplaintStatus {
	PENDING("접수 대기"),
	RECEIVED("접수 완료"),
	IN_PROGRESS("처리 진행중"),
	COMPLETED("처리 완료"),
	REJECTED("반려");

	private final String label;
}
