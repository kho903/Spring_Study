# 전략 패턴 - 예제 3
- 이전에는 `Context` 필드에 `Strategy`를 주입해서 사용했다.
- 이번에는 전략을 실행할 때 직접 파라미터로 전달해서 사용
## ContextV2
```java
package hello.advanced.trace.strategy.code.strategy;

import lombok.extern.slf4j.Slf4j;

/**
 * 전략을 파라미터로 전달 받는 방식
 */
@Slf4j
public class ContextV2 {
    public void execute(Strategy strategy) {
        long startTime = System.currentTimeMillis();
        //비즈니스 로직 실행
        strategy.call(); //위임
        //비즈니스 로직 종료
        long endTime = System.currentTimeMillis();
        long resultTime = endTime - startTime;
        log.info("resultTime={}", resultTime);
    }
}
```
- `ContextV2`는 전략을 필드로 가지지 않는다. 대신에 전략을 `execute(..)`가 호출될 때마다
항상 파라미터로 전달 받는다.

## ContextV2Test
```java
package hello.advanced.trace.strategy;

import hello.advanced.trace.strategy.code.strategy.ContextV2;
import hello.advanced.trace.strategy.code.strategy.Strategy;
import hello.advanced.trace.strategy.code.strategy.StrategyLogic1;
import hello.advanced.trace.strategy.code.strategy.StrategyLogic2;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
public class ContextV2Test {
    /**
     * 전략 패턴 적용
     */
    @Test
    void strategyV1() {
        ContextV2 context = new ContextV2();
        context.execute(new StrategyLogic1());
        context.execute(new StrategyLogic2());
    }
}
```
- `Context`와 `Strategy`를 '선 조립 후 실행' 하는 방식이 아니라 `Context`를 실행할 때마다
전략을 인수로 전달한다.
- 클라이언트는 `Context`를 실행하는 시점에 원하는 `Strategy`를 전달할 수 있다. 따라서
  이전 방식과 비교해서 원하는 전략을 더욱 유연하게 변경할 수 있다.
- 테스트 코드를 보면 하나의 `Context`만 생성한다. 그리고 하나의 `Context`에 실행 시점에 여러
전략을 인수로 전달해서 유연하게 실행하는 것을 확인할 수 있다.

## 전략 패턴 파라미터 프로세스
1. 클라이언트는 `Context`를 실행하면서 인수로 `Strategy`를 전달한다.
2. `Context`는 `execute()`로직을 실행한다.
3. `Context`는 파라미터로 넘어온 `strategy.call()` 로직을 실행한다.
4. `Context`의 `execute()` 로직이 종료된다.

## ContextV2Test - 추가
```java
/**
 * 전략 패턴 익명 내부 클래스
 */
@Test
void strategyV2() {
    ContextV2 context = new ContextV2();
    context.execute(new Strategy() {
        @Override
        public void call() {
            log.info("비즈니스 로직1 실행");
        }
    });
    context.execute(new Strategy() {
        @Override
        public void call() {
            log.info("비즈니스 로직2 실행");
        }
    });
}
```
- 여기도 물론 익명 내부 클래스를 사용할 수 있다.
- 코드 조각을 파라미터로 넘긴다고 생각하면 더 자연스럽다.

## ContextV2Test - 추가
```java
/**
 * 전략 패턴 익명 내부 클래스2, 람다
 */
@Test
void strategyV3() {
    ContextV2 context = new ContextV2();
    context.execute(() -> log.info("비즈니스 로직1 실행"));
    context.execute(() -> log.info("비즈니스 로직2 실행"));
}
```
- 람다로 단순화

### 정리
- `ContextV1`은 필드에 `Strategy`를 저장하는 방식으로 전략 패턴을 구사했다.
    - 선 조립, 후 실행 방법에 적합하다.
    - `Context`를 실행하는 시점에는 이미 조립이 끝났기 때문에 전략을 신경쓰지 않고 단순히
    실행만 하면 된다.
- `ContextV2`는 파라미터에 `Strategy`를 전달받는 방식으로 전략 패턴을 구사했다.
    - 실행할 떄 마다 전략을 유연하게 변경할 수 있다.
    - 단점 역시 실행할 떄 마다 전략을 계속 지정해주어야 한다는 점이다.

## 템플릿
- 지금 우리가 해결하고 싶은 문제는 변하는 부분과 변하지 않는 부분을 분리하는 것이다.
- 변하지 않는 부분을 템플릿이라고 하고, 그 템플릿 안에서 변하는 부분에 약간 다른 코드 조각을 넘겨서
실행하는 것이 목적이다.
- 지금 우리가 원하는 것은 애플리케이션 의존 관계를 설정하는 것처럼 선 조립, 후 실행이 아니다. 단순히
  코드를 실행할 때마다 변하지 않는 템플릿이 있고, 그 템플릿 안에서 원하는 부분만 살짝 다른 코드를 실행
  하고 싶을 뿐이다.
- `ContextV1`, `ContextV2` 중 우리가 고민하는 문제에서는 실행 시점에 유연하게 실행 코드 조각을 전달하는
`ContextV2`가 더 적합하다.
