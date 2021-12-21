# 스프링 데이터 페이징 활용 1 - Querydsl 페이징 연동
- 스프링 데이터의 Page, Pageable을 활용해보자.
- 전체 카운트를 한 번에 조회하는 단순한 방법
- 데이터 내용과 전체 카운트를 별도로 조회하는 방법

## 사용자 정의 인터페이스에 페이징 2가지 추가
```java
package study.querydsl.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import study.querydsl.dto.MemberSearchCondition;
import study.querydsl.dto.MemberTeamDto;

import java.util.List;

public interface MemberRepositoryCustom {
    List<MemberTeamDto> search(MemberSearchCondition condition);

    Page<MemberTeamDto> searchPageSimple(MemberSearchCondition condition,
                                         Pageable pageable);

    Page<MemberTeamDto> searchPageComplex(MemberSearchCondition condition,
                                          Pageable pageable);
}
```

## 전체 카운트를 한 번에 조회하는 단순한 방법
- searchPageSimple(), fetchResults() 사용
```java
/**
 * 단순한 페이징, fetchResults() 사용
 */
@Override
public Page<MemberTeamDto> searchPageSimple(MemberSearchCondition condition,
                                            Pageable pageable) {
    QueryResults<MemberTeamDto> results = queryFactory
            .select(new QMemberTeamDto(
                    member.id,
                    member.username,
                    member.age,
                    team.id,
                    team.name))
            .from(member)
            .leftJoin(member.team, team)
            .where(usernameEq(condition.getUsername()),
                    teamNameEq(condition.getTeamName()),
                    ageGoe(condition.getAgeGoe()),
                    ageLoe(condition.getAgeLoe()))
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetchResults();
    List<MemberTeamDto> content = results.getResults();
    long total = results.getTotal();
    return new PageImpl<>(content, pageable, total);
}
```
- Querydsl이 제공하는 `fetchResults()`를 사용하면 내용과 전체 카운트를 한 번에 조회할 수 잇다.
  (실제 쿼리는 2번 호출)
- `fetchResult()`는 카운트 쿼리 실행 시 필요 없는 `order by`는 제거한다.

## 데이터 내용과 전체 카운트를 별도로 조회하는 방법
- searchPageComplex()
```java
/**
 * 복잡한 페이징
 * 데이터 조회 쿼리와, 전체 카운트 쿼리를 분리
 */
@Override
public Page<MemberTeamDto> searchPageComplex(MemberSearchCondition condition,
                                             Pageable pageable) {
    List<MemberTeamDto> content = queryFactory
            .select(new QMemberTeamDto(
                    member.id,
                    member.username,
                    member.age,
                    team.id,
                    team.name))
            .from(member)
            .leftJoin(member.team, team)
            .where(usernameEq(condition.getUsername()),
                    teamNameEq(condition.getTeamName()),
                    ageGoe(condition.getAgeGoe()),
                    ageLoe(condition.getAgeLoe()))
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();
    long total = queryFactory
            .select(member)
            .from(member)
            .leftJoin(member.team, team)
            .where(usernameEq(condition.getUsername()),
                    teamNameEq(condition.getTeamName()),
                    ageGoe(condition.getAgeGoe()),
                    ageLoe(condition.getAgeLoe()))
            .fetchCount();
    return new PageImpl<>(content, pageable, total);
}
```
- 전체 카운트를 조회 하는 방법을 최적화 할 수 있으면 이렇게 분리하면 된다.
  (예를 들어서 전체 카운트를 조회할 때 조인 쿼리를 줄일 수 있다면 상당한 효과가 있다.)
- 코드를 리펙토링해서 내용 쿼리와 전체 카운트 쿼리를 읽기 좋게 분리하면 좋다.
