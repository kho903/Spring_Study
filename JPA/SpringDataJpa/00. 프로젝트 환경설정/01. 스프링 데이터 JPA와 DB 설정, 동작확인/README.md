# 스프링 데이터 JPA와 DB 설정, 동작확인
- application.yml
```yaml
spring:
  datasource:
    url: jdbc:h2:tcp://localhost/~/datajpa
    username: sa
    password:
    driver-class-name: org.h2.Driver
  jpa:
    hibernate:
      ddl-auto: create
    properties:
      hibernate:
#        show_sql: true
        format_sql: true

logging.level:
  org.hibernate.SQL: debug
#  org.hibernate.type: trace

```
- spring.jpa.hibernate.ddl-auto: create
    - 이 옵션은 애플리케이션 실행 시점에 테이블을 drop 하고, 다시 생성한다.
- show_sql : 옵션은 `System.out`에 하이버네이트 실행 SQL을 남긴다.
- org.hibernate.SQL : 옵션은 logger를 통해 하이버네이트 실행 SQL을 남긴다.

## 실제 동작하는 지 확인하기
- 회원 엔티티
```java

@Entity
@Getter
@Setter
public class Member {
    @Id
    @GeneratedValue
    private Long id;
    private String username;
 ...
}
```
- 회원 JPA 리포지토리
```java
@Repository
public class MemberJpaRepository {
    @PersistenceContext
    private EntityManager em;

    public Member save(Member member) {
        em.persist(member);
        return member;
    }

    public Member find(Long id) {
        return em.find(Member.class, id);
    }
}
```
- JPA 기반 테스트
```java
@SpringBootTest
@Transactional
@Rollback(false)
public class MemberJpaRepositoryTest {
    @Autowired
    MemberJpaRepository memberJpaRepository;

    @Test
    public void testMember() {
        Member member = new Member("memberA");
        Member savedMember = memberJpaRepository.save(member);
        Member findMember = memberJpaRepository.find(savedMember.getId());
        assertThat(findMember.getId()).isEqualTo(member.getId());
        assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
        assertThat(findMember).isEqualTo(member); //JPA 엔티티 동일성 보장
    }
}
```
- 스프링 데이터 JPA 리포지토리
```java
public interface MemberRepository extends JpaRepository<Member, Long> {
}
```
- 스프링 데이터 JPA 기반 테스트
```java
@SpringBootTest
@Transactional
@Rollback(false)
public class MemberRepositoryTest {
    @Autowired
    MemberRepository memberRepository;

    @Test
    public void testMember() {
        Member member = new Member("memberA");
        Member savedMember = memberRepository.save(member);
        Member findMember =
                memberRepository.findById(savedMember.getId()).get();
        Assertions.assertThat(findMember.getId()).isEqualTo(member.getId());

        Assertions.assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
        Assertions.assertThat(findMember).isEqualTo(member); //JPA 엔티티 동일성 보장
    }
}
```
- Entity, Repository 동작 확인
- jar 빌드해서 동작 확인
> 참고 : 스프링 부트를 통해 복잡한 설정이 다 자동화되었다. `persistence.xml`도 없고
> `LocalContainerEntityManagerFactoryBean`도 없다. 스프링 부트를 통한 추가 설정은
> 스프링 부트 메뉴얼을 참고한다.

### 쿼리 파라미터 로그 남기기
- 로그에 다음을 추가하기 : `org.hibernate.type` : SQL 실행 파라미터를 로그로 남긴다.
- 외부 라이브러리 사용 : https://github.com/gavlyukovskiy/spring-boot-data-source-decorator
    - implementation 'com.github.gavlyukovskiy:p6spy-spring-boot-starter:1.5.7'
> 참고 : 쿼리 파라미터를 로그로 남기는 외부 라이브러리는 시스템 자원을 사용하므로, 개발 단계에서는
> 편하게 사용해도 된다. 하지만 운영시스템에 적용하려면 꼭 성능테스트를 하고 사용하는 것이 좋다.
