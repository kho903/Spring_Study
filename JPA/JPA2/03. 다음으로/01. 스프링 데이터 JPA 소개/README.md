# 스프링 데이터 JPA 소개
- 스프링 데이터 JPA는 JPA를 사용할 때 지루하게 반복하는 코드를 자동화 해준다.
- `MemberRepository`를 스프링 데이터 JPA로 변경
## MemberRepository
```java
package jpabook.jpashop.repository;

import jpabook.jpashop.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class MemberRepository {
    private final EntityManager em;

    public void save(Member member) {
        em.persist(member);
    }

    public Member findOne(Long id) {
        return em.find(Member.class, id);
    }

    public List<Member> findAll() {
        return em.createQuery("select m from Member m", Member.class)
                .getResultList();
    }

    public List<Member> findByName(String name) {
        return em.createQuery("select m from Member m where m.name = :name",
                Member.class)
                .setParameter("name", name)
                .getResultList();
    }
}
```
- 스프링 데이터 JPA 적용
```java
package jpabook.jpashop.repository;

import jpabook.jpashop.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MemberRepository extends JpaRepository<Member, Long> {
    List<Member> findByName(String name);
}
```
- findOne() -> findById() 로 변경해야 한다.
- 스프링 데이터 JPA는 `JpaRepository`라는 인터페이스를 제공하는데, 여기에 기본적인 CRUD
기능이 모두 제공된다. (일반적으로 상상할 수 있는 모든 기능이 다 포함되어 있다.)
- `findByName`처럼 일반화하기 어려운 기능도 메서드 이름으로 정확한 JPQL 쿼리를 실행한다.
    - `select m from Member m where m.name = :name`
- 개발자는 인터페이스만 만들면 된다. 구현체는 스프링 데이터 JPA가 애플리케이션 실행시점에 주입해준다.

### 정리
- 스프링 데이터 JPA는 스프링과 JPA를 활용해서 애플리케이션을 만들 때 정말 편리한 기능을 많이 제공한다.
- 단순히 편리함을 넘어서 때로는 마법을 부리는 것 같을 정도로 놀라운 개발 생산성을 보여준다.
- 하지만 스프링 데이터 JPA는 JPA를 사용해서 이런 기능을 제공할 뿐이다.
- 결국 JPA 자체를 잘 이해하는 것이 가장 중요하다.
