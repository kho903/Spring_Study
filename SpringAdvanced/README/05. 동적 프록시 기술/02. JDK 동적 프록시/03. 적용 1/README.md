# JDK 동적 프록시 - 적용 1
- JDK 동적 프록시는 인터페이스가 필수이기 때문에 V1 애플리케이션에만 적용할 수 있다.
- 먼저 `LogTrace`를 적용할 수 있는 `InvocationHandler`를 만든다.

## LogTraceBasicHandler
```java
package hello.proxy.config.v2_dynamicproxy.handler;

import hello.proxy.trace.TraceStatus;
import hello.proxy.trace.logtrace.LogTrace;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class LogTraceBasicHandler implements InvocationHandler {

    private final Object target;
    private final LogTrace logTrace;

    public LogTraceBasicHandler(Object target, LogTrace logTrace) {
        this.target = target;
        this.logTrace = logTrace;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws
            Throwable {
        TraceStatus status = null;
        try {
            String message = method.getDeclaringClass().getSimpleName() + "."
                    + method.getName() + "()";
            status = logTrace.begin(message);
            //로직 호출
            Object result = method.invoke(target, args);
            logTrace.end(status);
            return result;
        } catch (Exception e) {
            logTrace.exception(status, e);
            throw e;
        }
    }
}
```
- `LogTraceBasicHandler`는 `InvocationHandler` 인터페이스를 구현해서 JDK 동적 프록시에서
사용된다.
- `private final Object target` : 프록시가 호출할 대상이다.
- `String message = method.getDeclaringClass().getSimpleName() + "." ...`
    - `LogTrace`에 사용할 메시지이다. 프록시를 직접 개발할 때는 `"OrderController.request()"`와
    같이 프록시마다 호출되는 클래스와 메서드 이름을 직접 남겼다. 이제는 `Method`를 통해서 호출되는
    메서드 정보와 클래스 정보를 동적으로 확인할 수 있기 때문에 이 정보를 사용하면 된다.
      
## DynamicProxyBasicConfig
- 동적 프록시를 사용하도록 수동 빈 등록을 설정
```java
package hello.proxy.config.v2_dynamicproxy;

import hello.proxy.app.v1.*;
import hello.proxy.config.v2_dynamicproxy.handler.LogTraceBasicHandler;
import hello.proxy.trace.logtrace.LogTrace;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.lang.reflect.Proxy;

@Configuration
public class DynamicProxyBasicConfig {
    @Bean
    public OrderControllerV1 orderControllerV1(LogTrace logTrace) {
        OrderControllerV1 orderController = new
                OrderControllerV1Impl(orderServiceV1(logTrace));
        OrderControllerV1 proxy = (OrderControllerV1)
                Proxy.newProxyInstance(OrderControllerV1.class.getClassLoader(),
                        new Class[]{OrderControllerV1.class},
                        new LogTraceBasicHandler(orderController, logTrace)
                );
        return proxy;
    }

    @Bean
    public OrderServiceV1 orderServiceV1(LogTrace logTrace) {
        OrderServiceV1 orderService = new
                OrderServiceV1Impl(orderRepositoryV1(logTrace));
        OrderServiceV1 proxy = (OrderServiceV1)
                Proxy.newProxyInstance(OrderServiceV1.class.getClassLoader(),
                        new Class[]{OrderServiceV1.class},
                        new LogTraceBasicHandler(orderService, logTrace)
                );
        return proxy;
    }

    @Bean
    public OrderRepositoryV1 orderRepositoryV1(LogTrace logTrace) {
        OrderRepositoryV1 orderRepository = new OrderRepositoryV1Impl();
        OrderRepositoryV1 proxy = (OrderRepositoryV1)
                Proxy.newProxyInstance(OrderRepositoryV1.class.getClassLoader(),
                        new Class[]{OrderRepositoryV1.class},
                        new LogTraceBasicHandler(orderRepository, logTrace)
                );
        return proxy;
    }
}
```
- 이전에는 프록시 클래스를 직접 개발했지만, 이제는 JDK 동적 프록시 기술을 사용해서 각각의
`Controller`, `Service`, `Repository`에 맞는 동적 프록시를 생성해주면 된다.
- `LogTraceBasicHandler` : 동적 프록시를 만들더라도 `LogTrace`를 출력하는 로직은 모두 같기 때문에
프록시는 모두 `LogTraceBasicHandler`를 사용한다.

## ProxyApplication - 수정
```java
import hello.proxy.config.v2_dynamicproxy.DynamicProxyBasicConfig;

//@Import({AppV1Config.class, AppV2Config.class})
//@Import(InterfaceProxyConfig.class)
//@Import(ConcreteProxyConfig.class)
@Import(DynamicProxyBasicConfig.class)
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
- `@Import(DynamicProxyBasicConfig.class)` : 이제 동적 프록시 설정을 `@Import`하고 실행.
- 정상 수행됨

## 남은 문제
- http://localhost:8080/v1/no-log
- no-log를 실행해도 동적 프록시가 적용되고, `LogTraceBasicHandler`가 실행되기 때문에 로그가 남는다.
이 부분을 로그가 남지 않도록 처리해야 한다.
