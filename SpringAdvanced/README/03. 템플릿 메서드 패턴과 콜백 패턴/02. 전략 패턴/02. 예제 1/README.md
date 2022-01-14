# 전략 패턴 - 예제 1
- 동일한 문제를 전략 패턴을 사용해서 해결
- 템플릿 메서드 패턴은 부모 클래스에 변하지 않는 템플릿을 두고, 변하는 부분을 자식 클래스에 두어서
상속을 사용해서 문제를 해결했다.
- 전략 패턴은 변하지 않는 부분을 `Context`라는 곳에 두고, 변하는 부분을 `Strategy`라는 인터페이스를
만들고 해당 인터페이스를 구현하도록 문제를 해결한다. 상속이 아니라 위임으로 해결하는 것이다.
- 전략 패턴에서 `Context`는 변하지 않는 템플릿 역할을 하고, `Strategy`는 변하는 알고리즘 역할을 한다.

## 전략 패턴의 의도
- GOF 디자인 패턴에서 정의한 전략 패턴의 의도는 다음과 같다.
- 알고리즘 제품군을 정의하고 각각을 캡슐화하여 상호 교환 가능하게 만들자. 전략을 사용하면 알고리즘을
사용하는 클라이언트와 독립적으로 알고리즘을 변경할 수 있다.

## Strategy 인터페이스
```java
package hello.advanced.trace.strategy.code.strategy;

public interface Strategy {
    void call();
}
```
## StrategyLogic1
```java
package hello.advanced.trace.strategy.code.strategy;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class StrategyLogic1 implements Strategy {
    @Override
    public void call() {
        log.info("비즈니스 로직1 실행");
    }
}
```
- 변하는 알고리즘은 `Strategy` 인터페이스를 구현하면 된다. 여기서는 비즈니스 로직1을 구현했다.

## StrategyLogic2
```java
package hello.advanced.trace.strategy.code.strategy;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class StrategyLogic2 implements Strategy {
    @Override
    public void call() {
        log.info("비즈니스 로직2 실행");
    }
}
```

## ContextV1
```java
package hello.advanced.trace.strategy.code.strategy;

import lombok.extern.slf4j.Slf4j;

/**
 * 필드에 전략을 보관하는 방식
 */
@Slf4j
public class ContextV1 {
    private Strategy strategy;

    public ContextV1(Strategy strategy) {
        this.strategy = strategy;
    }

    public void execute() {
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
- `ContextV1`은 변하지 않는 로직을 가지고 있는 템플릿 역할을 하는 코드이다. 전략 패턴에서는 이것을 컨텍스트(문맥)이라 한다.
- 쉽게 이야기해서 컨텍스트 (문맥)는 크게 변하지 않지만, 그 문맥 속에서 `strategy`를 통해 일부 전략이 변경된다 생각하면 된다.
- `Context`는 내부에 `Strategy strategy` 필드를 가지고 있다. 이 필드에 변하는 부분인 `Strategy`의 구현체를 주입하면 된다.
- 전략 패턴의 핵심은 `Context`는 `Strategy` 인터페이스에만 의존한다는 점이다. 덕분에 `Strategy`의 구현체를 변경하거나 새로
만들어도 `Context` 코드에는 영향을 주지 않는다.
- 스프링에서 의존관계 주입에서 사용하는 방식이 바로 전략 패턴이다.
## ContextV1Test - 추가
```java
/**
 * 전략 패턴 적용
 */
@Test
void strategyV1() {
     Strategy strategyLogic1 = new StrategyLogic1();
     ContextV1 context1 = new ContextV1(strategyLogic1);
     context1.execute();
        
     Strategy strategyLogic2 = new StrategyLogic2();
     ContextV1 context2 = new ContextV1(strategyLogic2);
     context2.execute();
}
```
- 전략 패턴을 사용해보자.
- 코드를 보면 의존관계 주입을 통해 `ContextV1`에 `Strategy`의 구현체인 
  `strategyLogic1`을 주입하는 것을 확인할 수 있다.
- 이렇게 해서 `Context`안에 원하는 전략을 주입한다.
- 이렇게 원하는 모양으로 조립을 완료하고 난 다음에 `context1.execute()`를 호출해서
`context`를 실행한다.

## 전략 패턴 실행 프로세스
1. `Context`에 원하는 `Strategy` 구현체를 주입한다.
2. 클라이언트는 `context`를 실행한다.
3. `context`는 `context` 로직을 시작한다.
4. `context` 로직 중간에 `strategy.call()`을 호출해서 주입 받은 `strategy`로직을 실행한다.
5. `context`는 나머지 로직을 실행한다.

## 실행 결과
```text
StrategyLogic1 - 비즈니스 로직1 실행
ContextV1 - resultTime=3
StrategyLogic2 - 비즈니스 로직2 실행
ContextV1 - resultTime=0
```