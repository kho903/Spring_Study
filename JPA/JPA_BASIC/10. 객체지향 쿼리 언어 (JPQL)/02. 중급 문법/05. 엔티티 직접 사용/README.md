# JPQL - 엔티티 직접 사용
## 엔티티 직접 사용 - 기본 키 값
- JPQL에서 엔티티를 직접 사용하면 SQL에서 해당 엔티티의 기본 키 값을 사용
- [JPQL] <br>
  select count(m.id) from Member m // 엔티티의 아이디를 사용 <br>
  select count(m) from Member m    // 엔티티를 직접 사용
- [SQL] (JPQL 둘 다 같은 다음 SQL을 실행) <br>
  select count(m.id) as cnt from Member m
- 엔티티를 파라미터로 전달
```java
String jpql = "select m from Member m where m = :member";
List resultList = em.createQuery(jpql)
                    .setParameter("member", member)
                    .getResultList();
```
- 식별자를 직접 전달
```java
String jpql = "select m from Member m where m.id = :memberId";
List resultList = em.createQuery(jpql)
                    .setParameter("memberId", memberId)
                    .getResultList();
```
- 실행된 SQL
`select m.* from Member m where m.id=?`
  
## 엔티티 직접 사용 - 외래 키 값
```java
Team team = em.find(Team.class, 1L);
String qlString = "select m from Member m where m.team = :team";
List resultList = em.createQuery(qlString)
                    .setParameter("teamId", teamId)
                    .getResultList();
```
- 실행된 SQL
`select m.* from Member m where m.team_id=?`
