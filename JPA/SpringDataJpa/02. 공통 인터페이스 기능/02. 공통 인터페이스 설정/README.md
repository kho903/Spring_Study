# 공통 인터페이스 설정
## JavaConfig 설정 - 스프링 부트 사용 시 생략 가능
```java
@Configuration
@EnableJpaRepositories(basePackages = "jpabook.jpashop.repository")
public class AppConfig {
}
```
- 스프링 부트 사용 시 `@SpringBootApplication` 위치를 지정 (해당 패키지와 하위 패키지 인식)
- 만약 위치가 달라지면 `@EnableJpaRepository` 필요

## 스프링 데이터 JPA가 구현 클래스 대신 생성
- `org.springframwork.data.repository.Repository`를 구현한 클래스는 스캔 대상
    - MemberRepository 인터페이스가 동작한 이유
    - 실제 출력해보기 (Proxy)
    - memberRepository.getClass() -> class com.sun.proxy.$ProxyXX
- `@Repository` 애노테이션 생략 가능
    - 컴포넌트 스캔을 스프링 데이터 JPA가 자동으로 처리
    - JPA 예외를 스프링 예외로 변환하는 과정도 자동으로 처리
