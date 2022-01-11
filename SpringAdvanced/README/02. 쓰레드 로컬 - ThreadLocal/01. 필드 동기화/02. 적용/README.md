# 필드 동기화 - 적용
- 지금까지 만든 `FieldLogTrace`를 애플리케이션에 적용해본다.
## LogTrace 스프링 빈 등록
- `FieldLogTrace`를 수동으로 스프링 빈으로 등록하자. 수동으로 등록하면 향후 구현체를 편리하게
변경할 수 있다는 장점이 있다.

## LogTraceConfig
```java
package hello.advanced;

import hello.advanced.trace.logtrace.FieldLogTrace;
import hello.advanced.trace.logtrace.LogTrace;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LogTraceConfig {
    @Bean
    public LogTrace logTrace() {
        return new FieldLogTrace();
    }
}
```

## OrderControllerV3
```java
package hello.advanced.app.v3;

import hello.advanced.trace.TraceStatus;
import hello.advanced.trace.logtrace.LogTrace;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class OrderControllerV3 {
    private final OrderServiceV3 orderService;
    private final LogTrace trace;

    @GetMapping("/v3/request")
    public String request(String itemId) {
        TraceStatus status = null;
        try {
            status = trace.begin("OrderController.request()");
            orderService.orderItem(itemId);
            trace.end(status);
            return "ok";
        } catch (Exception e) {
            trace.exception(status, e);
            throw e; //예외를 꼭 다시 던져주어야 한다.
        }
    }
}
```
## OrderServiceV3
```java
package hello.advanced.app.v3;

import hello.advanced.trace.TraceId;
import hello.advanced.trace.TraceStatus;
import hello.advanced.trace.logtrace.LogTrace;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderServiceV3 {
    private final OrderRepositoryV3 orderRepository;
    private final LogTrace trace;

    public void orderItem(String itemId) {
        TraceStatus status = null;
        try {
            status = trace.begin("OrderService.orderItem()");
            orderRepository.save(itemId);
            trace.end(status);
        } catch (Exception e) {
            trace.exception(status, e);
            throw e;
        }
    }
}
```
## OrderRepositoryV3
```java
package hello.advanced.app.v3;

import hello.advanced.trace.TraceId;
import hello.advanced.trace.TraceStatus;
import hello.advanced.trace.logtrace.LogTrace;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class OrderRepositoryV3 {
    private final LogTrace trace;

    public void save(String itemId) {
        TraceStatus status = null;
        try {
            status = trace.begin("OrderRepository.save()");
            //저장 로직
            if (itemId.equals("ex")) {
                throw new IllegalStateException("예외 발생!");
            }
            sleep(1000);
            trace.end(status);
        } catch (Exception e) {
            trace.exception(status, e);
            throw e;
        }
    }

    private void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
```
### 정상 실행
- http://localhost:8080/v3/request?itemId=hello
```text
[f8477cfc] OrderController.request()
[f8477cfc] |-->OrderService.orderItem()
[f8477cfc] | |-->OrderRepository.save()
[f8477cfc] | |<--OrderRepository.save() time=1004ms
[f8477cfc] |<--OrderService.orderItem() time=1006ms
[f8477cfc] OrderController.request() time=1007ms
```
### 예외실행
- http://localhost:8080/v3/request?itemId=ex
```text
[c426fcfc] OrderController.request()
[c426fcfc] |-->OrderService.orderItem()
[c426fcfc] | |-->OrderRepository.save()
[c426fcfc] | |<X-OrderRepository.save() time=0ms ex=java.lang.IllegalStateException: 예외 발생!
[c426fcfc] |<X-OrderService.orderItem() time=7ms ex=java.lang.IllegalStateException: 예외 발생!
[c426fcfc] OrderController.request() time=7ms ex=java.lang.IllegalStateException: 예외 발생!
```
- traceIdHolder 필드를 사용한 덕분에 파라미터 추가 없는 깔끔한 로그 추적기를 완성했다.
- 실제 서비스에 배포한다면..?