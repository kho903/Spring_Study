# JDK 동적 프록시 - 적용 2
메서드 이름 필터 기능 추가
- http://localhost:8080/v1/no-log
- 요구사항에 의해 이것을 호출 했을 때는 로그가 남으면 안된다.
- 이런 문제를 해결하기 위해 메서드 이름을 기준으로 특정 조건을 만족할 때만 로그를 남기는 기능을 개발.

## LogTraceFilterHandler
```java
package hello.proxy.config.v2_dynamicproxy.handler;

import hello.proxy.trace.TraceStatus;
import hello.proxy.trace.logtrace.LogTrace;
import org.springframework.util.PatternMatchUtils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class LogTraceFilterHandler implements InvocationHandler {

    private final Object target;
    private final LogTrace logTrace;
    private final String[] patterns;

    public LogTraceFilterHandler(Object target, LogTrace logTrace, String...
            patterns) {
        this.target = target;
        this.logTrace = logTrace;
        this.patterns = patterns;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws
            Throwable {
        //메서드 이름 필터
        String methodName = method.getName();
        if (!PatternMatchUtils.simpleMatch(patterns, methodName)) {
            return method.invoke(target, args);
        }
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
- `LogTraceFilterHandler`는 기존 기능에 다음 기능이 추가되었다.
    - 특정 메서드 이름이 매칭되는 경우에만 `LogTrace` 로직을 실행한다. 이름이 매칭되지
    않으면 실제 로직을 바로 호출한다.
- 스프링이 제공하는 `PatternMatchUtils.simpleMatch(..)`를 사용하면 단순한 매칭 로직을
쉽게 적용할 수 있다.
    - `xxx` : xxx가 정확히 매칭되면 참
    - `xxx*` : xxx로 시작하면 참
    - `*xxx` : xxx로 끝나면 참
    - `*xxx*` : xxx가 있으면 참
- `String[] patterns` : 적용할 패턴은 생성자를 통해서 외부에서 받는다.

## DynamicProxyFilterConfig
```java
package hello.proxy.config.v2_dynamicproxy;

import hello.proxy.app.v1.*;
import hello.proxy.config.v2_dynamicproxy.handler.LogTraceFilterHandler;
import hello.proxy.trace.logtrace.LogTrace;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.lang.reflect.Proxy;

@Configuration
public class DynamicProxyFilterConfig {
  public static final String[] PATTERNS = {"request*", "order*", "save*"};

  @Bean
  public OrderControllerV1 orderControllerV1(LogTrace logTrace) {
    OrderControllerV1 orderController = new
            OrderControllerV1Impl(orderServiceV1(logTrace));
    OrderControllerV1 proxy = (OrderControllerV1)
            Proxy.newProxyInstance(DynamicProxyFilterConfig.class.getClassLoader(),
                    new Class[]{OrderControllerV1.class},
                    new LogTraceFilterHandler(orderController, logTrace, PATTERNS)
            );
    return proxy;
  }

  @Bean
  public OrderServiceV1 orderServiceV1(LogTrace logTrace) {
    OrderServiceV1 orderService = new
            OrderServiceV1Impl(orderRepositoryV1(logTrace));
    OrderServiceV1 proxy = (OrderServiceV1)
            Proxy.newProxyInstance(DynamicProxyFilterConfig.class.getClassLoader(),
                    new Class[]{OrderServiceV1.class},
                    new LogTraceFilterHandler(orderService, logTrace, PATTERNS)
            );
    return proxy;
  }

  @Bean
  public OrderRepositoryV1 orderRepositoryV1(LogTrace logTrace) {
    OrderRepositoryV1 orderRepository = new OrderRepositoryV1Impl();
    OrderRepositoryV1 proxy = (OrderRepositoryV1)
            Proxy.newProxyInstance(DynamicProxyFilterConfig.class.getClassLoader(),
                    new Class[]{OrderRepositoryV1.class},
                    new LogTraceFilterHandler(orderRepository, logTrace, PATTERNS)
            );
    return proxy;
  }
}
```
- `public static final String[] PATTERNS = {"request*", "order*", "save*"};`
    - 적용할 패턴이다. `request`, `order`, `save`로 시작하는 메서드에 로그가 남는다.
- `LogTraceFilterHandler` : 앞서 만든 필터 기능이 있는 핸들러를 사용한다. 그리고
핸들러에 적용 패턴도 넣어준다.
  
## ProxyApplication - 추가
```java
import hello.proxy.config.v2_dynamicproxy.DynamicProxyFilterConfig;

@Import(DynamicProxyFilterConfig.class)
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
- `@Import(DynamicProxyFilterConfig.class)`으로 방금 만든 설정 추가

## 실행
- http://localhost:8080/v1/request?itemId=hello
- http://localhost:8080/v1/no-log
- 실행해보면 no-log가 사용하는 noLog() 메서드에는 로그가 남지 않는 것을 확인할 수 있다.

## JDK 동적 프록시 - 한계
- JDK 동적 프록시는 인터페이스가 필수이다.
- 그렇다면 V2 애플리케이션 처럼 인터페이스 없이 클래스만 있는 경우에는 어떻게 동적 프록시를
적용할 수 있을까?
- 이것은 일반적인 방법으로는 어렵고 `CGLIB`라는 바이트코드를 조작하는 특별한 라이브러리를 사용해야 한다.
