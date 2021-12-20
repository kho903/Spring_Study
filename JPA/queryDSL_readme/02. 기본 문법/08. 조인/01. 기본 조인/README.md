# 기본 조인
- 조인의 기본 문법은 첫 번째 파라미터에 조인 대상을 지정하고, 두 번째 파라미터에 별칭(alias)으로
사용할 Q 타입을 지정하면 된다.
- `join(조인 대상, 별칭으로 사용할 Q타입)`

## 기본 조인
```java
/**
 * 팀 A에 소속된 모든 회원
 */
@Test
public void join() throws Exception {
    QMember member = QMember.member;
    QTeam team = QTeam.team;
    List<Member> result = queryFactory
            .selectFrom(member)
            .join(member.team, team)
            .where(team.name.eq("teamA"))
            .fetch();
    assertThat(result)
            .extracting("username")
            .containsExactly("member1", "member2");
}
```
- `join()`, `innerJoin()` : 내부 조인 (inner join)
- `leftJoin()` : left 외부 조인 (left outer join)
- `rightJoin()` : right 외부 조인 (right outer join)
- JPQL의 `on`과 성능 최적화를 위한 `fetch` 조인 제공

## 세타 조인
- 연관관계가 없는 필드로 조인
```java
/**
 * 세타 조인(연관관계가 없는 필드로 조인)
 * 회원의 이름이 팀 이름과 같은 회원 조회
 */
@Test
public void theta_join() throws Exception {
    em.persist(new Member("teamA"));
    em.persist(new Member("teamB"));
    List<Member> result = queryFactory
            .select(member)
            .from(member, team)
            .where(member.username.eq(team.name))
            .fetch();
    assertThat(result)
            .extracting("username")
            .containsExactly("teamA", "teamB");
}
```
- from 절에 여러 엔티티를 선택해서 세타 조인
- 외부 조인 불가능 -> 다음에 설명할 조인 on을 사용하면 외부 조인 가능
