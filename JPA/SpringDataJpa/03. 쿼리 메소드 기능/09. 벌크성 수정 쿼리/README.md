# 벌크성 수정 쿼리
## JPA를 사용한 벌크성 수정 쿼리
```java
public int bulkAgePlus(int age) {
    int resultCount = em.createQuery(
            "update Member m set m.age = m.age + 1" +
            "where m.age >= :age")
        .setParameter("age", age)
        .executeUpdate();
    return resultCount;
}
```
## JPA를 사용한 벌크성 수정 쿼리 테스트
```java
@Test
public void bulkUpdate() throws Exception {
    //given
    memberJpaRepository.save(new Member("member1", 10));
    memberJpaRepository.save(new Member("member2", 19));
    memberJpaRepository.save(new Member("member3", 20));
    memberJpaRepository.save(new Member("member4", 21));
    memberJpaRepository.save(new Member("member5", 40));
        
    //when
    int resultCount = memberJpaRepository.bulkAgePlus(20);
        
    //then
    assertThat(resultCount).isEqualTo(3);
}
```
## 스프링 데이터 JPA를 사용한 벌크성 수정 쿼리
```java
@Modifying
@Query("update Member m set m.age = m.age + 1 where m.age >= :age")
int bulkAgePlus(@Param("age") int age);
```

## 스프링 데이터 JPA를 사용한 벌크성 수정 쿼리 테스트
```java
@Test
public void bulkUpdate() throws Exception {
    //given
    memberRepository.save(new Member("member1", 10));
    memberRepository.save(new Member("member2", 19));
    memberRepository.save(new Member("member3", 20));
    memberRepository.save(new Member("member4", 21));
    memberRepository.save(new Member("member5", 40));
        
    //when
    int resultCount = memberRepository.bulkAgePlus(20);
        
    //then
    assertThat(resultCount).isEqualTo(3);
}
```
- 벌크성 수정, 삭제 쿼리는 `@Modifying` 어노테이션을 사용
    - 사용하지 않으면 다음 예외 발생
    - `org.hibernate.hqlinternal.QueryExecutionRequestException: Not supported for DML operations`
- 벌크성 쿼리를 실행하고 나서 영속성 컨텍스트 초기화 : `@Modifying(clearAutomatically = true)`
  (이 옵션의 기본값은 `false`)
    - 이 옵션 없이 회원을 `findById`로 다시 조회하면 영속성 컨텍스트에 과거 값이 남아서 문제가 될 수 있다.
    만약 다시 조회해야 하면 꼭 영속성 컨텍스트를 초기화하자.
> 참고: 벌크 연산은 영속성 컨텍스트를 무시하고 실행하기 때문에, 영속성 컨텍스트에 있는 엔티티의 상태와
> DB에 엔티티 상태가 달라질 수 있다.<br>
> 권장하는 방안<br>
> 1. 영속성 컨텍스트에 엔티티가 없는 상태에서 벌크 연산을 먼저 실행한다.
> 2. 부득이하게 영속성 컨텍스트에 엔티티가 있으면 벌크 연산 직후 영속성 컨텍스트를 초기화한다.
