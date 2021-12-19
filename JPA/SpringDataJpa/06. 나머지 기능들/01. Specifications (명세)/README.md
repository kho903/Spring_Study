# Specifications (명세)

- 책 도메인 주도 설계 (Domain Driven Design)는 Specification(명세)라는 개념을 소개, 스프링 데이터 JPA는 JPA Criteria를 활용해서 이 개념을 사용할 수 있도록 지원

## 술어 (predicate)

- 참 또는 거짓으로 평가
- AND OR 같은 연산자로 조합해서 다양한 검색 조건을 쉽게 생성 (컴포지트 패턴)
- 예) 검색 조건 하나하나
- 스프링 데이터 JPA는 `org.springframework.data.jpa.domain.Specification`클래스로 정의

## 명세기능 사용방법

- `JpaSpecificationExecutor` 인터페이스 상속
```java
public interface MemberRepository extends JpaRepository<Member, Long>,
        JpaSpecificationExecutor<Member> {

}
```
- `JpaSpecificationExecutor` 인터페이스
```java
public interface JpaSpecificationExecutor<T> {
    Optional<T> findOne(@Nullable Specification<T> spec);

    List<T> findAll(Specification<T> spec);

    Page<T> findAll(Specification<T> spec, Pageable pageable);

    List<T> findAll(Specification<T> spec, Sort sort);

    long count(Specification<T> spec);
}
```
- `Specification`을 파라미터로 받아서 검색 조건으로 사용

### 명세 사용 코드
```java
@Test
public void specBasic() throws Exception {
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
    Specification<Member> spec =
            MemberSpec.username("m1").and(MemberSpec.teamName("teamA"));
    List<Member> result = memberRepository.findAll(spec);
        
    //then
    Assertions.assertThat(result.size()).isEqualTo(1);
}

```
- `Specification`을 구현하면 명세들을 조립할 수 있음. `where()`, `and()`, `or()`, `not()` 제공
- `findAll`을 보면 회원 이름 명세 (`username`)와 팀 이름 명세 (`teamName`)을
`and`로 조합해서 검색 조건으로 사용
  
- `MemberSpec` 명세 정의 코드
```java
public class MemberSpec {
    public static Specification<Member> teamName(final String teamName) {
        return (Specification<Member>) (root, query, builder) -> {
            if (StringUtils.isEmpty(teamName)) {
                return null;
            }
            Join<Member, Team> t = root.join("team", JoinType.INNER); //회원과
            조인
            return builder.equal(t.get("name"), teamName);
        };
    }

    public static Specification<Member> username(final String username) {
        return (Specification<Member>) (root, query, builder) ->
                builder.equal(root.get("username"), username);
    }
}
```
- 명세를 정의하려면 `Specification` 인터페이스를 구현
- 명세를 정의할 때는 `toPredicate(...)`메서드만 구현하면 되는데 JPA Criteria의 
`Root`, `CriteriaQuery`, `CriteriaBuilder` 클래스를 파라미터로 제공
> 참고: 실무에서는 JPA Criteria를 거의 안쓰고 QueryDSL 사용
> 