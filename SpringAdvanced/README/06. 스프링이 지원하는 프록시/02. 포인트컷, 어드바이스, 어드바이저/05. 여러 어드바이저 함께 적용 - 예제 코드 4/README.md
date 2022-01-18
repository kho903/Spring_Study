# 여러 어드바이저 함께 적용
- 어드바이저는 하나의 포인트컷과 하나의 어드바이스를 가지고 있다.
- 만약 여러 어드바이저를 하나의 `target`에 적용하려면 어떻게 해야 할까. 즉, 하나의 `target`에
여러 어드바이스를 어떻게 적용할까?
  
## 여러 프록시
## MultiAdvisorTest
```java
package hello.proxy.advisor;

import hello.proxy.common.advice.TimeAdvice;
import hello.proxy.common.service.ServiceImpl;
import hello.proxy.common.service.ServiceInterface;
import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.aop.Pointcut;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.support.DefaultPointcutAdvisor;

public class MultiAdvisorTest {
    @Test
    @DisplayName("여러 프록시")
    void multiAdvisorTest1() {
        //client -> proxy2(advisor2) -> proxy1(advisor1) -> target
        //프록시1 생성
        ServiceInterface target = new ServiceImpl();
        ProxyFactory proxyFactory1 = new ProxyFactory(target);
        DefaultPointcutAdvisor advisor1 = new
                DefaultPointcutAdvisor(Pointcut.TRUE, new Advice1());
        proxyFactory1.addAdvisor(advisor1);
        ServiceInterface proxy1 = (ServiceInterface) proxyFactory1.getProxy();
        //프록시2 생성, target -> proxy1 입력
        ProxyFactory proxyFactory2 = new ProxyFactory(proxy1);
        DefaultPointcutAdvisor advisor2 = new
                DefaultPointcutAdvisor(Pointcut.TRUE, new Advice2());
        proxyFactory2.addAdvisor(advisor2);
        ServiceInterface proxy2 = (ServiceInterface) proxyFactory2.getProxy();
        //실행
        proxy2.save();
    }

    @Slf4j
    static class Advice1 implements MethodInterceptor {
        @Override
        public Object invoke(MethodInvocation invocation) throws Throwable {
            log.info("advice1 호출");
            return invocation.proceed();
        }
    }

    @Slf4j
    static class Advice2 implements MethodInterceptor {
        @Override
        public Object invoke(MethodInvocation invocation) throws Throwable {
            log.info("advice2 호출");
            return invocation.proceed();
        }
    }
}
```
### 실행 결과
```text
MultiAdvisorTest$Advice2 - advice2 호출
MultiAdvisorTest$Advice1 - advice1 호출
ServiceImpl - save 호출
```
- 포인트컷은 `advisor1`, `advisor2` 모두 항상 `true`를 반환하도록 설정했다. 따라서
둘 다 어드바이스가 적용된다.

## 여러 프록시의 문제
- 이 방법이 잘못된 것은 아니지만 만약 적용해야 하는 얻바이저가 10개라면 10개의 프록시를 생성해야 한다.

## 하나의 프록시, 여러 어드바이저
- 스프링은 이 문제를 해결하기 위해 하나의 프록시에 여러 어드바이저를 적용할 수 있게 만들어 두었다.

## MultiAdvisorTest - multiAdvisorTest2() 추가
```java
@Test
@DisplayName("하나의 프록시, 여러 어드바이저")
void multiAdvisorTest2() {
    //proxy -> advisor2 -> advisor1 -> target
    DefaultPointcutAdvisor advisor2 = new DefaultPointcutAdvisor(Pointcut.TRUE,
            new Advice2());
    DefaultPointcutAdvisor advisor1 = new DefaultPointcutAdvisor(Pointcut.TRUE,
            new Advice1());
    ServiceInterface target = new ServiceImpl();
    ProxyFactory proxyFactory1 = new ProxyFactory(target);
    proxyFactory1.addAdvisor(advisor2);
    proxyFactory1.addAdvisor(advisor1);
    ServiceInterface proxy = (ServiceInterface) proxyFactory1.getProxy();
    //실행
    proxy.save();
}
```
- 프록시 팩토리에 원하는 만큼 `addAdvisor()`를 통해서 어드바이저를 등록하면 된다.
- 등록하는 순서대로 `advisor`가 호출된다. 여기서는 `advisor2`, `advisor1` 순서로 등록했다.

### 실행 결과
```text
MultiAdvisorTest$Advice2 - advice2 호출
MultiAdvisorTest$Advice1 - advice1 호출
ServiceImpl - save 호출
```
- 실행 결과를 보면 `advice2`, `advice1` 순서대로 호출된 것을 알 수 있다.

### 정리
- 결과적으로 여러 프록시를 사용할 때와 비교해서 결과는 같고, 성능은 더 좋다.

### 중요
> 스프링 AOP를 처음 공부하거나 사용하면, AOP 적용 수만큼 프록시가 생성된다고 착각하게 된다.
> 스프링은 AOP를 적용할 떄, 최적화를 진행해서 지금처럼 프록시는 하나만 만들고, 하나의 프록시에 여러
> 어드바이저를 적용한다.
> 정리하면 하나의 `target`에 여러 AOP가 동시에 적용되어도, 스프링 AOP는 `target`마다
> 하나의 프록시만 생성한다.
