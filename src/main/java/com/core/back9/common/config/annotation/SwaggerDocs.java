package com.core.back9.common.config.annotation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface SwaggerDocs {

    @Target({ElementType.METHOD, ElementType.TYPE, ElementType.ANNOTATION_TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(
            summary = "대표 호실 및 비교 호실 주요 통계 정보 조회", description = "대표 호실 및 비교 호실 주요 통계 정보를 조회한다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400",
                    description = "잘못된 요청",
                    content = {@Content(mediaType = "application/json",
                            examples = {
                                    @ExampleObject(name = "소유자 접근", value = "소유자만 접근할 수 있습니다."),
                                    @ExampleObject(name = "중복 계약 존재", value = "이미 계약된 호실이 존재합니다.")
                            })}),
            @ApiResponse(responseCode = "404", content = {@Content(schema = @Schema(example = "유효한 호실을 찾을 수 없습니다."))}),
            @ApiResponse(responseCode = "401", content = {@Content(schema = @Schema(example = "권한이 없습니다."))})
    })
    @interface GetContractStatisticInfo {
    }

    @Target({ElementType.METHOD, ElementType.TYPE, ElementType.ANNOTATION_TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(
            summary = "신계약 정보 등록",
            description = "특정 호실에 계약을 등록한다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "성공"),
            @ApiResponse(responseCode = "400", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "이미 계약된 호실이 존재합니다.")))
    })
    @interface RegisterContract {
    }

    @Target({ElementType.METHOD, ElementType.TYPE, ElementType.ANNOTATION_TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(
            summary = "재계약 정보 등록", description = "특정 호실에 재계약을 등록한다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "성공"),
            @ApiResponse(responseCode = "400",
                    description = "잘못된 요청",
                    content = {@Content(mediaType = "application/json",
                            examples = {
                                    @ExampleObject(name = "소유자 접근", value = "소유자만 접근할 수 있습니다."),
                                    @ExampleObject(name = "유효하지 않은 입주사 접근", value = "유효한 입주사를 찾을 수 없습니다."),
                                    @ExampleObject(name = "유효하지 않은 계약 접근", value = "유효한 계약을 찾을 수 없습니다."),
                                    @ExampleObject(name = "재계약시 만기일 오입력", value = "시작일이 끝일보다 이른 일자일 수 없습니다.")
                            })}),
            @ApiResponse(responseCode = "404", content = {@Content(schema = @Schema(example = "유효한 호실을 찾을 수 없습니다."))}),
            @ApiResponse(responseCode = "401", content = {@Content(schema = @Schema(example = "권한이 없습니다."))})
    })
    @interface RenewContract {
    }

    @Target({ElementType.METHOD, ElementType.TYPE, ElementType.ANNOTATION_TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(
            summary = "특정 호실의 모든 계약 조회", description = "특정 호실의 모든 계약을 조회한다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400", content = {@Content(schema = @Schema(example = "소유자만 접근할 수 있습니다."))}),
            @ApiResponse(responseCode = "404", content = {@Content(schema = @Schema(example = "유효한 호실을 찾을 수 없습니다."))}),
            @ApiResponse(responseCode = "401", content = {@Content(schema = @Schema(example = "권한이 없습니다."))})
    })
    @interface GetAllContract {
    }

    @Target({ElementType.METHOD, ElementType.TYPE, ElementType.ANNOTATION_TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(
            summary = "특정 계약 조회", description = "선택한 특정 계약을 조회한다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400",
                    description = "잘못된 요청",
                    content = {@Content(mediaType = "application/json",
                            examples = {
                                    @ExampleObject(name = "소유자 접근", value = "소유자만 접근할 수 있습니다."),
                                    @ExampleObject(name = "유효하지 않은 계약 접근", value = "유효한 계약을 찾을 수 없습니다.")
                            })}),
            @ApiResponse(responseCode = "404", content = {@Content(schema = @Schema(example = "유효한 호실을 찾을 수 없습니다."))}),
            @ApiResponse(responseCode = "401", content = {@Content(schema = @Schema(example = "권한이 없습니다."))})
    })
    @interface GetOneContract {
    }

    @Target({ElementType.METHOD, ElementType.TYPE, ElementType.ANNOTATION_TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(
            summary = "특정 계약 수정", description = "선택한 특정 계약 내용을 수정한다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400",
                    description = "잘못된 요청",
                    content = {@Content(mediaType = "application/json",
                            examples = {
                                    @ExampleObject(name = "소유자 접근", value = "소유자만 접근할 수 있습니다."),
                                    @ExampleObject(name = "유효하지 않은 계약 접근", value = "유효한 계약을 찾을 수 없습니다.")
                            })}),
            @ApiResponse(responseCode = "404", content = {@Content(schema = @Schema(example = "유효한 호실을 찾을 수 없습니다."))}),
            @ApiResponse(responseCode = "401", content = {@Content(schema = @Schema(example = "권한이 없습니다."))})
    })
    @interface ModifyContract {
    }

    @Target({ElementType.METHOD, ElementType.TYPE, ElementType.ANNOTATION_TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(
            summary = "계약 완료 처리", description = "대기(PENDING) 상태인 계약을 완료(COMPLETED) 상태로 변경한다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400",
                    description = "잘못된 요청",
                    content = {@Content(mediaType = "application/json",
                            examples = {
                                    @ExampleObject(name = "소유자 접근", value = "소유자만 접근할 수 있습니다."),
                                    @ExampleObject(name = "유효하지 않은 계약 접근", value = "유효한 계약을 찾을 수 없습니다."),
                                    @ExampleObject(name = "완료 가능 일자 오입력", value = "계약 완료 처리가 가능한 일자가 이미 경과했습니다."),
                                    @ExampleObject(name = "잘못된 계약 접근", value = "계약 대기 상태가 아닙니다.")
                            })}),
            @ApiResponse(responseCode = "404", content = {@Content(schema = @Schema(example = "유효한 호실을 찾을 수 없습니다."))}),
            @ApiResponse(responseCode = "401", content = {@Content(schema = @Schema(example = "권한이 없습니다."))})
    })
    @interface CompleteContract {
    }

    @Target({ElementType.METHOD, ElementType.TYPE, ElementType.ANNOTATION_TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(
            summary = "계약 취소 처리", description = "대기(PENDING) 상태 & 완료(COMPLETED) 상태인 계약을 취소(CANCELED) 상태로 변경한다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400",
                    description = "잘못된 요청",
                    content = {@Content(mediaType = "application/json",
                            examples = {
                                    @ExampleObject(name = "소유자 접근", value = "소유자만 접근할 수 있습니다."),
                                    @ExampleObject(name = "유효하지 않은 계약 접근", value = "유효한 계약을 찾을 수 없습니다."),
                                    @ExampleObject(name = "취소 가능 일자 오입력", value = "취소 가능한 일자가 경과했습니다."),
                                    @ExampleObject(name = "잘못된 계약 접근", value = "계약을 취소할 수 있는 상태가 아닙니다.")
                            })}),
            @ApiResponse(responseCode = "404", content = {@Content(schema = @Schema(example = "유효한 호실을 찾을 수 없습니다."))}),
            @ApiResponse(responseCode = "401", content = {@Content(schema = @Schema(example = "권한이 없습니다."))})
    })
    @interface CancelContract {
    }

    @Target({ElementType.METHOD, ElementType.TYPE, ElementType.ANNOTATION_TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(
            summary = "계약 이행 처리", description = "완료(COMPLETED) 상태인 계약을 이행(IN_PROGRESS) 상태로 변경한다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400",
                    description = "잘못된 요청",
                    content = {@Content(mediaType = "application/json",
                            examples = {
                                    @ExampleObject(name = "소유자 접근", value = "소유자만 접근할 수 있습니다."),
                                    @ExampleObject(name = "유효하지 않은 계약 접근", value = "유효한 계약을 찾을 수 없습니다."),
                                    @ExampleObject(name = "이행 가능 일자 오입력", value = "계약 이행 가능 일자가 아닙니다."),
                                    @ExampleObject(name = "잘못된 계약 접근", value = "계약 완료 상태가 아닙니다.")
                            })}),
            @ApiResponse(responseCode = "404", content = {@Content(schema = @Schema(example = "유효한 호실을 찾을 수 없습니다."))}),
            @ApiResponse(responseCode = "401", content = {@Content(schema = @Schema(example = "권한이 없습니다."))})
    })
    @interface ProgressContract {}

    @Target({ElementType.METHOD, ElementType.TYPE, ElementType.ANNOTATION_TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(
            summary = "계약 만료 처리", description = "이행(IN_PROGRESS) 상태인 계약을 만료(EXPIRE) 상태로 변경한다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400",
                    description = "잘못된 요청",
                    content = {@Content(mediaType = "application/json",
                            examples = {
                                    @ExampleObject(name = "소유자 접근", value = "소유자만 접근할 수 있습니다."),
                                    @ExampleObject(name = "유효하지 않은 계약 접근", value = "유효한 계약을 찾을 수 없습니다."),
                                    @ExampleObject(name = "만료일자 오입력", value = "만료 상태로 변경을 원하는 일자가 정해진 만료 일자보다 이전 일자인 경우 계약 만료 상태로 변경할 수 없습니다."),
                                    @ExampleObject(name = "잘못된 계약 접근", value = "계약 이행 상태가 아닙니다.")
                            })}),
            @ApiResponse(responseCode = "404", content = {@Content(schema = @Schema(example = "유효한 호실을 찾을 수 없습니다."))}),
            @ApiResponse(responseCode = "401", content = {@Content(schema = @Schema(example = "권한이 없습니다."))})
    })
    @interface ExpireContract {}

    @Target({ElementType.METHOD, ElementType.TYPE, ElementType.ANNOTATION_TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(
            summary = "계약 파기 처리", description = "이행(IN_PROGRESS) 상태인 계약을 파기(TERMINATED) 상태로 변경한다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400",
                    description = "잘못된 요청",
                    content = {@Content(mediaType = "application/json",
                            examples = {
                                    @ExampleObject(name = "소유자 접근", value = "소유자만 접근할 수 있습니다."),
                                    @ExampleObject(name = "유효하지 않은 계약 접근", value = "유효한 계약을 찾을 수 없습니다."),
                                    @ExampleObject(name = "퇴실일자 오입력", value = "원하는 퇴실 일자가 기존 퇴실 일자와 같거나 계약 종료 일자 보다 이후의 일자인 경우 계약 파기 상태로 변경할 수 없습니다."),
                                    @ExampleObject(name = "잘못된 계약 접근", value = "계약이 이행 중인 상태가 아닙니다.")
                            })}),
            @ApiResponse(responseCode = "404", content = {@Content(schema = @Schema(example = "유효한 호실을 찾을 수 없습니다."))}),
            @ApiResponse(responseCode = "401", content = {@Content(schema = @Schema(example = "권한이 없습니다."))})
    })
    @interface TerminateContract {}

    @Target({ElementType.METHOD, ElementType.TYPE, ElementType.ANNOTATION_TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(
            summary = "계약 내역 삭제 처리", description = "계약 내역을 삭제할 수 있다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400",
                    description = "잘못된 요청",
                    content = {@Content(mediaType = "application/json",
                            examples = {
                                    @ExampleObject(name = "소유자 접근", value = "소유자만 접근할 수 있습니다."),
                                    @ExampleObject(name = "삭제 미처리", value = "삭제가 완료되지 않았습니다.")
                            })}),
            @ApiResponse(responseCode = "404", content = {@Content(schema = @Schema(example = "유효한 호실을 찾을 수 없습니다."))}),
            @ApiResponse(responseCode = "401", content = {@Content(schema = @Schema(example = "권한이 없습니다."))})
    })
    @interface DeleteContract {}

    @Target({ElementType.METHOD, ElementType.TYPE, ElementType.ANNOTATION_TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(
            summary = "입주사 정보 등록", description = "입주사 정보를 등록한다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "성공"),
            @ApiResponse(responseCode = "404", content = {@Content(schema = @Schema(example = "관리자만 접근할 수 있습니다."))}),
            @ApiResponse(responseCode = "401", content = {@Content(schema = @Schema(example = "권한이 없습니다."))})
    })
    @interface RegisterTenant{}

    @Target({ElementType.METHOD, ElementType.TYPE, ElementType.ANNOTATION_TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(
            summary = "입주사 정보 수정", description = "선택한 입주사의 정보를 수정한다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400", content = {@Content(schema = @Schema(example = "유효한 입주사를 찾을 수 없습니다."))}),
            @ApiResponse(responseCode = "404", content = {@Content(schema = @Schema(example = "관리자만 접근할 수 있습니다."))}),
            @ApiResponse(responseCode = "401", content = {@Content(schema = @Schema(example = "권한이 없습니다."))})
    })
    @interface ModifyTenant{}

    @Target({ElementType.METHOD, ElementType.TYPE, ElementType.ANNOTATION_TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(
            summary = "입주사 정보 삭제", description = "선택한 입주사의 정보를 삭제한다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400", content = {@Content(schema = @Schema(example = "삭제가 완료되지 않았습니다."))}),
            @ApiResponse(responseCode = "404", content = {@Content(schema = @Schema(example = "관리자만 접근할 수 있습니다."))}),
            @ApiResponse(responseCode = "401", content = {@Content(schema = @Schema(example = "권한이 없습니다."))})
    })
    @interface DeleteTenant{}

}
