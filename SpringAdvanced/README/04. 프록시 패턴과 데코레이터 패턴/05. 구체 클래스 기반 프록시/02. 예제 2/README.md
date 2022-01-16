# 구체 클래스 기반 프록시 - 예제 2
## 클래스 기반 프록시 도입
- 지금까지 인터페이스를 기반으로 프록시를 도입했다. 그런데 자바의 다형성은 인터페이스를 구현하든,
아니면 클래스를 상속하든 상위 타입만 맞으면 다형성이 적용된다.
- 따라서 인터페이스가 없어도 프록시를 만들 수 있다는 뜻.

## TimeProxy
```java
package hello.proxy.pureproxy.concreteproxy.code;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TimeProxy extends ConcreteLogic {
    private ConcreteLogic realLogic;

    public TimeProxy(ConcreteLogic realLogic) {
        this.realLogic = realLogic;
    }

    @Override
    public String operation() {
        log.info("TimeDecorator 실행");
        long startTime = System.currentTimeMillis();
        String result = realLogic.operation();
        long endTime = System.currentTimeMillis();
        long resultTime = endTime - startTime;
        log.info("TimeDecorator 종료 resultTime={}", resultTime);
        return result;
    }
}
```
- `TimeProxy` 프록시는 시간을 측정하는 부가 기능을 제공한다. 그리고 인터페이스가 아니라
클래스인 `ConcreteLogic`을 상속 받아서 만든다.
  
## ConcreteProxyTest - addProxy
```java
@Test
void addProxy() {
    ConcreteLogic concreteLogic = new ConcreteLogic();
    TimeProxy timeProxy = new TimeProxy(concreteLogic);
    ConcreteClient client = new ConcreteClient(timeProxy);
    client.execute();
}
```
- 여기서 핵심은 `ConcreteClient`의 생성자에 `concreteLogic`이 아니라 `tiemProxy`를
주입하는 부분이다.
- `ConcreteClient`는 `ConcreteLogic`을 의존하는데, 다형성에 의해 `ConcreteLogic`에
`concreteLogic`도 들어갈 수 있고, `timeProxy`도 들어갈 수 있다.

## ConcreteLogic에 할당할 수 있는 객체
- `ConcreteLogic = concreteLogic` (본인과 같은 타입을 할당)
- `ConcreteLogix = timeProxy` (자식 타입을 할당)

## ConcreteClient 참고
```java
public class ConcreteClient {
    private ConcreteLogic concreteLogic; //ConcreteLogic, TimeProxy 모두 주입 가능

    public ConcreteClient(ConcreteLogic concreteLogic) {
        this.concreteLogic = concreteLogic;
    }

    public void execute() {
        concreteLogic.operation();
    }
}
```
### 실행 결과
```text
TimeDecorator 실행
ConcreteLogic 실행
TimeDecorator 종료 resultTime=1
```
- 실행 결과를 보면 인터페이스가 없어도 클래스 기반의 프록시가 잘 적용된 것을 확인할 수 있다.

> 참고 : 자바 언어에서 다형성은 인터페이스나 클래스를 구분하지 않고 모두 적용된다. 해당 타입과
> 그 타입의 하위 타입은 모두 다형성의 대상이 된다.