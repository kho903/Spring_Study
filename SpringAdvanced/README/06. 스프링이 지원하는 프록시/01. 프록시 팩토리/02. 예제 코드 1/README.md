# 프록시 팩토리 - 예제 코드 1
## Advice 만들기
- `Advice`는 프록시에 적용하는 부가 기능 로직이다. 이것은 JDK 동적 프록시가 제공하는 
`InvocationHandler`와 CGLIB가 제공하는 `MethodInterceptor`의 개념과 유사하다.
둘을 개념적으로 추상화한 것이다. 
- 프록시 팩토리를 사용하면 둘 대신에 `Advice`를 사용하면 된다.
- `Advice`를 만드는 방법은 여러가지가 있지만, 기본적인 방법은 다음 인터페이스를 구현하면 된다.

## MethodInterceptor - 스프링이 제공하는 코드
```java
package org.aopalliance.intercept;

public interface MethodInterceptor extends Interceptor {
    Object invoke(MethodInvocation invocation) throws Throwable;
}
```
- `MethodInvocation invocation`
    - 내부에는 다음 메서드를 호출하는 방법, 현재 프록시 객체 인스턴스, `args`, 메서드 정보 등이
    포함되어 있다. 기존에 파라미터로 제공되는 부분들이 이 안으로 모두 들어갔다고 생각하면 된다.
- CGLIB의 `MethodInterceptor`와 이름이 같으므로 패키지 이름에 주의!
    - 여기서 사용하는 `org.aopalliance.intercept` 패키지는 스프링 AOP 모듈
      (`spring-top`) 안에 들어있다.
- `MethodInterceptor`는 `Interceptor`를 상속하고 `Interceptor`는 `Advice`
인터페이스를 상속한다.

## TimeAdvice
```java
package hello.proxy.common.advice;

import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

@Slf4j
public class TimeAdvice implements MethodInterceptor {
    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        log.info("TimeProxy 실행");
        long startTime = System.currentTimeMillis();
        Object result = invocation.proceed();
        long endTime = System.currentTimeMillis();
        long resultTime = endTime - startTime;
        log.info("TimeProxy 종료 resultTime={}ms", resultTime);
        return result;
    }
}
```
- `TimeAdvice`는 `MethodInterceptor` 인터페이스를 구현한다.
- `Object result = invocation.proceed()`
    - `invocation.proceed()`를 호출하면 `target` 클래스를 호출하고 그 결과를 받는다.
    - 그런데 기존에 보았던 코드들과 다르게 `target` 클래스의 정보가 보이지 않는다.
    `target` 클래스의 정보는 `MethodInvocation invocation` 안에 모두 포함되어 있다.
    - 그 이유는 프록시 팩토리로 프록시를 생성하는 단계에서 이미 `target` 정보를
    파라미터로 전달받기 때문이다.

## ProxyfactoryTest
```java
package hello.proxy.proxyfactory;

import hello.proxy.common.advice.TimeAdvice;
import hello.proxy.common.service.ConcreteService;
import hello.proxy.common.service.ServiceImpl;
import hello.proxy.common.service.ServiceInterface;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.support.AopUtils;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
public class ProxyFactoryTest {
    @Test
    @DisplayName("인터페이스가 있으면 JDK 동적 프록시 사용")
    void interfaceProxy() {
        ServiceInterface target = new ServiceImpl();
        ProxyFactory proxyFactory = new ProxyFactory(target);
        proxyFactory.addAdvice(new TimeAdvice());
        ServiceInterface proxy = (ServiceInterface) proxyFactory.getProxy();
        log.info("targetClass={}", target.getClass());
        log.info("proxyClass={}", proxy.getClass());
        proxy.save();
        assertThat(AopUtils.isAopProxy(proxy)).isTrue();
        assertThat(AopUtils.isJdkDynamicProxy(proxy)).isTrue();
        assertThat(AopUtils.isCglibProxy(proxy)).isFalse();
    }
}
```
- `new ProxyFactory(target)` : 프록시 팩토리를 생성할 때, 생성자에 프록시의 호출
대상을 함께 넘겨준다. 프록시 팩토리는 이 인스턴스 정보를 기반으로 프록시를 만들어낸다. 만약
이 인스턴스에 인터페이스가 있다면 JDK 동적 프록시를 기본으로 사용하고 인터페이스가 없고 구체
클래스만 있다면 CGLIB를 통해서 동적 프록시를 생성한다. 여기서는 `target`이
`new ServiceImpl()`의 인스턴스이기 때문에 `ServiceInterface` 인터페이스가
있다. 따라서 이 인터페이스를 기반으로 JDK 동적 프록시를 생성한다.
- `proxyFactory.addAdvice(new TimeAdvice())` : 프록시 팩토리를 통해서 만든 프록시가
사용할 부가 기능 로직을 설정한다. JDK 동적 프록시가 제공하는 `InvocationHandler`와
CGLIB가 제공하는 `MethodInterceptor`의 개념과 유사하다. 이렇게 프록시가 제공하는
부가 기능 로직을 어드바이스(`Advice`)라 한다. 번역하면 조언을 해준다고 생각하면 된다.
- `proxyFactory.getProxy()` : 프록시 객체를 생성하고 그 결과를 받는다.

## 실행 결과
```text
ProxyFactoryTest - targetClass=class hello.proxy.common.service.ServiceImpl
ProxyFactoryTest - proxyClass=class com.sun.proxy.$Proxy13
TimeAdvice - TimeProxy 실행
ServiceImpl - save 호출
TimeAdvice - TimeProxy 종료 resultTime=1ms
```
- 실행 결과를 보면 프록시가 정상 적용된 것을 확인할 수 있다.
- `proxyClass=class com.sun.proxy.$Proxy13` 코드를 통해 JDK 동적 프록시가
적용된 것도 확인할 수 있다.

## 프록시 팩토리를 통한 프록시 적용 확인
프록시 팩토리로 프록시가 잘 적용되었는 지 확인하려면 다음 기능을 사용하면 된다.
- `AopUtils.isAopProxy(proxy)` : 프록시 팩토리를 통해서 프록시가 생성되면
JDK 동적 프록시나 CGLIB 모두 참이다.
- `AopUtils.isJdkDynamicProxy(proxy)` : 프록시 팩토리를 통해서 프록시가 생성되고,
JDK 동적 프록시인 경우 참
- `AopUtils.isCglibProxy(proxy)` : 프록시 팩토리를 통해서 프록시가 생성되고,
CGLIB 동적 프록시인 경우 참
