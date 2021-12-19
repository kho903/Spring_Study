# Projections
- https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#projections
- 엔티티 대신에 DTO를 편리하게 조회할 때 사용
- 전체 엔티티가 아니라 만약 회원 이름만 딱 조회하고 싶다면?
```java
public interface UsernameOnly {
    String getUsername();
}
```
- 조회할 엔티티의 필드를 getter 형식으로 지정하면 해당 필드만 선택해서 조회 (Projection)
```java
public interface MemberRepository ... {
    List<UsernameOnly> findProjectionsByUsername(String username);
}
```
- 메서드 이름은 자유, 반환타입으로 인지
```java
@Test
public void projections() throws Exception {
    //given
    Team teamA = new Team("teamA");
    em.persist(teamA);
    Member m1 = new Member("m1", 0, teamA);
    Member m2 = new Member("m2", 0, teamA);
    em.persist(m1);
    em.persist(m2);
    em.flush();
    em.clear();
    //when
    List<UsernameOnly> result =
            memberRepository.findProjectionsByUsername("m1");
    //then
    Assertions.assertThat(result.size()).isEqualTo(1);
}
```
```jpaql
select m.username from Member m
where m.username='m1';
```
- SQL에서도 select 절에서 username만 조회(Projection)하는 것을 확인

### 인터페이스 기반 Closed Projections
- 프로퍼티 형식 (getter)의 인터페이스를 제공하면, 구현체는 스프링 데이터 JPA가 제공
```java
public interface UsernameOnly {
    String getUsername();
}
```
- 단 이렇게 SpEL 문법을 사용하면 DB에서 엔티티 필드를 다 조회해온 다음에 계산한다. 따라서 JPQL
SELECT 절 최적화가 안된다.

## 클래스 기반 Projection
- 다음과 같이 인터페이스가 아닌 구체적인 DTO 형식도 가능
- 생성자의 파라미터 이름으로 매칭
```java
public class UsernameOnlyDto {
    private final String username;

    public UsernameOnlyDto(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }
}
```
## 동적 Projections
- 다음과 같이 Generic type을 주면, 동적으로 프로젝션 데이터 변경 가능
```java
<T> List<T> findProjectionsByUsername(String username, Class<T> type);
```
- 사용 코드
```java
List<UsernameOnly> result = memberRepository.findProjectionsByUsername("m1", UsernameOnly.class);
```
- 중첩 구조 처리
```java
public interface NestedClosedProjection {
    String getUsername();

    TeamInfo getTeam();

    interface TeamInfo {
        String getName();
    }
}
```
```jpaql
select
 m.username as col_0_0_,
 t.teamid as col_1_0_,
 t.teamid as teamid1_2_,
 t.name as name2_2_
from
 member m
left outer join
 team t
 on m.teamid=t.teamid
where
 m.username=?
```

### 주의
- 프로젝션 대상이 root 엔티티면, JPQL SELECT 절 최적화 가능
- 프로젝션 대상이 ROOT가 아니면 
    - LEFT OUTER JOIN 처리
    - 모든 필드를 SELECT해서 엔티티로 조회한 다음에 계산

### 정리
- 프로젝션 대상이 root 엔티티면 유용하다.
- 프로젝션 대상이 root 엔티티를 넘어가면 JPQL SELECT 최적화가 안된다.
- 실무의 복잡한 쿼리를 해결하기에는 한계가 있다.
- 실무에서는 단순할 때만 사용하고, 조금만 복잡해지면 QUeryDSL을 사용하자.
