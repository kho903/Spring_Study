# 구체 클래스 기반 프록시 - 예제 1
- 다음에 보이는 `ConcreteLogic`은 인터페이스가 없고 구체 클래스만 있다. 이렇게 인터페이스가
없어도 프록시를 적용할 수 있을까?
- 먼저 프록시 작성 전 기본 코드.
## ConcreteLogic
```java
package hello.proxy.pureproxy.concreteproxy.code;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ConcreteLogic {
    public String operation() {
        log.info("ConcreteLogic 실행");
        return "data";
    }
}
```
- `ConcreteLogic`은 인터페이스가 없고, 구체 클래스만 있다. 여기에 프록시를 도입해야 한다.

## ConcreteClient
```java
package hello.proxy.pureproxy.concreteproxy.code;

public class ConcreteClient {
    private ConcreteLogic concreteLogic;

    public ConcreteClient(ConcreteLogic concreteLogic) {
        this.concreteLogic = concreteLogic;
    }

    public void execute() {
        concreteLogic.operation();
    }
}
```
## ConcreteProxyTest
```java
package hello.proxy.pureproxy.concreteproxy;

import hello.proxy.pureproxy.concreteproxy.code.ConcreteClient;
import hello.proxy.pureproxy.concreteproxy.code.ConcreteLogic;
import org.junit.jupiter.api.Test;

public class ConcreteProxyTest {
    @Test
    void noProxy() {
        ConcreteLogic concreteLogic = new ConcreteLogic();
        ConcreteClient client = new ConcreteClient(concreteLogic);
        client.execute();
    }
}
```