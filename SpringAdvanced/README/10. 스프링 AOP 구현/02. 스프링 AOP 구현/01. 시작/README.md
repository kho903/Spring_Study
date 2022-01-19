# 스프링 AOP 구현 - 1. 시작
- 스프링 AOP를 구현하는 일반적인 방법은 `@Aspect`를 사용하는 방법이다.
- `@Aspect`를 사용한 가장 단순한 AOP 구현.

## AspectV1
```java
package hello.aop.order.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

@Slf4j
@Aspect
public class AspectV1 {
    //hello.aop.order 패키지와 하위 패키지
    @Around("execution(* hello.aop.order..*(..))")
    public Object doLog(ProceedingJoinPoint joinPoint) throws Throwable {
        log.info("[log] {}", joinPoint.getSignature()); //join point 시그니처
        return joinPoint.proceed();
    }
}
```
- `@Around` 애노테이션의 값인 `execution(* hello.aop.order..*(..))`는 포인트컷이 된다.
- `@Around` 애노테이션의 메서드인 `doLog`는 어드바이스(`Advice`)가 된다.
- `execution(* hello.aop.order..*(..))`는 `hello.aop.order` 패키지와 그 하위 
  패키지(`..`)를 지정하는 AspectJ 포인트컷 표현식이다.
- 이제 `OrderService`, `OrderRepository`의 모든 메서드는 AOP 적용의 대상이 된다. 참고로
스프링은 프록시 방식의 AOP를 사용하므로 프록시를 통하는 메서드만 적용 대상이 된다.

### 참고
> 스프링 AOP는 AspectJ의 문법을 차용하고, 프록시 방식의 AOP를 제공한다. AspectJ를 직접
> 사용하는 것은 아니다. 스프링 AOP를 사용할 때는 `@Aspect` 애노테이션을 주로 사용하는데,
> 이 애노테이션도 AspectJ가 제공하는 애노테이션이다.

### 참고
> `@Aspect`를 포함한 `org.aspectj` 패키지 관련 기능은 `aspectjweaver.jar` 라이브러리가 
> 제공하는 기능이다. 앞서 `build.gradle`에 `spring-boot-starter-aop`를 포함했는데, 이렇게
> 하면 스프링의 AOP 관련 기능과 함께 `aspectjweaver.jar`도 함께 사용할 수 있게 의존 관계에
> 포함된다. 그런데 스프링에서는 AspectJ가 제공하는 애노테이션이나 관련 인터페이스만 사용하는
> 것이고, 실제 AspectJ가 제공하는 컴파일, 로드타임 위버 등을 사용하는 것은 아니다. 스프링은 
> 지금까지 우리가 학습한 것처럼 프록시 방식의 AOP를 사용한다.

## AopTest - 추가
```java
package hello.aop;

import hello.aop.order.OrderRepository;
import hello.aop.order.OrderService;
import hello.aop.order.aop.AspectV1;
import org.junit.jupiter.api.Test;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Import(AspectV1.class) //추가
@SpringBootTest
public class AopTest {
    @Autowired
    OrderService orderService;
    @Autowired
    OrderRepository orderRepository;

    @Test
    void aopInfo() {
        System.out.println("isAopProxy, orderService=" +
                AopUtils.isAopProxy(orderService));
        System.out.println("isAopProxy, orderRepository=" +
                AopUtils.isAopProxy(orderRepository));
    }

    @Test
    void success() {
        orderService.orderItem("itemA");
    }

    @Test
    void exception() {
        assertThatThrownBy(() -> orderService.orderItem("ex"))
                .isInstanceOf(IllegalStateException.class);
    }
}
```
`@Aspect`는 애스펙트라는 표식이지 컴포넌트의 스캔이 되는 것은 아니다. 따라서 `AspectV1`를
AOP로 사용하려면 스프링 빈으로 등록해야 한다. 스프링 빈으로 등록하는 방법은 다음과 같다.
- `@Bean`을 사용해서 직접 등록
- `@Component` 컴포넌트 스캔을 사용해서 자동 등록
- `@Import` 주로 설정 파일을 추가할 때 사용 (`@Configuration`)
    - `@Import`는 주로 설정 파일을 추가할 떄 사용하지만, 이 기능으로 스프링 빈도 등록할 수 있다.

## 실행
- AopUtils.isAopProxy(...) 도 프록시가 적용되었으므로 true 를 반환한다.
## 실행 - success()
```text
[log] void hello.aop.order.OrderService.orderItem(String)
[orderService] 실행
[log] String hello.aop.order.OrderRepository.save(String)
[orderRepository] 실행
```