# 프록시 패턴 - 예제 코드 1
## 프록시 패턴 - 예제 코드 작성
- 프록시 패턴을 이해하기 위한 예제 코드 작성. 먼저 프록시 패턴 도입 전 코드를 단순하게 만든다.

## Subject 인터페이스
```java
package hello.proxy.pureproxy.proxy.code;

public interface Subject {
    String operation();
}
```
- 예제에서 `Subject` 인터페이스는 단순히 `operation()` 메서드 하나만 가지고 있다.

## RealSubject
```java
package hello.proxy.pureproxy.proxy.code;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RealSubject implements Subject {
    @Override
    public String operation() {
        log.info("실제 객체 호출");
        sleep(1000);
        return "data";
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
- `RealSubject`는 `Subject` 인터페이스를 구현했다. `operation()`은 데이터 조회를 시뮬레이션
하기 위해 1초 쉬도록 했다. 예를 들어서 데이터나 DB나 외부에서 조회하는 데 1초가 걸린다고 생각하면 된다.
- 호출할 때마다 시스템에 큰 부하를 주는 데이터 조회라고 가정한다.

## ProxyPatternClient
```java
package hello.proxy.pureproxy.proxy.code;

public class ProxyPatternClient {
    private Subject subject;

    public ProxyPatternClient(Subject subject) {
        this.subject = subject;
    }

    public void execute() {
        subject.operation();
    }
}
```
- `Subject` 인터페이스에 의존하고, `Subject`를 호출하는 클라이언트 코드이다.
- `execute()`를 실행하면 `subject.operation()`를 호출한다.

## ProxyPatternTest
```java
package hello.proxy.pureproxy.proxy;

import hello.proxy.pureproxy.proxy.code.ProxyPatternClient;
import hello.proxy.pureproxy.proxy.code.RealSubject;
import hello.proxy.pureproxy.proxy.code.Subject;
import org.junit.jupiter.api.Test;

public class ProxyPatternTest {
    @Test
    void noProxyTest() {
        RealSubject realSubject = new RealSubject();
        ProxyPatternClient client = new ProxyPatternClient(realSubject);
        client.execute();
        client.execute();
        client.execute();
    }
}
```
- 테스트 코드에서는 `client.execut()`를 3번 호출한다. 데이터를 조회하는 데 1초가 소모되므로
총 3초의 시간이 걸린다.

### 실행 결과
```text
RealSubject - 실제 객체 호출
RealSubject - 실제 객체 호출
RealSubject - 실제 객체 호출
```
### client.execute()를 3번 호출하면 다음과 같이 처리된다.
1. client -> realSubject 를 호출해서 값을 조회한다. (1초)
2. client -> realSubject 를 호출해서 값을 조회한다. (1초)
3. client -> realSubject 를 호출해서 값을 조회한다. (1초)

### 캐시
- 그런데 이 데이터가 한 번 조회하면 변하지 않는 데이터라면 어딘가에 보관해두고 이미 조회한 데이터를
사용하는 것이 성능상 좋다. 이런 것을 캐시라고 한다.
- 프록시 패턴의 주요 기능은 접근 제어 이다. 캐시도 접근 자체를 제어하는 기능 중 하나이다.
- 이미 개발된 로직을 전혀 수행하지 않고, 프록시 객체를 통해 캐시를 적용 가능하다.
