# 템플릿 콜백 패턴 - 적용
## TraceCallback 인터페이스
```java
package hello.advanced.trace.callback;

public interface TraceCallback<T> {
    T call();
}
```
- 콜백을 전달하는 인터페이스이다.
- `<T>` 제네릭을 사용했다. 콜백의 반환 타입을 정의한다.

## TraceTemplate
```java
package hello.advanced.trace.callback;

import hello.advanced.trace.TraceStatus;
import hello.advanced.trace.logtrace.LogTrace;

public class TraceTemplate {
    private final LogTrace trace;

    public TraceTemplate(LogTrace trace) {
        this.trace = trace;
    }

    public <T> T execute(String message, TraceCallback<T> callback) {
        TraceStatus status = null;
        try {
            status = trace.begin(message);
            //로직 호출
            T result = callback.call();
            trace.end(status);
            return result;
        } catch (Exception e) {
            trace.exception(status, e);
            throw e;
        }
    }
}
```
- `TraceTemplate`는 템플릿 역할을 한다.
- `execute(..)`를 보면 `message` 데이터와 콜백인 `TraceCallback callback`을 전달 받는다.
- `<T>` 제네릭을 사용했다. 반환 타입을 정의한다.

## OrderControllerV5
```java
package hello.advanced.app.v5;

import hello.advanced.trace.callback.TraceCallback;
import hello.advanced.trace.callback.TraceTemplate;
import hello.advanced.trace.logtrace.LogTrace;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OrderControllerV5 {
    private final OrderServiceV5 orderService;
    private final TraceTemplate template;

    public OrderControllerV5(OrderServiceV5 orderService, LogTrace trace) {
        this.orderService = orderService;
        this.template = new TraceTemplate(trace);
    }

    @GetMapping("/v5/request")
    public String request(String itemId) {
        return template.execute("OrderController.request()", new
                TraceCallback<>() {
                    @Override
                    public String call() {
                        orderService.orderItem(itemId);
                        return "ok";
                    }
                });
    }
}
```
- `this.template = new TraceTemplate(trace)` : `trace` 의존관계 주입을 받으면서 필요한
`TraceTemplate` 템플릿을 생성한다. 참고로 `TraceTemplate`를 처음부터 스프링 빈으로 등록하고 
주입받아도 된다. 이 부분은 선택이다.
- `template.execute(.., new TraceCallback(){...}` : 템플릿을 실행하면서 콜백을 전달한다.
여기서는 콜백으로 익명 내부 클래스를 사용했다.

## OrderServiceV5
```java
package hello.advanced.app.v5;

import hello.advanced.trace.callback.TraceTemplate;
import hello.advanced.trace.logtrace.LogTrace;
import org.springframework.stereotype.Service;

@Service
public class OrderServiceV5 {
    private final OrderRepositoryV5 orderRepository;
    private final TraceTemplate template;

    public OrderServiceV5(OrderRepositoryV5 orderRepository, LogTrace trace) {
        this.orderRepository = orderRepository;
        this.template = new TraceTemplate(trace);
    }

    public void orderItem(String itemId) {
        template.execute("OrderController.request()", () -> {
            orderRepository.save(itemId);
            return null;
        });
    }
}
```
- `template.execute(.., new TraceCallback() {...}` : 템플릿을 실행하면서 콜백을 전달한다.
여기서는 콜백을 람다로 전달했다.
  
## OrderRepositoryV5
```java
package hello.advanced.app.v5;

import hello.advanced.trace.callback.TraceTemplate;
import hello.advanced.trace.logtrace.LogTrace;
import org.springframework.stereotype.Repository;

@Repository
public class OrderRepositoryV5 {
    private final TraceTemplate template;

    public OrderRepositoryV5(LogTrace trace) {
        this.template = new TraceTemplate(trace);
    }

    public void save(String itemId) {
        template.execute("OrderRepository.save()", () -> {
            //저장 로직
            if (itemId.equals("ex")) {
                throw new IllegalStateException("예외 발생!");
            }
            sleep(1000);
            return null;
        });
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
- http://localhost:8080/v5/request?itemId=hello

### 정상 실행 로그
```text
[aaaaaaaa] OrderController.request()
[aaaaaaaa] |-->OrderService.orderItem()
[aaaaaaaa] | |-->OrderRepository.save()
[aaaaaaaa] | |<--OrderRepository.save() time=1001ms
[aaaaaaaa] |<--OrderService.orderItem() time=1003ms
[aaaaaaaa] OrderController.request() time=1004ms
```
