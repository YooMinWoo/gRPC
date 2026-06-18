package com.example.grpc.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.example.grpc.common.error.ErrorCode;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "모든 API에서 공통으로 사용하는 표준 응답 형식")
public record ApiResponse<T>(
        @Schema(description = "요청 처리 성공 여부", example = "true")
        boolean success,

        @Schema(description = "응답 코드. 성공은 SUCCESS, 실패는 도메인 접두사를 포함한 예외 코드", example = "SUCCESS")
        String code,

        @Schema(description = "사용자와 개발자가 이해할 수 있는 한국어 응답 메시지", example = "요청이 성공적으로 처리되었습니다.")
        String message,

        @Schema(description = "응답 데이터. 실패 응답에서는 null")
        T data,

        @Schema(description = "응답 생성 시각")
        Instant timestamp
) {

    private static final String SUCCESS_CODE = "SUCCESS";
    private static final String SUCCESS_MESSAGE = "요청이 성공적으로 처리되었습니다.";

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, SUCCESS_CODE, SUCCESS_MESSAGE, data, Instant.now());
    }

    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(true, SUCCESS_CODE, message, data, Instant.now());
    }

    public static ApiResponse<Void> failure(ErrorCode errorCode) {
        return new ApiResponse<>(false, errorCode.code(), errorCode.message(), null, Instant.now());
    }
}
