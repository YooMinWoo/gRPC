package com.example.grpc.common.response;

import com.example.grpc.common.error.CommonErrorCode;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ApiResponseTest {

    @Test
    void successCreatesStandardSuccessBody() {
        ApiResponse<String> response = ApiResponse.success("created");

        assertThat(response.success()).isTrue();
        assertThat(response.code()).isEqualTo("SUCCESS");
        assertThat(response.message()).isEqualTo("요청이 성공적으로 처리되었습니다.");
        assertThat(response.data()).isEqualTo("created");
        assertThat(response.timestamp()).isNotNull();
    }

    @Test
    void failureCreatesStandardFailureBodyFromErrorCode() {
        ApiResponse<Void> response = ApiResponse.failure(CommonErrorCode.INVALID_INPUT);

        assertThat(response.success()).isFalse();
        assertThat(response.code()).isEqualTo("COMMON_INVALID_INPUT");
        assertThat(response.message()).isEqualTo("요청 값이 올바르지 않습니다.");
        assertThat(response.data()).isNull();
        assertThat(response.timestamp()).isNotNull();
    }
}
