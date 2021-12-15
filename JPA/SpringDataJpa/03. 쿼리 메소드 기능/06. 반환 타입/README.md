# 반환 타입
- 스프링 데이터 JPA는 유연한 반환 타입 지원
```java
List<Member> findByUsername(String name); //컬렉션
Member findByUsername(String name); //단건
Optional<Member> findByUsername(String name); //단건 Optional
```
- 스프링 데이터 JPA 공식 문서
    - https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repository-query-return-types
    
## 조회 결과가 많거나 없으면?
- 컬렉션
    - 결과 없음 : 빈 컬렉션 반환
- 단건 조회
    - 결과 없음 : `null` 반환
    - 결과가 2건 이상 : `javax.persistence.NonUniqueResultException` 예외 발생
> 참고 : 단건으로 지정한 메서드를 호출하면 스프링 데이터 JPA는 내부에서 JPQL의
> `Query.getSingleResult()`메서드를 호출한다. 이 메서드를 호출했을 떄 조회 결과가 없으면
> `javax.persistence.NoResultException`예외가 발생했는데 개발자 입장에서
> 다루기가 상당히 불편하다. 스프링 데이터 JPA는 단건을 조회할 때 이 예외가 발생하면
> 예외를 무시하고 대신에 `null`을 반환한다.
