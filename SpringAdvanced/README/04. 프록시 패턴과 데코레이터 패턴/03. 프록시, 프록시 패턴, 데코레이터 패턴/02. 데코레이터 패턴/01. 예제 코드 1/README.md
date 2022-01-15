# 데코레이터 패턴 - 예제 코드 1
- 데코레이터 패턴을 이해하기 위한 코드 작성. 데코레이터 도입 전 코드

## Component 인터페이스
```java
package hello.proxy.pureproxy.decorator.code;

public interface Component {
    String operation();
}
```
- `Component` 인터페이스는 단순히 `String operation()` 메서드를 가진다.

## RealComponent
```java
package hello.proxy.pureproxy.decorator.code;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RealComponent implements Component {
    @Override
    public String operation() {
        log.info("RealComponent 실행");
        return "data";
    }
}
```
- `RealComponent`는 `Componet` 인터페이스를 구현한다.
- `operation()` : 단순히 로그를 남기고 `"data"` 문자를 반환한다.

## DecoratorPatternClient
```java
package hello.proxy.pureproxy.decorator.code;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DecoratorPatternClient {
    private Component component;

    public DecoratorPatternClient(Component component) {
        this.component = component;
    }

    public void execute() {
        String result = component.operation();
        log.info("result={}", result);
    }
}
```
- 클라이언트 코드는 단순히 `Component` 인터페이스를 의존한다.
- `execut()`를 실행하면 `component.operation()`을 호출하고, 그 결과를 출력한다.

## DecoratorPatternTest
```java
package hello.proxy.pureproxy.decorator;

import hello.proxy.pureproxy.decorator.code.*;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
public class DecoratorPatternTest {
    @Test
    void noDecorator() {
        Component realComponent = new RealComponent();
        DecoratorPatternClient client = new DecoratorPatternClient(realComponent);
        client.execute();
    }
}
```
- 테스트 코드는 `client -> realComponent`의 의존관계를 설정하고, `client.execute()`를 호출한다.

### 실행 결과
```text
RealComponent - RealComponent 실행
DecoratorPatternClient - result=data
```
