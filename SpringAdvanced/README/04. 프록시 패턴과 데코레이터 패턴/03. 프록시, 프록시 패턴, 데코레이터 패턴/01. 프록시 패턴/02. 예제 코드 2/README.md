# 프록시 패턴 - 예제 코드 2
- 프록시 패턴을 적용
## CacheProxy
```java
package hello.proxy.pureproxy.proxy.code;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CacheProxy implements Subject {
    private Subject target;
    private String cacheValue;

    public CacheProxy(Subject target) {
        this.target = target;
    }

    @Override
    public String operation() {
        log.info("프록시 호출");
        if (cacheValue == null) {
            cacheValue = target.operation();
        }
        return cacheValue;
    }
}
```
프록시도 실제 객체와 그 모양이 같아야 하기 때문에, `Subject` 인터페이스를 구현해야 한다.
- `private Subject target` : 클라이언트가 프록시를 호출하면 프록시가 최종적으로 
실제 객체를 호출해야 한다. 따라서 내부에 실제 객체의 참조를 가지고 있어야 한다. 이렇게
프록시가 호출하는 대상을 `target`이라 한다.
- `operation()` : 구현한 코드를 보면 `cacheValue`에 값이 없으면 실제 객체(`target`)를
호출해서 값을 구한다. 그리고 구한 값을 `cacheValue`에 저장하고 반환한다. 만약 `cacheValue`에
값이 있으면 실제 객체를 전혀 호출하지 않고, 캐시 값을 그대로 반환한다. 따라서 처음 조회 이후에는 
캐시(`cacheValue`)에서 매우 빠르게 데이터를 조회할 수 있다.

## ProxyPatternTest - cacheProxyTest() 추가
```java
package hello.proxy.pureproxy.proxy;

import hello.proxy.pureproxy.proxy.code.CacheProxy;
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

    @Test
    void cacheProxyTest() {
        Subject realSubject = new RealSubject();
        Subject cacheProxy = new CacheProxy(realSubject);
        ProxyPatternClient client = new ProxyPatternClient(cacheProxy);
        client.execute();
        client.execute();
        client.execute();
    }
}
```
## cacheProxyTest()
- `realSubject`와 `cacheProxy`를 생성하고 둘을 연결한다. 결과적으로 `cacheProxy`가
`realSubject`를 참조하는 런타임 객체 의존관계가 완성된다. 그리고 마지막으로 `client`에
`realSubject`가 아닌 `cacheProxy`를 주입한다. 이 과정을 통해서 
`client -> cacheProxy -> realSubject` 런타임 객체 의존관계가 완성된다.

- `cacheProxyTest()`는 `client.execute()`를 총 3번 호출한다. 이번에는 클라이언트가
실제 `realSubject`를 호출하는 것이 아니라 `cacheProxy`를 호출하게 된다.

### 실행 결과
```text
CacheProxy - 프록시 호출
RealSubject - 실제 객체 호출
CacheProxy - 프록시 호출
CacheProxy - 프록시 호출
```

## client.execute() 3번 호출 프로세스
1. client의 cacheProxy 호출 -> cacheProxy에 캐시 값이 없다. -> realSubject를 호출, 결과를 캐시에 저장 (1초)
2. client의 cacheProxy 호출 -> cacheProxy에 캐시 값이 있다. -> cacheProxy에서 즉시 반환 (0초)
3. client의 cacheProxy 호출 -> cacheProxy에 캐시 값이 있다. -> cacheProxy에서 즉시 반환 (0초)

- 결과적으로 캐시 프록시를 도입하기 전에는 3초가 걸렸지만, 캐시 프록시 도입 이후에는 최초에 한번만
1초가 걸리고, 이후에는 거의 즉시 반환한다.

### 정리
- 프록시 패턴의 핵심은 `RealSubject` 코드와 클라이언트 코드를 전혀 변경하지 않고, 프록시를 도입해서
접근 제어를 했다는 점이다.
- 그리고 클라이언트 코드의 변경 없이 자유롭게 프록시를 넣고 뺄 수 있다. 실제 클라이언트 입장에서는
프록시 객체가 주입되었는 지, 실제 객체가 주입되었는 지 알지 못한다.
