package com.core.back9.entity.constant;

public enum ContractStatus {

	// 계약대기(입주사가 등록) | 계약완료(소유자가 승인) | 계약취소(입실 전에 취소) | 계약이행 | 계약파기(계약 만료일 이전에 퇴실) | 계약만료(정상 종료)
	// 계약이행 상태일때만 공실이 아님
	// 계약이행, 계약만료 상태 변경은 배치 작업 필요
}
