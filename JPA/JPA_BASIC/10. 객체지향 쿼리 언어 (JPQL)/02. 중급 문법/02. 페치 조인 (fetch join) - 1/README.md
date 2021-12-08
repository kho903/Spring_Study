# JPQL - 페치 조인 (fetch join)
## 페치 조인 (fetch join)
- SQL 조인 종류 X
- JPQL에서 성능 최적화를 위해 제공하는 기능
- 연관된 엔티티나 컬렉션을 SQL 한 번에 함께 조회하는 기능
- join fetch 명령어 사용
- 페치 조인 ::= [ LEFT [OUTER] | INNER ] JOIN FETCH 조인경로

## 엔티티 페치 조인
- 회원을 조회하면서 연관된 팀도 함께 조회 (SQL 한 번에)
- SQL을 보면 회원 뿐만 아니라 팀(T.*)도 함께 SELECT
- [JPQL] <br>
  select m from Member m join fetch m.team
- [SQL] <br>
  SELECT M.*, T.* FROM MEMBER M
  INNER JOIN TEAM T ON M.TEAM_ID = T.ID

## 페치 조인 사용 코드
```java
String jpql = "select m from Member m join fetch m.team";
List<Member> members = em.createQuery(jpql, Member.class)
                        .getResultList();

for (Member member : members) {
    // 페치 조인으로 회원과 팀을 함께 조회해서 지연 로딩 X
    System.out.println("username = " + member.getUsername() + ", " +
                    "teamName = " + member.getTeam().name());
}
```
```text
// 결과
username = 회원1, teamname = 팀A
username = 회원2, teamname = 팀A
username = 회원3, teamname = 팀B 
```

## 컬렉션 페치 조인
- 일대다 관계, 컬렉션 페치 조인
- [JPQL] <br>
  select t                            <br>
  from Team t join fetch t.members    <br>
  where t.name = '팀A'
- [SQL] <br>
  SELECT T.*, M.*                     <br>
  FROM TEAM T                         <br>
  INNER JOIN MEMBER M ON T.ID = M.TEAM_ID <br>
  WHERE T.NAME = '팀A'

## 컬렉션 페치 조인 사용 코드
```java
String query = "select t from Team t join fetch t.members";
List<Team> result = em.createQuery(query, Team.class)
        .getResultList();
for (Team team : result) {
    System.out.println("team = " + team.getName() + " | members = "+ team.getMembers().size());
    for (Member member : team.getMembers()) {
        // 페치 조인으로 팀과 회원을 함께 조회해서 지연 로딩 발생 안함
        System.out.println(" ==> member = " + member);
    }
}
```
```text
// 출력 결과
team = 팀A | members = 2
 ==> member = Member{id=3, username='회원1', age=0}
 ==> member = Member{id=4, username='회원2', age=0}
team = 팀A | members = 2
 ==> member = Member{id=3, username='회원1', age=0}
 ==> member = Member{id=4, username='회원2', age=0}
team = 팀B | members = 1
 ==> member = Member{id=5, username='회원3', age=0}
```

## 페치 조인과 DISTINCT
- SQL의 DISTINCT는 중복된 결과를 제거하는 명령
- JPQL의 DISTINCT 2가지 기능 제공
  - 1. SQL에 DISTINCT를 추가
  - 2. 애플리케이션에서 엔티티 중복 제거

### 페치 조인과 DISTINCT
- select distinct t                   <br>
  from Team t join fetch t.members    <br>
  where t.name = '팀A'
- SQL에 DISTINCT를 추가하지만 데이터가 다르므로 SQL 결과에서 중복제거 실패
- DISTINCT가 추가로 애플리케이션에서 중복 제거시도
- 같은 식별자를 가진 Team 엔티티 제거
```text
// DISTINCT 추가시 결과
team = 팀A | members = 2
 ==> member = Member{id=3, username='회원1', age=0}
 ==> member = Member{id=4, username='회원2', age=0}
team = 팀B | members = 1
 ==> member = Member{id=5, username='회원3', age=0}
```

## 페치 조인과 일반 조인의 차이
- 일반 조인 실행 시 연관된 엔티티를 함께 조회하지 않음
- [JPQL]                       <br>
  select t                     <br>
  from Team t join t.members m <br>
  where t.name = '팀A'
- [SQL]                                   <br>
  SELECT T.*                              <br>
  FROM TEAM T                             <br>
  INNER JOIN MEMBER M ON T.ID = M.TEAM_ID <br>
  WHERE T.NAME = '팀A'
  
- JPQL은 결과를 반환할 때 연관관계 고려 X
- 단지 SELECT 절에 지정한 엔티티만 조회할 뿐
- 여기서는 팀 엔티티만 조회하고, 회원 엔티티는 조회 X
- 페치 조인을 사용할 때만 연관된 엔티티도 함께 조회 (즉시 로딩)
- 페치 조인은 객체 그래프를 SQL 한 번에 조회하는 개념

## 페치 조인 실행 예시
- 페치 조인은 연관된 엔티티를 함께 조회함
- [JPQL]                            <br>
  select t                          <br>
  from Team t join fetch t.members  <br>
  where t.name = ‘팀A' 
- [SQL]
  SELECT T.*, M.*                        <br>
  FROM TEAM T                            <br>
  INNER JOIN MEMBER M ON T.ID=M.TEAM_ID  <br>
  WHERE T.NAME = '팀A'