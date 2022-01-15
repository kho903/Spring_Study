# 인터페이스 기반 프록시 - 적용
- 인터페이스와 구현체가 있는 V1 App에 지금까지 학습한 프록시를 도입해서 `LogTrace`를 사용해본다.
- 프록시를 사용하면 기존 코드를 전혀 수정하지 않고 로그 추적 기능 도입할 수 있다.

## OrderRepositoryInterfaceProxy
```java
package hello.proxy.config.v1_proxy.interface_proxy;

import hello.proxy.app.v1.OrderRepositoryV1;
import hello.proxy.trace.TraceStatus;
import hello.proxy.trace.logtrace.LogTrace;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class OrderRepositoryInterfaceProxy implements OrderRepositoryV1 {
    private final OrderRepositoryV1 target;
    private final LogTrace logTrace;

    @Override
    public void save(String itemId) {
        TraceStatus status = null;
        try {
            status = logTrace.begin("OrderRepository.request()");
            //target 호출
            target.save(itemId);
            logTrace.end(status);
        } catch (Exception e) {
            logTrace.exception(status, e);
            throw e;
        }
    }
}
```
- 프록시를 만들기 위해 인터페이스를 구현하고 구현한 메서드에 `LogTrace`를 사용하는 로직을 추가한다.
지금까지는 `OrderRepositoryImpl`이 이런 로직을 모두 추가해야했다. 프록시를 사용한 덕분에 이 부분을
프록시가 대신 처리해준다. 따라서 `OrderRepositoryImpl` 코드를 변경하지 않아도 된다.
- `OrderRepositoryV1 target` 프록시가 실제 호출할 원본 리포지토리의 참조를 가지고 있어야 한다.

## OrderServiceInterfaceProxy
```java
package hello.proxy.config.v1_proxy.interface_proxy;

import hello.proxy.app.v1.OrderServiceV1;
import hello.proxy.trace.TraceStatus;
import hello.proxy.trace.logtrace.LogTrace;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class OrderServiceInterfaceProxy implements OrderServiceV1 {
    private final OrderServiceV1 target;
    private final LogTrace logTrace;

    @Override
    public void orderItem(String itemId) {
        TraceStatus status = null;
        try {
            status = logTrace.begin("OrderService.orderItem()");
            //target 호출
            target.orderItem(itemId);
            logTrace.end(status);
        } catch (Exception e) {
            logTrace.exception(status, e);
            throw e;
        }
    }
}
```

## OrderControllerInterfaceProxy
```java
package hello.proxy.config.v1_proxy.interface_proxy;

import hello.proxy.app.v1.OrderControllerV1;
import hello.proxy.trace.TraceStatus;
import hello.proxy.trace.logtrace.LogTrace;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class OrderControllerInterfaceProxy implements OrderControllerV1 {
    private final OrderControllerV1 target;
    private final LogTrace logTrace;

    @Override
    public String request(String itemId) {
        TraceStatus status = null;
        try {
            status = logTrace.begin("OrderController.request()");
            //target 호출
            String result = target.request(itemId);
            logTrace.end(status);
            return result;
        } catch (Exception e) {
            logTrace.exception(status, e);
            throw e;
        }
    }

    @Override
    public String noLog() {
        return target.noLog();
    }
}
```
- `noLog()` 메서드는 로그를 남기지 않아야 한다. 따라서 별도의 로직 없이 단순히 `target`을 호출하면 된다.

## InterfaceProxyConfig
```java
package hello.proxy.config.v1_proxy;

import hello.proxy.app.v1.*;
import
        hello.proxy.config.v1_proxy.interface_proxy.OrderControllerInterfaceProxy;
import
        hello.proxy.config.v1_proxy.interface_proxy.OrderRepositoryInterfaceProxy;
import hello.proxy.config.v1_proxy.interface_proxy.OrderServiceInterfaceProxy;
import hello.proxy.trace.logtrace.LogTrace;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class InterfaceProxyConfig {
    @Bean
    public OrderControllerV1 orderController(LogTrace logTrace) {
        OrderControllerV1Impl controllerImpl = new
                OrderControllerV1Impl(orderService(logTrace));
        return new OrderControllerInterfaceProxy(controllerImpl, logTrace);
    }

    @Bean
    public OrderServiceV1 orderService(LogTrace logTrace) {
        OrderServiceV1Impl serviceImpl = new
                OrderServiceV1Impl(orderRepository(logTrace));
        return new OrderServiceInterfaceProxy(serviceImpl, logTrace);
    }

    @Bean
    public OrderRepositoryV1 orderRepository(LogTrace logTrace) {
        OrderRepositoryV1Impl repositoryImpl = new OrderRepositoryV1Impl();
        return new OrderRepositoryInterfaceProxy(repositoryImpl, logTrace);
    }
}
```
- `LogTrace`가 아직 스프링 빈으로 등록 되어 있지 않다.

## V1 프록시 런타임 객체 의존 관계 설정
- 이제 프록시의 런타임 객체 의존 관계를 설정하면 된다. 기존에는 스프링 빈이 `orderControllerV1Impl`,
`orderServiceV1Impl`같은 실제 객체를 반환헀다. 하지만 이제는 프록시를 사용해야 한다. 따라서
프록시를 생성하고 프록시를 실제 스프링 빈 대신 등록한다. 실제 객체는 스프링 빈으로 등록하지 않는다.
- 프록시는 내부에 실제 객체를 참조하고 있다. 예를 들어서 `OrderServiceInterfaceProxy`는 내부에
실제 대상 객체인 `OrderServiceImpl`을 가지고 있다.
- 정리하면 다음과 같은 의존 관계를 가지고 있다.
    - `proxy -> target`
    - `orderServiceInterfaceProxy -> orderServiceV1Impl`
- 스프링 빈으로 실제 객체 대신에 프록시 객체를 등록했기 때문에 앞으로 스프링 빈을 주입 받으면
실제 객체 대신에 프록시 객체가 주입된다.
- 실제 객체가 스프링 빈으로 등록되지 않는다고 해서 사라지는 것은 아니다. 프록시 객체가 실제 객체를 
참조하기 때문에 프록시를 통해서 실제 객체를 호출할 수 있다. 쉽게 이야기 해서 프록시 객체 안에
실제 객체가 있는 것이다.

## 스프링 컨테이너 - 프록시 적용 전
```
빈이름 - 빈객체
orderController - OrderControllerV1Impl@x01
orderService - OrderServiceV1Impl@x02
orderRepository - OrderRepositoryV1Impl@x03
```
AppV1Config 를 통해 프록시를 적용하기 전
- 실제 객체가 스프링 빈으로 등록된다. 빈 객체의 마지막에 `@x0..`라고 해둔 것은 인스턴스라는 뜻이다.
## 스프링 컨테이너 - 프록시 적용 후
```java
빈이름 - 빈 객체
orderController - OrderControllerInterfaceProxy@x04 -> OrderControllerV1Impl@x01
orderService - OrderServiceInterfaceProxy@x05 -> OrderServiceV1Impl@x02
orderRepository - OrderRepositoryInterfaceProxy@x06 -> OrderRepositoryV1Impl@x03
```
`InterfaceProxyConfig`를 통해 프록시를 적용한 후
- 스프링 컨테이너에 프록시 객체가 등록된다. 스프링 컨테이너는 이제 실제 객체가 아니라 프록시 객체를
스프링 빈으로 관리한다.
- 이제 실제 객체는 스프링 컨테이너와는 상관이 없다. 실제 객체는 프록시 객체를 통해서 참조될 뿐이다.
- 프록시 객체는 스프링 컨테이너가 관리하고 자바 힙 메모리에도 올라간다. 반면에 실제 객체는 자바 힙
메모리에는 올라가지만 스프링 컨테이너가 관리하지는 않는다.

## ProxyApplication  
```java
package hello.proxy;

import hello.proxy.config.AppV1Config;
import hello.proxy.config.AppV2Config;
import hello.proxy.config.v1_proxy.InterfaceProxyConfig;
import hello.proxy.trace.logtrace.LogTrace;
import hello.proxy.trace.logtrace.ThreadLocalLogTrace;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

//@Import({AppV1Config.class, AppV2Config.class})
@Import(InterfaceProxyConfig.class)
@SpringBootApplication(scanBasePackages = "hello.proxy.app")
public class ProxyApplication {
    public static void main(String[] args) {
        SpringApplication.run(ProxyApplication.class, args);
    }

    @Bean
    public LogTrace logTrace() {
        return new ThreadLocalLogTrace();
    }
}
```
- `@Bean` : 먼저 `LogTrace` 스프링 빈 추가를 먼저 해주어야 한다. 이것을 여기에 등록한 이유는
앞으로 사용할 모든 예제에서 함께 사용하기 위해서다.
- `@Import(InterfaceProxyConfig.class)` : 프록시를 적용한 설정 파일을 사용하자.

### 실행 및 실행결과(로그)
- http://localhost:8080/v1/request?itemId=hello
```text
[65b39db2] OrderController.request()
[65b39db2] |-->OrderService.orderItem()
[65b39db2] | |-->OrderRepository.request()
[65b39db2] | |<--OrderRepository.request() time=1002ms
[65b39db2] |<--OrderService.orderItem() time=1002ms
[65b39db2] OrderController.request() time=1003ms
```
- 실행 결과를 확인해보면 로그 추적 기능이 프록시를 통해 잘 동작하는 것을 확인할 수 있다.

## 정리
### 추가된 요구사항
- 원본 코드를 전혀 수정하지 않고, 로그 추적기를 적용해라.
- 특정 메서드는 로그를 출력하지 않는 기능
    - 보안상 일부는 로그를 출력하면 안된다.
- 다음과 같은 다양한 케이스에 적용할 수 있어야 한다.
    - v1 - 인터페이스가 있는 구현 클래스에 적용
    - v2 - 인터페이스가 없는 구체 클래스에 적용
    - v3 - 컴포넌트 스캔 대상에 기능 적용

### 정리 2
- 프록시와 DI 덕분에 원본 코드를 전혀 수정하지 않고, 로그 추적기를 도입할 수 있었다.
- 물론 너무 많은 프록시 클래스를 만들어야 하는 단점이 있기는 함.
