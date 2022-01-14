# 템플릿 콜백 패턴 - 예제
- 템플릿 콜백 패턴 구현. `ContextV2`와 내용이 같고 이름만 다름.
- `Context` -> `Template`
- `Strategy` -> `Callback`

## Callback - 인터페이스
```java
package hello.advanced.trace.strategy.code.template;

public interface Callback {
    void call();
}
```
- 콜백 로직을 전달할 인터페이스이다.
## TimeLogTemplate
```java
package hello.advanced.trace.strategy.code.template;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TimeLogTemplate {
    public void execute(Callback callback) {
        long startTime = System.currentTimeMillis();
        //비즈니스 로직 실행
        callback.call(); //위임
        //비즈니스 로직 종료
        long endTime = System.currentTimeMillis();
        long resultTime = endTime - startTime;
        log.info("resultTime={}", resultTime);
    }
}
```
## TemplateCallbackTest
```java
package hello.advanced.trace.strategy;

import hello.advanced.trace.strategy.code.template.Callback;
import hello.advanced.trace.strategy.code.template.TimeLogTemplate;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
public class TemplateCallbackTest {
    /**
     * 템플릿 콜백 패턴 - 익명 내부 클래스
     */
    @Test
    void callbackV1() {
        TimeLogTemplate template = new TimeLogTemplate();
        template.execute(new Callback() {
            @Override
            public void call() {
                log.info("비즈니스 로직1 실행");
            }
        });
        template.execute(new Callback() {
            @Override
            public void call() {
                log.info("비즈니스 로직2 실행");
            }
        });
    }

    /**
     * 템플릿 콜백 패턴 - 람다
     */
    @Test
    void callbackV2() {
        TimeLogTemplate template = new TimeLogTemplate();
        template.execute(() -> log.info("비즈니스 로직1 실행"));
        template.execute(() -> log.info("비즈니스 로직2 실행"));
    }
}
```
- 별도의 클래스를 만들어서 전달해도 되지만, 콜백을 사용할 경우 익명 내부 클래스나 람다를 사용하는 것이
편리하다.
- 물론 여러 곳에서 함께 사용되는 경우 재사용을 위해 콜백을 별도의 클래스로 만들어도 된다.
