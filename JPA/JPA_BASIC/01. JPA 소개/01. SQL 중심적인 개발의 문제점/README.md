# SQL 중심적인 개발의 문제점
- 지금 시대는 객체를 관계형 DB에 관리
    - SQL...
- 무한 반복, 지루한 코드
    - CRUD query (insert into, update, select, delete...)
- SQL에 의존적인 개발을 피하기가 어렵다.
## 패러다임의 불일치
- 객체 vs 관계형 데이터베이스
- 객체지향 프로그래밍은 추상화, 캡슐화, 정보은닉, 상속, 다형성 등
시스템의 복잡성을 제어할 수 있는 다양한 장치들을 제공한다.
- 객체를 영구 보관하는 다양한 저장소
    - RDB, NoSQL, File, ODB...
    - 현실적인 대안은 관계형 데이터베이스

## 객체와 관계형 데이터베이스의 차이
1. 상속
2. 연관관계
3. 데이터 타입
4. 데이터 식별 방법
- 조회할 때 번잡해짐
### 자바 컬렉션에 저장하면?
- list.add(album);
### 자바 컬렉션에서 조회하면?
- Album album = list.get(albumId);
- 부모타입으로 조회 후 다형성 활용
    - Item item = list.get(albumId);

## 연관관계
- 객체는 참조를 사용 : member.getTeam();
- 테이블은 외래 키를 사용 : JOIN ON M.TEAM_ID = T.TEAM_ID
- 보통 객체를 테이블에 맞추어 모델링
```java
class Member {
    String id;          // MEMBER_ID 컬럼 사용
    Long teamId;        // TEAM_ID FK 컬럼 사용
    String username;    // USERNAME 컬럼 사용
}

class Team {
    Long id;            // TEAM_ID PK 사용
    String name;        // NAME 컬럼 사용
}
```
- INSERT INTO MEMBER(MEMBER_ID, TEAM_ID, USERNAME) VALUES ...

## 객체다운 모델링
```java
class Member {
    String id;          // MEMBER_ID 컬럼 사용
    Team team;          // 참조로 연관관계를 맺는다.
    String username;    // USERNAME 컬럼 사용
    
    Team getTeam() {
        return team;
    }
}

class Team {
    Long id;            // TEAM_ID PK 사용
    String name;        // NAME 컬럼 사용
}
```
- 객체 모델링 저장 : member.getTeam().getId(); 로 TEAM_ID 해결
  - INSERT INTO MEMBER(MEMBER_ID, TEAM_ID, USERNAME) VALUES ...
- 객체 모델링 조회
```sql
SELECT M.*, T.*
FROM MEMBER M 
JOIN TEAM T ON M.TEAM_ID = T.TEAM_ID;
```
```java
public Member find(String memberId) {
    // SQL 실행...
    Member member = new Member();
    // 데이터베이스에서 조회한 회원 관련 정보를 모두 입력
    Team team = new Team();
    // 데이터베이스에서 조회한 팀 관련 정보를 모두 입력
    
    // 회원과 팀 관계 설정
    member.setTeam(team);
    return member;
}
```
- 객체 모델링, 자바 컬렉션에 관리
  - list.add(member);
  - Member member = list.get(memberId);
  - Team team = member.getTeam();
- 문제 : 처음 실행하는 SQL에 따라 탐색 범위 결정
```sql
SELECT M.*, T.*
  FROM MEMBER M 
  JOIN TEAM T ON M.TEAM_ID = T.TEAM_ID
```
  - member.getTeam();  // OK
  - member.getOrder(); // null
### 엔티티 신뢰 문제
```java
class MemberService {
    //...
    public void process() {
        Member member = memberDAO.find(memberId);
        member.getTeam(); // ??
        member.getOrder().getDelivery(); // ??
    }
}
```

### 모든 객체를 미리 로딩할 수는 없다.
- 상황에 따라 동일한 회원 조회 메서드를 여러번 생성
```java
memberDAO.getMember(); // Member만 조회
memberDAO.getMemberWithTeam(); // Member와 Team 조회

// Member, Order, Delivery
memberDAO.getMemberWithOrderWithDelivery();
```
- 계층형 아키텍처, 진정한 의미의 계층 분할이 어렵다.

### 비교하기
```java
String memberId = "100";
Member member1 = memberDAO.getMember(memberId);
Member member2 = memberDAO.getMember(memberId);
member1 == member2 // 다르다.

class MemberDAO {
    public Member getMember(String memberId) {
        String sql = "SELECT * from MEMBER where MEMBER_ID = ?";
        ...
        // JDBC API, SQL 실행
        return new Member(...);
    }
}
```
- 자바 컬렉션에서 조회
```java
String memberId = "100";
Member member1 = list.get(memberId);
Member member2 = list.get(memberId);

member1 == member2 // 같다.
```
- 객체답게 모델링 할수록 매핑 작업만 늘어난다.
- 객체를 자바 컬렉션에 저장하듯이 DB에 저장할 수는 없을까?<br>
-> JPA