# 조인
- 내부 조인 : <br>
    SELECT m FROM Member m [INNER] JOIN m.team t
- 외부 조인 : <br>
    SELECT m FROM Member m LEFT [OUTER] JOIN m.team t
- 세타 조인 : <br>
    SELECT count(m) FROM Member m, Team t where m.username = t.name

## 조인 - ON 절
- ON 절을 활용한 조인 (JPA 2.1 부터 지원)
    - 1. 조인 대상 필터링
    - 2. 연관관계 없는 엔티티 외부 조인 (하이버네이트 5.1 부터)

### 1. 조인 대상 필터링
- 예) 회원과 팀을 조인하면서 팀 이름이 A인 팀만 조인
- JPQL : <br>
  SELECT m, t FROM Member m LEFT JOIN m.team t on t.name = 'A'
- SQL : <br>
  SELECT m.*, t.* FROM <br>
  Member m LEFT JOIN Team t On m.TEAM_ID=t.id and t.name='A'

## 2. 연관관계 없는 엔티티 외부 조인
- 예) 회원의 이름과 팀의 이름이 같은 대상 외부 조인
- JPQL : <br>
  SELECT m, t FROM <br>
  Member m LEFT JOIN Team ON m.username = t.name
- SQL : <br>
  SELECT m.*, t.* FROM <br>
  Member m LEFT JOIN Team t ON m.username = t.name
