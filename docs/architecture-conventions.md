# 아키텍처 규약

이 프로젝트는 Java 17, Spring Boot 4.1, Spring Data JPA, PostgreSQL, DDD,
헥사고날 아키텍처를 사용한다. 모든 신규 코드는 이 문서와 `AGENTS.md`를 반드시 따른다.

## 핵심 원칙

- 도메인 모델은 프레임워크와 데이터베이스에 의존하지 않는다.
- 애플리케이션 계층은 유스케이스를 조합하고 트랜잭션 경계를 관리한다.
- 어댑터 계층은 외부 입출력 기술을 담당한다.
- 의존성 방향은 항상 바깥에서 안쪽으로만 향한다.
- 비즈니스 규칙은 도메인 계층에 둔다.
- Spring, JPA, Web, Swagger 어노테이션은 도메인 계층에서 금지한다.

## 권장 패키지 구조

```text
com.example.grpc
├── common
│   ├── error
│   ├── response
│   └── swagger
└── {domain}
    ├── domain
    │   ├── model
    │   ├── service
    │   └── exception
    ├── application
    │   ├── port
    │   │   ├── in
    │   │   └── out
    │   └── service
    └── adapter
        ├── in
        │   └── web
        └── out
            └── persistence
```

도메인별 패키지는 기능 단위로 만든다. 예를 들어 회원 도메인은
`com.example.grpc.member` 아래에 `domain`, `application`, `adapter`를 둔다.

## DDD 규약

- Aggregate Root는 도메인 불변식을 지키는 메서드를 제공한다.
- 외부 계층은 Aggregate 내부 상태를 임의로 변경하지 않는다.
- Value Object는 불변 객체로 만들고 값 검증을 생성 시점에 수행한다.
- 도메인 서비스는 특정 Aggregate 하나에 자연스럽게 속하지 않는 도메인 규칙만 담는다.
- 도메인 예외는 도메인 패키지의 `exception`에 두고 `DomainException`을 상속한다.
- 도메인별 에러 코드는 `{DomainName}ErrorCode` enum으로 만들고 `ErrorCode`를 구현한다.
- 에러 코드는 반드시 도메인 접두사를 포함한다. 예: `MEMBER_NOT_FOUND`,
  `ORDER_ALREADY_PAID`.

## 헥사고날 아키텍처 규약

- Inbound Adapter는 HTTP 요청을 DTO로 받고 Inbound Port를 호출한다.
- Inbound Port는 유스케이스 인터페이스이며 `application.port.in`에 둔다.
- Application Service는 Inbound Port를 구현하고 Outbound Port를 호출한다.
- Outbound Port는 저장소, 외부 API, 메시징 같은 외부 의존 추상화이며
  `application.port.out`에 둔다.
- Outbound Adapter는 Outbound Port를 구현하고 JPA, 외부 API 클라이언트 같은 기술을 사용한다.
- Adapter는 도메인 모델과 DTO/JPA Entity 사이의 변환을 책임진다.
- Controller가 JPA Repository를 직접 호출하는 것은 금지한다.
- Application Service가 Spring MVC DTO나 JPA Entity를 직접 반환하는 것은 금지한다.
- Domain 계층이 Application, Adapter, Common Web 기술에 의존하는 것은 금지한다.

## Spring Data JPA 규약

- JPA Entity는 `adapter.out.persistence` 아래에 둔다.
- JPA Repository는 `adapter.out.persistence` 아래에 둔다.
- JPA Entity 클래스 이름은 `{DomainName}JpaEntity` 형식을 사용한다.
- JPA Entity에는 비즈니스 로직을 절대 넣지 않는다.
- JPA Entity는 테이블 매핑, ORM 식별자, 연관관계, 영속성 생성자만 가진다.
- JPA Entity에는 Aggregate 불변식을 지키는 메서드, 상태 전이 메서드, 계산 규칙을 넣지 않는다.
- 비즈니스 로직은 도메인 모델 또는 도메인 서비스에 둔다.
- Persistence Adapter는 Domain Model과 JpaEntity 사이의 변환 메서드를 가진다.
- JPA Repository 인터페이스는 Application 계층 밖으로 노출하지 않는다.
- Lazy loading에 의존한 비즈니스 흐름을 만들지 않는다. 필요한 조회 모델은 별도 쿼리로 준비한다.

## 데이터베이스 규약

- 운영과 로컬 실행의 기본 데이터베이스는 PostgreSQL이다.
- 테스트 프로파일은 H2 인메모리 DB를 사용하되 `MODE=PostgreSQL`을 적용한다.
- H2에서만 동작하는 SQL, 컬럼 타입, 예약어 회피 방식에 의존하지 않는다.
- PostgreSQL 접속 정보는 환경변수로 주입한다.
- 애플리케이션 기본 설정은 다음 환경변수를 사용한다.
  - `SPRING_DATASOURCE_URL`
  - `SPRING_DATASOURCE_USERNAME`
  - `SPRING_DATASOURCE_PASSWORD`
- JPA 쿼리나 네이티브 SQL을 추가할 때 PostgreSQL 기준으로 검토한다.
- DB 스키마 변경 도구가 도입되기 전까지 임의의 DDL 자동 생성을 운영 전제로 삼지 않는다.

## 계층별 금지 의존성

- `domain` -> Spring Framework, JPA, Web, Swagger 의존 금지
- `application` -> Web DTO, JPA Entity, JPA Repository 의존 금지
- `adapter.in.web` -> JPA Repository 직접 의존 금지
- `adapter.out.persistence` -> Controller DTO 의존 금지
- `common` -> 특정 도메인 구현 의존 금지

## 코드 리뷰 체크리스트

- 비즈니스 규칙이 JPA Entity나 Controller에 들어가지 않았는가?
- Port 인터페이스가 유스케이스와 외부 의존을 명확히 분리하는가?
- 도메인 모델이 Spring/JPA 어노테이션 없이 동작하는가?
- 예외 코드가 도메인 이름 접두사를 가지는가?
- 모든 API 응답이 `ApiResponse`를 사용하는가?
- Swagger 설명이 한국어이며 충분히 친절한가?
