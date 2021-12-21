# 스프링 데이터 페이징 활용3 - 컨트롤러 개발
## 실제 컨트롤러
```java
package study.querydsl.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import study.querydsl.dto.MemberSearchCondition;
import study.querydsl.dto.MemberTeamDto;
import study.querydsl.repository.MemberJpaRepository;
import study.querydsl.repository.MemberRepository;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class MemberController {
    private final MemberJpaRepository memberJpaRepository;
    private final MemberRepository memberRepository;

    @GetMapping("/v1/members")
    public List<MemberTeamDto> searchMemberV1(MemberSearchCondition condition) {
        return memberJpaRepository.search(condition);
    }

    @GetMapping("/v2/members")
    public Page<MemberTeamDto> searchMemberV2(MemberSearchCondition condition,
                                              Pageable pageable) {
        return memberRepository.searchPageSimple(condition, pageable);
    }

    @GetMapping("/v3/members")
    public Page<MemberTeamDto> searchMemberV3(MemberSearchCondition condition,
                                              Pageable pageable) {
        return memberRepository.searchPageComplex(condition, pageable);
    }
}
```
- http://localhost:8080/v2/members?size=5&page=2

## 스프링 데이터 정렬 (Sort)
- 스프링 데이터 JPA는 자신의 정렬(Sort)을 Querydsl의 정렬(OrderSpecifier)로 편리하게 변경하는
기능을 제공한다.
- 스프링 데이터 정렬을 Querydsl의 정렬로 직접 전환하는 방법은 다음과 같다.
- 스프링 데이터 Sort를 Querydsl의 OrderSpecifier로 변환
```java
JPAQuery<Member> query = queryFactory.selectFrom(member);
for (Sort.Order o : pageable.getSort()) {
     PathBuilder pathBuilder = new PathBuilder(member.getType(), member.getMetadata());
     query.orderBy(new OrderSpecifier(o.isAscending() ? Order.ASC : Order.DESC,
     pathBuilder.get(o.getProperty())));
}
List<Member> result = query.fetch();
```
> 참고 : 정렬 (`Sort`)은 조건이 조금만 복잡해져도 `Pageable`의 `Sort` 기능을 사용하기 어렵다.
> 루트 엔티티 범위를 넘어가는 동적 정렬 기능이 필요하면 스프링 데이터 페이징이 제공하는 `Sort`를
> 사용하기 보다는 파라미터를 받아서 직접 처리하는 것을 권장한다.
