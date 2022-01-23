# 스프링 AOP 구현 - 05. 어드바이스 순서
- 어드바이스는 기본적으로 순서를 보장하지 않는다. 순서를 지정하고 싶으면 `@Aspect` 적용 단위로 
`org.springframewok.core.annotation.@Order` 애노테이션을 적용해야 한다. 문제는 이것을 
어드바이스 단위가 아니라 클래스 단위로 적용할 수 있다는 점이다. 그래서 지금처럼 하나의 애스펙트에
여러 어드바이스가 있으면 순서를 보장 받을 수 없다. 따라서 애스펙트를 별도의 클래스로 분리해야 한다.
  
- 현재 로그를 남기는 순서가 보장되지 않는 것을 [`doTransaction()` -> `doLog()`] 트랜잭션이
먼저 처리되고, 이후에 로그가 남도록 변경해본다.
  
## AspectV5Order
```java
package hello.aop.order.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;

@Slf4j
public class AspectV5Order {
    @Aspect
    @Order(2)
    public static class LogAspect {
        @Around("hello.aop.order.aop.Pointcuts.allOrder()")
        public Object doLog(ProceedingJoinPoint joinPoint) throws Throwable {
            log.info("[log] {}", joinPoint.getSignature());
            return joinPoint.proceed();
        }
    }

    @Aspect
    @Order(1)
    public static class TxAspect {
        @Around("hello.aop.order.aop.Pointcuts.orderAndService()")
        public Object doTransaction(ProceedingJoinPoint joinPoint) throws
                Throwable {
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
}
```
- 하나의 애스펙트 안에 있던 어드바이스를 `LogAspect`, `TxAspect` 애스펙트로 각각 분리했다.
- 그리고 각 애스펙트에 `@Order` 애노테이션을 통헤 실행 순서를 적용했다.

### AopTest - 변경
```java
//@Import(AspectV4Pointcut.class)
@Import({AspectV5Order.LogAspect.class, AspectV5Order.TxAspect.class})
@SpringBootTest
public class AopTest {
}
```

### 실행
- 실행 결과를 보면 트랜잭션 어드바이스가 먼저 실행되는 것을 확인할 수 있다.
```text
[트랜잭션 시작] void hello.aop.order.OrderService.orderItem(String)
[log] void hello.aop.order.OrderService.orderItem(String)
[orderService] 실행
[log] String hello.aop.order.OrderRepository.save(String)
[orderRepository] 실행
[트랜잭션 커밋] void hello.aop.order.OrderService.orderItem(String)
[리소스 릴리즈] void hello.aop.order.OrderService.orderItem(String)
```