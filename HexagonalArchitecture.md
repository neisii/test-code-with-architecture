# Hexagonal Architecture

1. 테스트하기  쉬운 코드가 좋은 코드일 확률이 높다.
 - 실수를 많이 줄일 수 있다.
2. 아키텍처: 문제 해결을 위해 준수해야 하는 제약을 넣는 과정
3. 아키텍트: 
 - 인적 자원 절감 -> 동시 작업이 가능 -> 관심사를 분리 -> 경계를 나눔
 - 의존성 역전 + @
4. 정책을 만들고 세부사항을 미루는 시스템 개발
   3) 세부사항: 데이터베이스, 웹서버, REST, 의존성 주입 프레임워크
   4) 도메인이 우선 개발되어야 한다. 
5. 의존성 역전: 포트-어댑터 패턴
- 청소부는 청소가 목적
- 청소기가 진공청소기인지 핸디청소기 인지 알 필요 없음


레이어드 아키텍처의 문제점
데이터베이스 위주의 사고를 하게 된다. ->상향식
Controller <- Service <- Repository
프레임워크를 먼저 생각하게 된다. -> 하향식
Controller -> Service -> Repository

헥사고날은 상향식
Controller RepositoryImpl
↑
Service Repository
↑
ServiceImpl ( 비즈니스 파사드 )
↑
Domain



모델은 어디까지 세분화해야 하는가?
Domain Entity와 Jpa Entity 를 분리해야 하냐?

DTO Domain @Entity
DTORequest DTOResponse
DomainDTO CacheDTO
이게 정말 필요한가?


https://www.youtube.com/watch?v=QUMERCN3rZs

애플리케이션을 UI나 데이터베이스 없이 동작하도록 만들어라
 - 자동회된 회귀 테스르를 할 수 있다.
 - 데이터베이스를 사용할 수 없을 때도 동작한다.
 - 사용자의 개입 없이도 애플리케이션을 함께 연결할 수 있다.






테스트 범위
 - 커버리지가 중요하는 건 아니다.
- Right-BICEP 원칙
   - 결과가 올바른가?
   - 경계 조건은 맞는가?
    - Edge Case
      - Conformance
      - Ordering
      - Range
      - Reference
      - Existence
      - Cardinality
      - Time
    - Coner Case
  - Inversion Relationship 역관계 검증
  - Cross check 교차 검증
  - Error conditions 오류 상황을 강제로 발생시키고 시스템이 어떻게 동작하는지 테스트
  - Performance Characteristics 성능 조건 부합 여부(e2e)

테스트 팁
- ParameterizedTest 가시성 확보 (+MethodSource)
- AssertAll 앞의 검증이 실패해도 다음 assert문을 모두 실행할 수 있도록 함
- 하나의 테스트 when, then은 한번만  -> 가능하면 한눈에 표현하도록
- Awaitilty -> Thread.sleep, Thread.join 의 대안
- FIRST 원칙
  - Fast: 빨라야 함
  - Indenpendent: 독립적이어야 함
  - Repeatable: 반복 수행해도 결과가 같아야 함.
  - Self-validating: 시스템의 성공/실패 여부를 알 수 있어야 하 ㅁ.
  - Timely: 코드 구현 전에 테스트 작성

구글 엔지니어는 이렇게 일한다.

좋은 코드를 가늠하는 확실한 방법은 "얼마나 수정하기 쉬운가"다.
좋은 프로그래머는 사람이 이해할 수 있는 코드를 작성한다.


