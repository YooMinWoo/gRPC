# API, Swagger, Error, Response 규약

모든 HTTP API는 한국어 Swagger 문서, 공통 응답 형식, 전역 예외 처리를 사용한다.

## Swagger 문서화 규약

- Swagger 문서는 반드시 한국어로 작성한다.
- Controller에는 `@Tag`를 붙이고 도메인과 기능을 한국어로 설명한다.
- API 메서드에는 `@Operation(summary = "...", description = "...")`를 붙인다.
- `summary`는 한 줄로 사용자가 이해할 수 있게 작성한다.
- `description`은 요청 목적, 주요 제약, 성공 시 결과를 친절하게 설명한다.
- Request DTO 필드에는 `@Schema(description = "...", example = "...")`를 붙인다.
- Response DTO 필드에도 `@Schema`를 붙인다.
- 에러 응답은 가능한 실패 상황을 한국어 설명으로 문서화한다.
- 내부 구현 용어보다 사용자와 API 소비자가 이해할 수 있는 표현을 우선한다.

## ApiResponse 규약

모든 API 응답은 `ApiResponse<T>`를 사용한다.

```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "요청이 성공적으로 처리되었습니다.",
  "data": {},
  "timestamp": "2026-06-18T06:00:00Z"
}
```

- 성공 응답의 `code`는 `SUCCESS`를 사용한다.
- 실패 응답의 `code`는 도메인 접두사가 있는 예외 코드를 사용한다.
- `message`는 반드시 한국어로 작성한다.
- 실패 응답의 `data`는 사용하지 않는다.
- Controller는 원시 DTO를 직접 반환하지 않고 `ApiResponse.success(...)`로 감싼다.
- 예외 응답은 `GlobalExceptionHandler`가 `ApiResponse.failure(...)`로 만든다.

## 전역 에러 처리 규약

- 모든 도메인 예외는 `DomainException`을 상속한다.
- 모든 에러 코드는 `ErrorCode`를 구현한다.
- 공통 에러는 `CommonErrorCode`를 사용한다.
- 도메인 에러는 `{DomainName}ErrorCode`를 사용한다.
- 에러 코드 이름은 `{DOMAIN}_{REASON}` 형식을 사용한다.
- HTTP 상태 코드는 `ErrorCode.status()`에 정의한다.
- Controller에서 try-catch로 API 에러 응답을 직접 만들지 않는다.
- 예상 가능한 실패는 도메인별 `DomainException`으로 표현한다.
- 예상하지 못한 예외는 `COMMON_INTERNAL_SERVER_ERROR`로 응답한다.

## 도메인 예외 예시

```java
public enum MemberErrorCode implements ErrorCode {
    MEMBER_NOT_FOUND("MEMBER_NOT_FOUND", "회원을 찾을 수 없습니다.", HttpStatus.NOT_FOUND);

    private final String code;
    private final String message;
    private final HttpStatus status;
}
```

```java
throw new DomainException(MemberErrorCode.MEMBER_NOT_FOUND);
```

## API 작성 체크리스트

- Controller 반환 타입이 `ApiResponse<T>`인가?
- Swagger 설명, 예시, 에러 설명이 모두 한국어인가?
- DTO 필드마다 `@Schema` 설명과 예시가 있는가?
- 에러 코드가 도메인 접두사를 가지는가?
- Controller가 비즈니스 로직이나 JPA Repository를 직접 호출하지 않는가?
- 실패 응답이 `GlobalExceptionHandler`를 통해 생성되는가?
