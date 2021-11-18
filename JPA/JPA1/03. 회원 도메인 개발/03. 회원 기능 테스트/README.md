# 회원 기능 테스트
## 테스트 요구사항
- 회원가입을 성공해야 한다.
- 회원가입 할 떄 같은 이름이 있으면 예외가 발생해야 한다.

## 회원가입 테스트 코드
```java
package jpabook.jpashop.service;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class MemberServiceTest {
    @Autowired
    MemberService memberService;
    @Autowired
    MemberRepository memberRepository;

    @Test
    public void 회원가입() throws Exception {
        //Given
        Member member = new Member();
        member.setName("kim");
        //When
        Long saveId = memberService.join(member);
        //Then
        assertEquals(member, memberRepository.findOne(saveId));
    }

    @Test(expected = IllegalStateException.class)
    public void 중복_회원_예외() throws Exception {
        //Given
        Member member1 = new Member();
        member1.setName("kim");
        Member member2 = new Member();
        member2.setName("kim");
        //When
        memberService.join(member1);
        memberService.join(member2); //예외가 발생해야 한다.
        //Then
        fail("예외가 발생해야 한다.");
    }
}
```
## 기술 설명
- `@RunWith(SpringRunner.class)` : 스프링과 테스트 통합
- `@SpringBooteTest` : 스프링 부트 띄우고 테스트 (이계 없으면 `@Autowired` 다 실패)
- `@Transactional` : 반복 가능한 테스트 지원, 각각의 테스트를 실행할 때마다 트랜잭션을 시작하고
테스트가 끝나면 트랜잭션을 강제로 롤백 (이 어노테이션이 테스트 케이스에서 사용할 때만 롤백)

## 기능 설명
- 회원가입 테스트
- 중복 회원 예외처리 테스트

## 테스트 케이스를 위한 설정
- 테스트는 케이스 격리된 환경에서 실행하고, 끝나면 데이터를 초기화하는 것이 좋다. 그런 면에서
메모리 DB를 사용하는 것이 가장 이상적이다.
- 추가적으로 테스트 케이스를 위한 스프링 환경과, 일반적으로 애플리케이션을 실행하는 환경은 보통 다르므로
설정 파일을 다르게 사용
- 다음과 같이 간단하게 테스트용 설정파일 추가

- `test/resources/application.yml`
```yaml
spring:
# datasource:
# url: jdbc:h2:mem:testdb
# username: sa
# password:
# driver-class-name: org.h2.Driver
# jpa:
# hibernate:
# ddl-auto: create
# properties:
# hibernate:
 # show_sql: true
# format_sql: true
# open-in-view: false
logging.level:
 org.hibernate.SQL: debug
# org.hibernate.type: trace
```
- 이렇게 하면 스프링을 실행 할 때 이 위치에 있는 설정 파일을 읽는다.
- 스프링 부트는 dataSource 설정이 없으면, 기본적으로 메모리 DB를 사용하고,
driver-class도 현재 등록된 라이브러리를 보고 찾아준다.
- 추가로 `ddl-auto`도 `create-drop` 모드로 동작한다.
- 따라서 데이터소스나, JPA 관련 별도 추가설정 필요 X
