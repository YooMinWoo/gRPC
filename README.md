# gRPC

Java 17, Spring Boot 4.1, Spring Data JPA, PostgreSQL 기반 프로젝트입니다.

## 필수 규약

- [아키텍처 규약](docs/architecture-conventions.md)
- [API, Swagger, Error, Response 규약](docs/api-conventions.md)
- [에이전트 작업 규칙](AGENTS.md)

## 검증

Gradle이 설치되어 있거나 Gradle Wrapper가 추가된 환경에서 다음 명령을 실행합니다.

```bash
gradle test
```

## 데이터베이스

기본 실행 환경은 PostgreSQL을 사용합니다.

```bash
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/grpc
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=postgres
```

테스트 환경은 H2 인메모리 DB를 사용하며 `MODE=PostgreSQL`로 동작합니다. 테스트에서
H2 전용 문법이나 PostgreSQL과 다른 동작에 의존하지 않아야 합니다.
