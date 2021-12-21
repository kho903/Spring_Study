# 리포지토리 지원 - QuerydslRepositorySupport
QuerydslRepositorySupport
## 장점
- `getQuerydsl().applyPagination()` 스프링 데이터가 제공하는 페이징을 Querydsl로
편리하게 반환 가능 (단! Sort는 오류 발생)
- `from()`으로 시작 가능 (최근에는 QueryFactory를 사용해서 `select()`로 시작하는 것이 더 명시적)
- EntityManager 제공

## 한계
- Querydsl 3.x 버전을 대상으로 만듬
- Querydsl 4.x에 나온 JPAQueryFactory로 시작할 수 없음
    - select로 시작할 수 없음
- `QueryFactory`를 제공하지 않음
- 스프링 데이터 Sort