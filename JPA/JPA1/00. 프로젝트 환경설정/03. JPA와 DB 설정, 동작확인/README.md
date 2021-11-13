# JPA와 DB 설정, 동작 확인
- application.yml
```yaml
spring:
  datasource:
     url: jdbc:h2:tcp://localhost/~/jpashop
     username: sa
     password:
     driver-class-name: org.h2.Driver
  jpa:
     hibernate:
         ddl-auto: create
     properties:
         hibernate:
            # show_sql: true
             format_sql: true
logging.level:
     org.hibernate.SQL: debug
    # org.hibernate.type: trace
```
- spring.jpa.hibernate.ddl-auto: create
    - 이 옵션은 애플리케이션 실행 시점에 테이블을 drop 하고, 다시 생성한다.

### 참고
- 모든 로그 출력은 가급적 로거를 통해 남겨야 한다.
    - `show_sql` : 옵션은 `System.out`에 하이버네이트 실행 SQL을 남긴다.
    - `org.hibernate.SQL` : 옵션은 logger를 통해 하이버네이트 실행 SQL을 남긴다.

### 주의
- `application.yml` 같은 yml 파일은 띄어쓰기(스페이스) 2칸으로 계층을 만드므로 띄어쓰기 2칸 필수!
- 예를 들어서 아래의 datasource 는 spring: 하위에 있고 앞에 띄어쓰기 2칸이 있으므로
  spring.datasource 가 된다.

## 실제 동작 확인
### 회원 엔티티
```java
@Entity
@Getter @Setter
public class Member {
    @Id @GeneratedValue
    private Long id;
    private String username;
 ...
}
```
### 회원 리포지토리
```java
@Repository
public class MemberRepository {
    @PersistenceContext
    EntityManager em;

    public Long save(Member member) {
        em.persist(member);
        return member.getId();
    }

    public Member find(Long id) {
        return em.find(Member.class, id);
    }
}
```

### 테스트
```java
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MemberRepositoryTest {
    @Autowired
    MemberRepository memberRepository;

    @Test
    @Transactional
    @Rollback(false)
    public void testMember() {
        Member member = new Member();
        member.setUsername("memberA");
        Long savedId = memberRepository.save(member);
        Member findMember = memberRepository.find(savedId);
        Assertions.assertThat(findMember.getId()).isEqualTo(member.getId());

        Assertions.assertThat(findMember.getUsername()).isEqualTo(member.getUsername())
        ;
        Assertions.assertThat(findMember).isEqualTo(member); //JPA 엔티티 동일성 보장
    }
}
```

### 참고
- 스프링 부트를 통해 복잡한 설정이 다 자동화되었다. `persistence.xml`도 없고,
  `LocalContainerEntityManagerFactoryBean`도 없다.
  
## 쿼리 파라미터 로그 남기기
- 로그에 다음을 추가하기 `org.hibernate.type` : SQL 실행 파라미터를 로그로 남긴다.
- 외부 라이브러리 사용 : https://github.com/gavlyukovskiy/spring-boot-data-source-decorator
  - 참고: 쿼리 파라미터를 로그로 남기는 외부 라이브러리는 시스템 자원을 사용하므로, 개발 단계에서는 편하게 사용해도 된다. 
    하지만 운영시스템에 적용하려면 꼭 성능테스트를 하고 사용하는 것이 좋다.
