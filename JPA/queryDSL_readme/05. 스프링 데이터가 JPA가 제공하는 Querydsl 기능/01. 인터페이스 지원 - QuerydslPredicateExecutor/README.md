# 인터페이스 지원 - QuerydslPredicateExecutor
- 공식 url
    - https://docs.spring.io/spring-data/jpa/docs/2.2.3.RELEASE/reference/html/#core.extensions.querydsl
    
## QuerydslPredicateExecutor 인터페이스
```java
public interface QuerydslPredicateExecutor<T> {
    Optional<T> findById(Predicate predicate);

    Iterable<T> findAll(Predicate predicate);

    long count(Predicate predicate);

    boolean exists(Predicate predicate);
    // … more functionality omitted.
}
```
- 리포지토리에 적용
```java
interface MemberRepository extends JpaRepository<User, Long>, QuerydslPredicateExecutor<User> {
}
```
```java
Iterable result = memberRepository.findAll(
          member.age.between(10, 40)
          .and(member.username.eq("member1"))
);
```
## 한계점
- 조인 X (묵시적 조인은 가능하지만 left join이 불가능하다.)
- 클라이언트가 Querydsl에 의존해야 한다. 서비스 클래스가 Querydsl이라는 구현 기술에 의존해야 한다.
- 복잡한 실무환경에서 사용하기에는 한계가 명확하다.
> 참고 : `QueryPredicateExecutor`는 Pageable, Sort를 모두 지원하고 정상 동작한다.
