# JPA Hint & Lock
## JPA Hint
- JPA 쿼리 힌트 (SQL 힌트가 아니라 JPA 구현체에게 제공하는 힌트)

## 쿼리 힌트 사용
```java
@QueryHints(value = @QueryHint(name = "org.hibernate.readOnly", value = "true"))
Member findReadOnlyByUsername(String username);
```

## 쿼리 힌트 사용 확인
```java
@Test
public void queryHint() throws Exception {
    //given
    memberRepository.save(new Member("member1", 10));
    em.flush();
    em.clear();

    //when
    Member member = memberRepository.findReadOnlyByUsername("member1");
    member.setUsername("member2");
    em.flush(); //Update Query 실행X
}
```

## 쿼리 힌트 Page 추가 예제
```java
@QueryHints(value = {@QueryHint(name = "org.hibernate.readOnly", value = "true")},
        forCounting = true)
Page<Member> findByUsername(String name,Pagable pageable);
```
- `org.springframwork.data.jpa.repository.QueryHints` 어노테이션을 사용
- `forCounting` : 반환 타입으로 `Page` 인터페이스를 적용하면 추가로 호출하는 페이징을 위한 
count 쿼리도 쿼리 힌트 적용 (기본값 `true`)

## Lock
```java
@Lock(LockModeType.PESSIMISTIC_WRITE)
List<Member> findByUsername(String name);
```
- `org.springframework.data.jpa.repository.Lock` 어노테이션을 사용
