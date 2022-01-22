# 스프링 AOP 구현 - 03. 어드바이스 추가
앞서 로그를 출력하는 기능 + 트랜잭션 적용 코드

## 트랜잭션 기능은 보통 다음과 같이 동작
- 핵심 로직 실행 직전에 트랜잭션을 시작
- 핵심 로직 실행
- 핵심 로직 실행에 문제가 없으면 커밋
- 핵심 로직 실행에 예외가 발생하면 롤백

## AspectV3
```java
package hello.aop.order.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

@Slf4j
@Aspect
public class AspectV3 {
    //hello.aop.order 패키지와 하위 패키지
    @Pointcut("execution(* hello.aop.order..*(..))")
    public void allOrder() {
    }

    //클래스 이름 패턴이 *Service
    @Pointcut("execution(* *..*Service.*(..))")
    private void allService() {
    }

    @Around("allOrder()")
    public Object doLog(ProceedingJoinPoint joinPoint) throws Throwable {
        log.info("[log] {}", joinPoint.getSignature());
        return joinPoint.proceed();
    }

    //hello.aop.order 패키지와 하위 패키지 이면서 클래스 이름 패턴이 *Service
    @Around("allOrder() && allService()")
    public Object doTransaction(ProceedingJoinPoint joinPoint) throws Throwable {
        try {
            log.info("[트랜잭션 시작] {}", joinPoint.getSignature());
            Object result = joinPoint.proceed();
            log.info("[트랜잭션 커밋] {}", joinPoint.getSignature());
            return result;
        } catch (Exception e) {
            log.info("[트랜잭션 롤백] {}", joinPoint.getSignature());
            throw e;
        } finally {
            log.info("[리소스 릴리즈] {}", joinPoint.getSignature());
        }
    }
}
```
- `allOrder()` 포인트컷은 `hello.aop.order` 패키지와 하위 패키지를 대상으로 한다.
- `allService()` 포인트컷은 타입 이름 패턴이 `*Service`를 대상으로 하는데 쉽게 이야기해서
`XxxService`처럼 `Service`로 끝나는 것을 대상으로 한다. `*Servi*`와 같은 패턴도 가능.
- 여기서 타입 이름 패턴이라고 한 이유는 클래스, 인터페이스에 모두 적용되기 때문이다.


### `@Around("allOrder() && allService()")`
- 포인트컷은 이렇게 조합할 수 있다. `&&`, `||`, `!` 3가지 조합이 가능하다.
- `hello.aop.order` 패키지와 하위 패키지 이면서 타입 이름 패턴이 `*Service`인 것을 대상으로 한다.
- 결과적으로 `doTransaction()` 어드바이스는 `OrderService`에만 적용된다.
- `doLog()` 어드바이스는 `OrderService`, `OrderRepository`에 모두 적용된다.

### 포인트컷이 적용된 AOP 결과는 다음과 같다.
- `orderService` : `doLog()`, `doTransaction()` 어드바이스 적용
- `orderRepository` : `doLog()` 어드바이스 적용

## AopTest - 수정
```java
//@Import(AspectV1.class)
//@Import(AspectV2.class)
@Import(AspectV3.class)
@SpringBootTest
public class AopTest {
}
```
### 실행 - success()
```text
[log] void hello.aop.order.OrderService.orderItem(String)
[트랜잭션 시작] void hello.aop.order.OrderService.orderItem(String)
[orderService] 실행
[log] String hello.aop.order.OrderRepository.save(String)
[orderRepository] 실행
[트랜잭션 커밋] void hello.aop.order.OrderService.orderItem(String)
[리소스 릴리즈] void hello.aop.order.OrderService.orderItem(String)
```
## 실행 순서
### AOP 적용 전
클라이언트 -> `orderService.orderItem()` -> `orderRepository.save()`

### AOP 적용 후
클라이언트 -> [`doLog()` -> `doTransaction()`] -> `orderService.orderItem()`
-> [`doLog()`] -> `orderRepository.save()`

- `orderService`에는 `doLog()`, `doTransaction()` 두가지 어드바이스가 적용되어 있고,
`orderRepository`에는 `doLog()` 하나의 어드바이스만 적용된 것을 확인할 수 있다.
  
### 실행 - exception()
```text
[log] void hello.aop.order.OrderService.orderItem(String)
[트랜잭션 시작] void hello.aop.order.OrderService.orderItem(String)
[orderService] 실행
[log] String hello.aop.order.OrderRepository.save(String)
[orderRepository] 실행
[트랜잭션 롤백] void hello.aop.order.OrderService.orderItem(String)
[리소스 릴리즈] void hello.aop.order.OrderService.orderItem(String)
```
- 예외 상황에서는 트랜잭션 커밋 대신에 트랜잭션 롤백이 호출되는 것을 확인할 수 있다.
- 그런데 여기에서 로그를 남기는 순서가 [`doLog()` -> `doTransaction()`] 순서로
작동한다.
- 만약 어드바이스가 적용되는 순서를 바꾸고 싶으면 `doTransaction()` -> `doLog()`
이렇게 트랜잭션 이후에 로그를 남겨야 할 것이다.
