# 로그 추적기 V2 - 적용
- 로그 추적기를 애플리케이션에 적용

## V2 적용하기
- 메서드 호출의 깊이를 표현하고, HTTP 요청도 구분해보자.
- 이렇게 하려면 처음 로그를 남기는 `OrderController.request()`에서 로그를 남길 때
어떤 깊이와 어떤 트랜잭션 ID를 사용했는지 다음 차례인 `OrderService.orderItem()`에서
로그를 남기는 시점에 알아야 한다.
- 결국 현재 로그의 상태 정보인 `트랜잭션ID`와 `level`이 다음으로 전달되어야 한다.
- 이 정보는 `TraceStatus.traceId`에 담겨있다. 따라서 `traceId`를 컨트롤러에서 
서비스를 호출할 때 넘겨주면 된다.
- `traceId`를 넘기도록 V2 전체 코드를 수정하자.

## OrderControllerV2
```java
package hello.advanced.app.v2;

import hello.advanced.trace.TraceStatus;
import hello.advanced.trace.hellotrace.HelloTraceV2;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class OrderControllerV2 {
    private final OrderServiceV2 orderService;
    private final HelloTraceV2 trace;

    @GetMapping("/v2/request")
    public String request(String itemId) {
        TraceStatus status = null;
        try {
            status = trace.begin("OrderController.request()");
            orderService.orderItem(status.getTraceId(), itemId);
            trace.end(status);
            return "ok";
        } catch (Exception e) {
            trace.exception(status, e);
            throw e;
        }
    }
}
```
- `TraceStatus status = trace.begin()` 에서 반환 받은 `TraceStatus`에는
`트랜잭션ID`와 `level` 정보가 있는 `traceId`가 있다.
- `orderService.orderItem()`을 호출할 때 `traceId`를 파라미터로 전달한다.
- `traceId`를 파라미터로 전달하기 위해 `OrderServiceV2.orderItem()`의 파라미터에
`traceId`를 추가해야 한다.

## OrderServiceV2
```java
package hello.advanced.app.v2;

import hello.advanced.trace.TraceId;
import hello.advanced.trace.TraceStatus;
import hello.advanced.trace.hellotrace.HelloTraceV2;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderServiceV2 {
    private final OrderRepositoryV2 orderRepository;
    private final HelloTraceV2 trace;

    public void orderItem(TraceId traceId, String itemId) {
        TraceStatus status = null;
        try {
            status = trace.beginSync(traceId, "OrderService.orderItem()");
            orderRepository.save(status.getTraceId(), itemId);
            trace.end(status);
        } catch (Exception e) {
            trace.exception(status, e);
            throw e;
        }
    }
}
```
- `orderItem()`은 파라미터로 전달 받은 `traceId`를 사용해서 `trace.beginSync()`를
  실행한다.
- `beginSync()`는 내부에서 다음 `traceId`를 생성하면서 트랜잭션ID는 유지하고,
  `level`은 하나 증가시킨다.
- `beginSync()`가 반환한 새로운 `traceStatus`를 `orderRepository.save()`를
호출하면서 파라미터로 전달한다.
- `traceId`를 파라미터로 전달하기 위해 `orderRepository.save()`의 파라미터에
  `traceId`를 추가해야 한다.

## OrderRepositoryV2
```java
package hello.advanced.app.v2;

import hello.advanced.trace.TraceId;
import hello.advanced.trace.TraceStatus;
import hello.advanced.trace.hellotrace.HelloTraceV2;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class OrderRepositoryV2 {
    private final HelloTraceV2 trace;

    public void save(TraceId traceId, String itemId) {
        TraceStatus status = null;
        try {
            status = trace.beginSync(traceId, "OrderRepository.save()");
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
- `save()`는 파라미터로 전달 받은 `traceId()`를 사용해서 `trace.beginSync()`를 
  실행한다.
- `beginSync()`는 내부에서 다음 `traceId`를 생성하면서 트랜잭션ID는 유지하고,
  `level`은 하나 증가시킨다.
- `beginSync()`는 이렇게 갱신된 `traceId`로 새로운 `traceStatus`를 반환한다.
- `trace.end(status)`를 호출하면서 반환된 `traceStatus`를 전달한다.

### 정상 실행
- http://localhost:8080/v2/request?itemId=hello
### 정상 실행 로그
```text
[c80f5dbb] OrderController.request()
[c80f5dbb] |-->OrderService.orderItem()
[c80f5dbb] | |-->OrderRepository.save()
[c80f5dbb] | |<--OrderRepository.save() time=1005ms
[c80f5dbb] |<--OrderService.orderItem() time=1014ms
[c80f5dbb] OrderController.request() time=1017ms
```

### 예외 실행
- http://localhost:8080/v2/request?itemId=ex
### 예외 실행 로그
```text
[ca867d59] OrderController.request()
[ca867d59] |-->OrderService.orderItem()
[ca867d59] | |-->OrderRepository.save()
[ca867d59] | |<X-OrderRepository.save() time=0ms ex=java.lang.IllegalStateException: 예외 발생!
[ca867d59] |<X-OrderService.orderItem() time=7ms ex=java.lang.IllegalStateException: 예외 발생!
[ca867d59] OrderController.request() time=7ms ex=java.lang.IllegalStateException: 예외 발생!
```
- 실행 로그를 보면 같은 HTTP 요청에 대해서 `트랜잭션ID`가 유지되고, `level`도 잘 표현되는 것을 확인할 수 있다.
