# 플러시
- 영속성 컨텍스트의 변경 내용을 데이터베이스에 반영

## 플러시 발생
- 변경 감지
- 수정된 엔티티 쓰기 지연 SQL 저장소에 등록
- 쓰기 지연 SQL 저장소의 쿼리를 데이터베이스에 전송
  (등록, 수정, 삭제 쿼리)
  
## 영속성 컨텍스트를 플러시하는 방법
- em.flush() - 직접 호출
- 트랜잭션 커밋 - 플러시 자동 호출
- JPQL 쿼리 실행 - 플러시 자동 호출

## JPQL 쿼리 실행 시 플러시가 자동으로 호출되는 이유
```java
em.persist(memberA);
em.persist(memberB);
em.persist(memberC);

// 중간에 JPQL 실행
query = em.createQuery("select m from Member m", Member.class);
List<Member> members = query.getResultList();
```

## 플러시 모드 옵션
`em.setFlushMode(FlushModeType.COMMIT)`
- FlushModeType.AUTO : 커밋이나 쿼리를 실행할 때 플러시 (기본값)
- FlushModeType.COMMIT : 커밋할 때만 플러시

## 플러시는!
- 영속성 컨텍스트를 비우지 않음
- 영속성 컨텍스트의 변경내용을 데이터베이스에 동기화
- 트랜잭션이라는 작업 단위가 중요 -> 커밋 직전에만 동기화하면 됨
