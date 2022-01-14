# 전략 패턴 - 예제 2
- 전략 패턴도 익명 내부 클래스를 사용할 수 있다.

## ContextV1Test - 추가
```java
/**
 * 전략 패턴 익명 내부 클래스1
 */
@Test
void strategyV2() {
    Strategy strategyLogic1 = new Strategy() {
        @Override
        public void call() {
            log.info("비즈니스 로직1 실행");
        }
    };
    log.info("strategyLogic1={}", strategyLogic1.getClass());
        
    ContextV1 context1 = new ContextV1(strategyLogic1);
    context1.execute();
    Strategy strategyLogic2 = new Strategy() {
        @Override
        public void call() {
            log.info("비즈니스 로직2 실행");
        }
    };
    log.info("strategyLogic2={}", strategyLogic2.getClass());
        
    ContextV1 context2 = new ContextV1(strategyLogic2);
    context2.execute();
}
```
### 실행 결과
```text
ContextV1Test - strategyLogic1=class
hello.advanced.trace.strategy.ContextV1Test$1
ContextV1Test - 비즈니스 로직1 실행
ContextV1 - resultTime=0
ContextV1Test - strategyLogic2=class
hello.advanced.trace.strategy.ContextV1Test$2
ContextV1Test - 비즈니스 로직2 실행
ContextV1 - resultTime=0
```
- 실행 결과를 보면 `ContextV1Test$1`, `ContextV1Test$2`와 같이 익명 내부 클래스가 생성된 것을
확인할 수 있다.
  
## ContextV1Test - 추가
```java
/**
 * 전략 패턴 익명 내부 클래스2
 */
@Test
void strategyV3() {
    ContextV1 context1 = new ContextV1(new Strategy() {
        @Override
        public void call() {
            log.info("비즈니스 로직1 실행");
        }
    });
    context1.execute();
    ContextV1 context2 = new ContextV1(new Strategy() {
        @Override
        public void call() {
        log.info("비즈니스 로직2 실행");
        }
    });
    context2.execute();
}
```
- 익명 내부 클래스를 변수에 담아두지 말고, 생성하면서 바로 `ContextV1`에 전달해도 된다.

## ContextV1Test - 추가
```java
/**
 * 전략 패턴, 람다
 */
@Test
void strategyV4() {
    ContextV1 context1 = new ContextV1(() -> log.info("비즈니스 로직1 실행"));
    context1.execute();
    ContextV1 context2 = new ContextV1(() -> log.info("비즈니스 로직2 실행"));
    context2.execute();
}
```
- 익명 내부 클래스를 자바8부터 제공하는 람다로 변경할 수 있다. 
- 람다로 변경하려면 인터페이스에 메서드가 1개만 있으면 되는데, 여기에서 제공하는 `Strategy`
인터페이스는 메서드가 1개만 있으므로 람다로 사용할 수 있다.
  
### 정리
- 전략 패턴은 변하지 않는 부분을 `Context`에 두고 `Strategy`를 구현해서 만든다.
- 그리고 `Context`의 내부 필드에 `Strategy`를 주입해서 사용했다.

### 선 조립, 후 실행
- `Context`의 내부 필드에 `Strategy`를 두고 사용하는 방식은 `Context`와 `Strategy`를
실행 전에 원하는 모양으로 조립해두고, 그 다음에 `Context`를 실행하는 선 조립, 후 실행 방식에서 매우 유용
- `Context`와 `Strategy`를 한 번 조립하고 나면 이후로는 `Context`를 실행하기만 하면 된다.
- 우리가 스프링으로 애플리케이션을 개발할 때, 애플리케이션 로딩 시점에 의존관계 주입을 통해 필요한
의존관계를 모두 맺어두고 난 다음에 실제 요청을 처리하는 것과 같은 원리이다.
- 이 방식의 단점은 `Context`와 `Strategy`를 조립한 이후에는 전략을 변경하기가 번거롭다는 점이다.
- 물론 `Context`에 `setter`를 제공해서 `Strategy`를 넘겨 받아 변경하면 되지만, `Context`를 
싱글톤으로 사용할 때는 동시성 이슈 등 고려할 점이 많다.
- 그래서 전략을 실시간으로 변경해야 하면 차라리 이전에 개발한 테스트 코드처럼 `Context`를 하나 더 생성하고
그곳에 다른 `Strategy`를 주입하는 것이 더 나은 선택일 수 있다.
  