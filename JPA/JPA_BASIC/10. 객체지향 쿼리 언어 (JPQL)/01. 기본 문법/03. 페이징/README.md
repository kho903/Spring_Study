# 페이징 API
- JPA는 페이징을 다음 두 API로 추상화
- setFirstResult(int startPosition) : 조회 시작 위치 (0부터 시작)
- setMaxResults(int maxResult) : 조회할 데이터 수

## 페이징 API 예시
```java
// 페이징 쿼리
String jpql = "select m from Member m order by m.name desc";
List<Member> resultList = em.createQuery(jpql, Member.class)
    .setFirstResult(10)
    .setMaxResults(20)
    .getResultList();
```

## 페이징 API - MySQL 방안
```sql
SELECT
    M.ID AS ID,
    M.AGE AS AGE,
    M.TEAM_ID AS TEAM_ID,
    M.NAME AS NAME ,
FROM
    MEMBER M 
ORDER BY
    M.NAME DESC LIMIT ?, ?
```

## 페이징 API - Oracle 방언
```sql
SELECT *
FROM (SELECT ROW_.*, ROWNUM ROWNUM_
      FROM (SELECT M.ID      AS ID,
                   M.AGE     AS AGE,
                   M.TEAM_ID AS TEAM_ID,
                   M.NAME    AS NAME
            FROM MEMBER M
            ORDER BY M.NAME
           ) ROW_
      WHERE ROWNUM <= ?
     )
WHERE ROWNUM_ > ?
```
