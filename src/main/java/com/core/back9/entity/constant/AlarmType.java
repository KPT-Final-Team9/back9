package com.core.back9.entity.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum AlarmType {

	COMMON("", ""),
	COMPLAINT_PENDING("민원", "민원이 등록되었습니다."),
	COMPLAINT_RECEIVED("민원", "민원이 정상적으로 접수 완료되었습니다."),
	COMPLAINT_IN_PROGRESS("민원", "민원이 처리중입니다."),
	COMPLAINT_COMPLETED("민원", "민원 처리가 완료되었습니다."),
	COMPLAINT_REJECTED("민원", "민원이 반려되었습니다"),
	;

	private final String label;

	private final String description;

}
