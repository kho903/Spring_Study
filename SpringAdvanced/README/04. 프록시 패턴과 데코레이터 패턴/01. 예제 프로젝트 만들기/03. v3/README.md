# 예제 프로젝트 만들기 v3
## v3 - 컴포넌트 스캔으로 스프링 빈 자동 등록
- 이번에는 컴포넌트 스캔으로 스프링 빈 자동 등록

## OrderRepositoryV3
```java
package hello.proxy.app.v3;

import org.springframework.stereotype.Repository;

@Repository
public class OrderRepositoryV3 {
    public void save(String itemId) {
        //저장 로직
        if (itemId.equals("ex")) {
            throw new IllegalStateException("예외 발생!");
        }
        sleep(1000);
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
## OrderServiceV3
```java
package hello.proxy.app.v3;

import org.springframework.stereotype.Service;

@Service
public class OrderServiceV3 {
    private final OrderRepositoryV3 orderRepository;

    public OrderServiceV3(OrderRepositoryV3 orderRepository) {
        this.orderRepository = orderRepository;
    }

    public void orderItem(String itemId) {
        orderRepository.save(itemId);
    }
}
```
## OrderControllerV3
```java
package hello.proxy.app.v3;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class OrderControllerV3 {
    private final OrderServiceV3 orderService;

    public OrderControllerV3(OrderServiceV3 orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/v3/request")
    public String request(String itemId) {
        orderService.orderItem(itemId);
        return "ok";
    }
}
```
- `ProxyApplication`에서 `@SpringBootApplication(scanBasePackages = "hello.proxy.app")`를
사용했고, 각각 `@RetController`, `@Service`, `@Repository` 애노테이션을 가지고
있기 떄문에 컴포넌트 스캔의 대상이 된다.

## 실행 및 결과
- http://localhost:8080/v1/request?itemId=hello
- 웹 브라우저 화면: `ok`
