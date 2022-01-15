# 예제 프로젝트 만들기 v1
- 다양한 상황에서 프록시 사용법을 이해하기 위해 다음과 같은 기준으로 기본 예제 프로젝트 생성
## 3가지 상황
- v1 : 인터페이스와 구현 클래스 - 스프링 빈으로 수동 등록
- v2 : 인터페이스 없는 구체 클래스 - 스프링 빈으로 수동 등록
- v3 : 컴포넌트 스캔으로 스프링 빈 자동 등록

- 실무에서는 스프링 빈으로 등록할 클래스는 인터페이스가 있는 경우도 있고 없는 경우도 있다.
- 그리고 스프링 빈을 수동으로 직접 등록하는 경우도 있고, 컴포넌트 스캔으로 자동으로 등록하는
경우도 있다.
- 이런 다양한 케이스에 프록시를 어떻게 적용할까.

## v1 - 인터페이스와 구현 클래스 - 스프링 빈으로 수동 등록
- 지금까지 보아왔던 `Controller`, `Service`, `Repository`에 인터페이스를
도입하고, 스프링 빈으로 등록한다.
  
## OrderRepositoryV1
```java
package hello.proxy.app.v1;

public interface OrderRepositoryV1 {
    void save(String itemId);
}
```

## OrderRepositoryV1Impl
```java
package hello.proxy.app.v1;

public class OrderRepositoryV1Impl implements OrderRepositoryV1 {
    @Override
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

## OrderServiceV1
```java
package hello.proxy.app.v1;

public interface OrderServiceV1 {
    void orderItem(String itemId);
}
```

## OrderServiceImpl
```java
package hello.proxy.app.v1;

public class OrderServiceV1Impl implements OrderServiceV1 {
    private final OrderRepositoryV1 orderRepository;

    public OrderServiceV1Impl(OrderRepositoryV1 orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public void orderItem(String itemId) {
        orderRepository.save(itemId);
    }
}
```

## OrderControllerV1
```java
package hello.proxy.app.v1;

import org.springframework.web.bind.annotation.*;

@RequestMapping //스프링은 @Controller 또는 @RequestMapping 이 있어야 스프링 컨트롤러로 인식
@ResponseBody
public interface OrderControllerV1 {
    @GetMapping("/v1/request")
    String request(@RequestParam("itemId") String itemId);

    @GetMapping("/v1/no-log")
    String noLog();
}
```
- `@RequestMapping` : 스프링 MVC는 타입에 `@Controller` 또는 `@RequestMapping` 애노테이션이
있어야 스프링 컨트롤러로 인식한다. 그리고 스프링 컨트롤러로 인식해야, HTTP URL이 매핑되고
동작한다. 이 애노테이션은 인터페이스에 사용해도 된다.
- `@ResponseBody` : HTTP 메시지 컨버터를 사용해서 응답한다. 이 애노테이션은 인터페이스에 사용해도 된다.
- `@RequestParam("itemId") String itemId` : 인터페이스에는 `@RequestParam("itemId")`의
값을 생략하면 `itemId` 단어를 컴파일 이후 자바 버전에 따라 인식하지 못할 수 있다. 인터페이스에서는
꼭 넣어주자. 클래스에는 생략해도 대부분 잘 지원된다.
- 코드를 보면 `request()`, `noLog()` 두 가지 메서드가 있다. `request()`는 `LogTrace`를 적용할
대상이고, `noLog()`는 단순히 `LogTrace`를 적용하지 않을 대상이다.

## OrderControllerV1Impl
```java
package hello.proxy.app.v1;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class OrderControllerV1Impl implements OrderControllerV1 {
    private final OrderServiceV1 orderService;

    public OrderControllerV1Impl(OrderServiceV1 orderService) {
        this.orderService = orderService;
    }

    @Override
    public String request(String itemId) {
        orderService.orderItem(itemId);
        return "ok";
    }

    @Override
    public String noLog() {
        return "ok";
    }
}
```
- 컨트롤러 구현체이다. `OrderControlelrV1` 인터페이스에 스프링 관련 애노테이션이 정의되어 있다.

## AppV1Config
- 이제 스프링 빈으로 수동 등록해보자.
```java
package hello.proxy.config;

import hello.proxy.app.v1.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppV1Config {
    @Bean
    public OrderControllerV1 orderControllerV1() {
        return new OrderControllerV1Impl(orderServiceV1());
    }

    @Bean
    public OrderServiceV1 orderServiceV1() {
        return new OrderServiceV1Impl(orderRepositoryV1());
    }

    @Bean
    public OrderRepositoryV1 orderRepositoryV1() {
        return new OrderRepositoryV1Impl();
    }
}
```
### ProxyApplication - 코드 추가
```java
package hello.proxy;

import hello.proxy.config.AppV1Config;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@Import(AppV1Config.class)
@SpringBootApplication(scanBasePackages = "hello.proxy.app") //주의
public class ProxyApplication {
    public static void main(String[] args) {
        SpringApplication.run(ProxyApplication.class, args);
    }
}
```
- `@Import(AppConfig.class)` : 클래스를 스프링 빈으로 등록한다. 여기서는 `AppV1Config.class`를
스프링 빈으로 등록한다. 일반적으로 `@Configuration` 같은 설정 파일을 등록할 떄 사용하지만,
스프링 빈을 등록할 떄도 사용할 수 있다.
- `@SpringBootApplication(scanBasePackages = "hello.proxy.app")` : 
`@ComponentScan`의 기능과 같다. 컨포넌트 스캔을 시작할 위치를 지정한다. 이 값을 설정하면
해당 패키지와 그 하위 패키지를 컴포넌트 스캔한다. 이 값을 사용하지 않으면 `ProxyApplication`이
있는 패키지와 그 하위 패키지를 스캔한다.

### 주의
- `@Configuration`은 내부에 `@Component` 애노테이션을 포함하고 있어서 컴포넌트 스캔의 대상이 된다.
- 따라서 컴포넌트 스캔에 의해 `hello.proxy.config` 위치의 설정 파일들이 스프링 빈으로 자동 등록 되지
않도록 컴포넌트 스캔의 시작위치를 `scanBasePackage=hello.proxy.app`로 설정해야 한다.

## 실행 및 결과
- http://localhost:8080/v1/request?itemId=hello
- 웹 브라우저 화면 : `ok`