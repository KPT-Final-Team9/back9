package com.core.back9.entity.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ContractStatus {
    // 계약대기, 계약완료, 계약취소, 계약이행, 계약중단, 계약만료

    // 보통 입주일 이전에 계약을 진행해 첫 계약 데이터 등록 시점에는 계약 대기 상태를 가지는 것으로 판단
    // 계약 대기, 완료, 취소는 소유자(or어드민..?)이 직접 변경해야한다.
    // 파기의 경우 아직 잘 모르겠음
    // 이행, 만료의 경우 배치 처리로 전환 가능
    PENDING("계약 대기"),
    COMPLETED("계약 완료"),
    CANCELED("계약 취소"), // 계약 등록(대기) 상태 이후 실제 계약 시작 일자까지 완료처리 하지 않으면 취소처리됨 or 이행 전 취소를 원할 시 취소처리
    IN_PROGRESS("계약 이행"),
    TERMINATED("계약 파기"),
    EXPIRED("계약 만료");

    private final String label;

}
