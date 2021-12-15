# JPA NamedQuery
- JPA의 NamedQuery를 호출할 수 있음
## `@NamedQuery` 어노테이션으로 Named 쿼리 정의
```java

@Entity
@NamedQuery(
        name = "Member.findByUsername",
        query = "select m from Member m where m.username = :username")
public class Member {
 ...
}
```
## JPA를 직접 사용해서 Named 쿼리 호출
```java
public class MemberRepository {
    public List<Member> findByUsername(String username) {
        ...
        
        List<Member> resultList =
                em.createNamedQuery("Member.findByUsername", Member.class)
                        .setParameter("username", username)
                        .getResultList();
    }
} 
```
## 스프링 데이터 JPA로 NamedQuery 사용
```java
@Query(name = "Member.findByUsername")
List<Member> findByUsername(@Param("username") String username);
```
- `@Query`를 생략하고 메서드 이름만으로 Named 쿼리를 호출할 수 있다.
## 스프링 데이터 JPA로 Named 쿼리 호출
```java
public interface MemberRepository
        extends JpaRepository<Member, Long> { //** 여기 선언한 Member 도메인 클래스
    List<Member> findByUsername(@Param("username") String username);
}
```
- 스프링 데이터 JPA는 선언한 "도메인 클래스 + .(점) + 메서드 이름"으로 Named 쿼리를 찾아서 실행
- 만약 실행할 Named 쿼리가 없으면 메서드 이름으로 쿼리 생성 전략을 사용한다.
- 필요하면 전략을 변경할 수 있지만 권장하지 않는다.

> 참고 : 스프링 데이터 JPA를 사용하면 실무에서 Named Query를 직접 등록해서 사용하는 일은
> 드물다. 대신 `@Query`를 사용해서 리포지토리 메소드에 쿼리를 직접 정의한다.
