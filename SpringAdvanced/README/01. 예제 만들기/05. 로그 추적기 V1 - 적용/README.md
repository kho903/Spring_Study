# 로그 추적기 V1 - 적용
- 애플리케이션에 우리가 개발한 로그 추적기 적용
- 기존 v0 패키지 -> v1 패키지로 새로 만들고 기존 코드 복사 후 리펙토링

## v1 적용하기
- `OrderControllerV1`, `OrderServiceV1`, `OrderRepository`에 로그 추적기를 적용해본다.
- 먼저 컨트롤러
### OrderControllerV1
```java
package hello.advanced.app.v1;

import hello.advanced.trace.TraceStatus;
import hello.advanced.trace.hellotrace.HelloTraceV1;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class OrderControllerV1 {
    private final OrderServiceV1 orderService;
    private final HelloTraceV1 trace;

    @GetMapping("/v1/request")
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
- `HelloTraceV1 trace` : `HelloTraceV1`을 주입 받는다. 참고로 `HelloTraceV1`은 `@Component`
애노테이션을 가지고 있기 때문에 컴포넌트 스캔의 대상이 된다. 따라서 자동으로 스프링 빈으로 등록된다.
- `trace.begin("OrderController.request()")`: 로그를 시작할 때 메시지 이름으로 컨트롤러 이름 + 메서드
이름을 주었다. 이렇게 하면 어떤 컨트롤러와 메서드가 호출되었는지 로그로 편리하게 확인할 수 있다. (수작업)
- 단순하게 `trace.begin()`, `trace.end()` 코드 두 줄만 적용하면 될 줄 알았지만, 예외처리로 인해
지저분한 try, catch문이 추가된다.
- `begin()`의 결과 값으로 받은 `TraceStatus status` 값을 `end()`, `exception()`에 넘겨야 한다. 결국 
`try`, `catch` 블록 모두에 이 값을 넘겨야 한다. 따라서 `try` 상위에 `TraceStatus status` 코드를
선언해야 한다. 만약 `try` 안에서 `TraceStatus status`를 선언하면 `try` 블록 안에서만 해당 변수가 유효하기
때문에 `catch` 블록에 넘길 수 없다. 따라서 컴파일 오류 발생
- `throw e` 예외를 꼭 다시 던저주어야 한다. 그렇지 않으면 여기서 예외를 먹어버리고, 이후에 정상 흐름으로 동작한다.
로그는 애플리케이션의 흐름에 영향을 주면 안된다. 로그 때문에 예외가 사라지면 안된다.

### 실행
- 정상 : http://localhost:8080/v1/request?itemId=hello
- 예외 : http://localhost:8080/v1/request?itemId=ex
- 실행해보면 정상 흐름과 예외 모두 로그로 잘 출력되는 것을 확인할 수 있다.

### OrderServiceV1
```java
package hello.advanced.app.v1;

import hello.advanced.trace.TraceStatus;
import hello.advanced.trace.hellotrace.HelloTraceV1;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderServiceV1 {
    private final OrderRepositoryV1 orderRepository;
    private final HelloTraceV1 trace;

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
### OrderRepositoryV1
```java
package hello.advanced.app.v1;

import hello.advanced.trace.TraceStatus;
import hello.advanced.trace.hellotrace.HelloTraceV1;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class OrderRepositoryV1 {
    private final HelloTraceV1 trace;

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
- 정상 실행 : http://localhost:8080/v1/request?itemId=hello
- 정상 실행 로그
```text
[11111111] OrderController.request()
[22222222] OrderService.orderItem()
[33333333] OrderRepository.save()
[33333333] OrderRepository.save() time=1000ms
[22222222] OrderService.orderItem() time=1001ms
[11111111] OrderController.request() time=1001ms
```
> 참고: 아직 level 관련 기능 X, level은 항상 0, 그리고 트랜잭션 ID값도 아직 개발 X
- 예외 실행 : http://localhost:8080/v1/request?itemId=ex
- 예외 실행 로그
```text
[5e110a14] OrderController.request()
[6bc1dcd2] OrderService.orderItem()
[48ddffd6] OrderRepository.save()
[48ddffd6] OrderRepository.save() time=0ms ex=java.lang.IllegalStateException: 예외 발생!
[6bc1dcd2] OrderService.orderItem() time=6ms
ex=java.lang.IllegalStateException: 예외 발생!
[5e110a14] OrderController.request() time=7ms
ex=java.lang.IllegalStateException: 예외 발생!
```
- HelloTraceV1 덕분에 직접 로그를 하나하나 남기는 것 보다는 편하게 여러가지 로그를 남길 수 있었다.
- 하지만 로그를 남기기 위한 코드가 너무 복잡하다.

## 남은 문제
### 현재 요구사항
- 모든 PUBLIC 메서드의 호출과 응답 정보를 로그로 출력 O
- 애플리케이션의 흐름을 변경하면 안됨 O 
    - 로그를 남긴다고 해서 비즈니스 로직의 동작에 영향을 주면 안됨 O
- 메서드 호출에 걸린 시간 O
- 정상 흐름과 예외 흐름 구분 O
    - 예외 발생시 예외 정보가 남아야 함 O
- 메서드 호출의 깊이 표현 X
- HTTP 요청을 구분 X
    - HTTP 요청 단위로 특정 ID를 남겨서 어떤 HTTP 요청에서 시작된 것인지 명확하게 구분이 가능해야 함
    - 트랜잭션 ID (DB 트랜잭션 X)
> 아직 구현하지 못한 요구사항은 메서드 호출의 깊이를 표현하고 같은 HTTP 요청이면 같은
> 트랜잭션 ID를 남기는 것이다. <br>
> 이 기능은 직전 로그의 깊이와 트랜잭션 ID가 무엇인지 알아야 할 수 있는 일이다. <br>
> 예를 들어서 `OrderController.request()`에서 로그를 남길 때 어떤 깊이와 어떤 트랜잭션 ID를
> 사용했는지를 그 다음에 로그를 남기는 `OrderService.orderItem()`에서 로그를 남길때 알아야 한다.<br>
> 결국 현재 로그의 상태 정보인 `트랜잭션 ID`와 `level`이 다음으로 전달되어야 한다. <br>
> 정리하면 로그에 대한 문맥(`Context`) 정보가 필요하다.
