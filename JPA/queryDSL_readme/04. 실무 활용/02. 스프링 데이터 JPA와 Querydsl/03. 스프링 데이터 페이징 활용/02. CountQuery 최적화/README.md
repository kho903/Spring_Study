# 스프링 데이터 페이징 활용2 - CountQuery 최적화
## PageableExecutionUtils.getPage()로 최적화
```java
JPAQuery<Member> countQuery = queryFactory
    .select(member)
    .from(member)
    .leftJoin(member.team, team)
    .where(usernameEq(condition.getUsername()),
            teamNameEq(condition.getTeamName()),
            ageGoe(condition.getAgeGoe()),
            ageLoe(condition.getAgeLoe()));
// return new PageImpl<>(content, pageable, total);
return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchCount);
```
- 스프링 데이터 라이브러리가 제공
- count 쿼리가 생략 가능한 경우 생략해서 처리
    - 페이지 시작이면서 컨텐츠 사이즈가 페이지 사이즈보다 작을 때
    - 마지막 페이지 일 때 (offset + 컨텐츠 사이즈를 더해서 전체 사이즈 구함)
    