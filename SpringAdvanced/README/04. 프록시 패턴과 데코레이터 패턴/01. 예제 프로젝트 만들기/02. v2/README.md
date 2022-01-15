# 예제 프로젝트 만들기 v2
## v2 - 인터페이스 없는 구체 클래스 - 스프링 빈으로 수동 등록
- 이번에는 인터페이스가 없는 `Controller`, `Service`, `Repository`를 스프링 빈으로
수동 등록

## OrderRepositoryV2
```java
package hello.proxy.app.v2;

public class OrderRepositoryV2 {
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
## OrderServiceV2
```java
package hello.proxy.app.v2;

public class OrderServiceV2 {
    private final OrderRepositoryV2 orderRepository;

    public OrderServiceV2(OrderRepositoryV2 orderRepository) {
        this.orderRepository = orderRepository;
    }

    public void orderItem(String itemId) {
        orderRepository.save(itemId);
    }
}
```
## OrderControllerV2
```java
package hello.proxy.app.v2;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Slf4j
@RequestMapping
@ResponseBody
public class OrderControllerV2 {
    private final OrderServiceV2 orderService;

    public OrderControllerV2(OrderServiceV2 orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/v2/request")
    public String request(String itemId) {
        orderService.orderItem(itemId);
        return "ok";
    }

    @GetMapping("/v2/no-log")
    public String noLog() {
        return "ok";
    }
}
```
- `@RequestMapping` : 스프링MVC는 타입에 `@Controller` 또는 `@RequestMapping`
애노테이션이 있어야 스프링 컨트롤러로 인식한다. 그리고 스프링 컨트롤러로 인식해야, HTTP URL이
매핑되고 동작한다. 그런데 여기서는 `@Controller`를 사용하지 않고, `@RequestMapping`
애노테이션을 사용했다. 그 이유는 `@Controller`를 사용하면 자동 컴포넌트 스캔의 대상이 되기 때문이다.
여기서는 컴포넌트 스캔을 통한 자동 빈 등록이 아니라 수동 빈 등록을 하는 것이 목표다. 따라서
컴포넌트 스캔과 관계 없는 `@RequestMapping`를 타입에 사용했다.

## AppV2Config
```java
package hello.proxy.config;

import hello.proxy.app.v2.OrderControllerV2;
import hello.proxy.app.v2.OrderRepositoryV2;
import hello.proxy.app.v2.OrderServiceV2;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppV2Config {
    @Bean
    public OrderControllerV2 orderControllerV2() {
        return new OrderControllerV2(orderServiceV2());
    }

    @Bean
    public OrderServiceV2 orderServiceV2() {
        return new OrderServiceV2(orderRepositoryV2());
    }

    @Bean
    public OrderRepositoryV2 orderRepositoryV2() {
        return new OrderRepositoryV2();
    }
}
```
- 수동 빈 등록을 위한 설정

## ProxyApplication
```java
package hello.proxy;

import hello.proxy.config.AppV1Config;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@Import({AppV1Config.class, AppV2Config.class})
@SpringBootApplication(scanBasePackages = "hello.proxy.app")
public class ProxyApplication {
    public static void main(String[] args) {
        SpringApplication.run(ProxyApplication.class, args);
    }
}
```

### 변경 사항
- 기존 : `@Import(AppV1Config.class)`
- 변경 : `@Import({AppV1Config.class, AppConfig.class})`
- `@Import` 안에 배열로 등록하고 싶은 설정 파일을 다양하게 추가할 수 있다.

## 실행 및 결과
- http://localhost:8080/v1/request?itemId=hello
- 웹 브라우저 화면: `ok`
