# @Query, 리포지토리 메소드에 쿼리 정의하기
## 메소드에 JPQL 쿼리 작성
```java
public interface MemberRepository extends JpaRepository<Member, Long> {
    @Query("select m from Member m where m.username= :username and m.age = :age")
    List<Member> findUser(@Param("username") String username, @Param("age") int age);
}
```
- `@org.springframework.data.jpa.repository.Query` 어노테이션 사용
- 실행할 메서드에 정적 쿼리를 직접 작성하므로 이름 없는 Named 쿼리라 할 수 있음
- JPA Named 쿼리처럼 애플리케이션 실행 시점에 문법 오류를 발견할 수 있음 (매우 큰 장점)

> 참고 : 실무에서는 메소드 이름으로 쿼리 생성 기능은 파라미터가 증가하면 
> 메서드 이름이 매우 지저분해진다. 따라서 `@Query` 기능을 자주 사용하게 된다.
