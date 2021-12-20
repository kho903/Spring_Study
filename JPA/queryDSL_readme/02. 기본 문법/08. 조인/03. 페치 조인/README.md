# 페치 조인
- 페치 조인은 SQL에서 제공하는 기능은 아니다.
- SQL 조인 활용해서 연관된 엔티티를 SQL 한 번에 조회하는 기능이다.
- 주로 성능 최적화에 사용하는 방법이다.

## 페치 조인 미적용
- 지연 로딩으로 Member, Team SQL 쿼리 각각 실행
```java
@PersistenceUnit
EntityManagerFactory emf;

@Test
public void fetchJoinNo() throws Exception {
    em.flush();
    em.clear();
    Member findMember = queryFactory
            .selectFrom(member)
            .where(member.username.eq("member1"))
            .fetchOne();
    boolean loaded =
            emf.getPersistenceUnitUtil().isLoaded(findMember.getTeam());
    assertThat(loaded).as("페치 조인 미적용").isFalse();
}
```

## 페치 조인 적용
- 즉시 로딩으로 Member, Team SQL 쿼리 조인으로 한 번에 조회
```java
@Test
public void fetchJoinUse() throws Exception {
    em.flush();
    em.clear();
    Member findMember = queryFactory
            .selectFrom(member)
            .join(member.team, team).fetchJoin()
            .where(member.username.eq("member1"))
            .fetchOne();
    boolean loaded =
            emf.getPersistenceUnitUtil().isLoaded(findMember.getTeam());
    assertThat(loaded).as("페치 조인 적용").isTrue();
}
```
사용 방법
- `left(), leftJoin()` 등 조인 기능 뒤에 `fetchJoin()` 이라고 추가하면 된다.
