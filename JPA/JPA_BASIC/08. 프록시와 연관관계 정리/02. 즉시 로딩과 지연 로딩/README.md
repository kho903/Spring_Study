# 즉시 로딩과 지연 로딩
## Member를 조회할 때 Team도 함께 조회해야 할까?
- 단순히 member 정보만 사용하는 비즈니스 로직
- println(member.getName());

## 지연 로딩 LAZY를 사용해서 프록시로 조회
```java
@Entity
public class Member {
    
    @Id
    @GeneratedValue
    private Long id;
    
    @Column(name = "USERNAME")
    private String name;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TEAM_ID")
    private Team team;
    ...
}
```

## 지연로딩
1. 로딩 -> member1
2. 지연로딩 LAZY (member1 -> team1 (프록시 team1 엔티티))

### 지연로딩 LAZY를 사용해서 프록시로 조회
- Member member = em.find(Member.class, 1L);
- Team team = member.getTeam();
- team.getName(); // 실제 team 을 사용하는 시점에 초기화 (DB 조회)

## Member와 Team을 자주 함께 사용한다면?
## 즉시 로딩 EAGER를 사용해서 함께 조회
```java
@Entity
public class Member {
    @Id
    @GeneratedValue
    private Long id;
    
    @Column(name = "USERNAME")
    private String name;
    
    @ManyToOne(fetch = FetchType.EAGER) //**
    @JoinColumn(name = "TEAM_ID")
    private Team team;
    ...
}
```

## 즉시 로딩
1. 로딩 -> member1
2. 즉시 로딩 EAGER : member1 -> team1 (실제 team1 엔티티)

## 즉시 로딩 (EAGER), Member 조회 시 항상 Team도 조회
- JPA 구현체는 가능하면 조인을 사용해서 SQL 한 번에 함께 조회

## 프록시와 즉시로딩 주의
- 가급적 지연 로딩만 사용 (특히 실무에서)
- 즉시 로딩을 적용하면 예상하지 못한 SQL이 발생
- 즉시 로딩은 JPQL에서 N + 1 문제를 일으킨다.
- @ManyToOne, @OneToOne은 기본이 즉시 로딩 -> LAZY로 설정
- @OneToMany, @ManyToMany는 기본이 지연 로딩

## 지연 로딩 활용 (이론적일 뿐, 실무에선 전부 지연 로딩)
- Member와 Team은 자주 함께 사용 -> 즉시 로딩
- Member와 Order는 가끔 사용 -> 지연 로딩
- Order와 Product는 자주 함께 사용 -> 즉시 로딩

### 지연 로딩 활용 - 실무
- 모든 연관관계에 지연 로딩을 사용해라!
- 실무에서 즉시 로딩을 사용하지 마라!
- JPQL fetch 조인이나, 엔티티 그래프 기능을 사용해라!
- 즉시 로딩은 상상하지 못한 쿼리가 나간다.
