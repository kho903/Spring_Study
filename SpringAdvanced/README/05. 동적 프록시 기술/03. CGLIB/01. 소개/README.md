# CGLIB - 소개
- CGLIB는 바이트코드를 조작해서 동적으로 클래스를 생성하는 기술을 제공하는 라이브러리이다.
- CGLIB를 사용하면 인터페이스가 없어도 구체 클래스만 가지고 동적 프록시를 만들어낼 수 있다.
- CGLIB는 원래는 외부 라이브러리인데, 스프링 프레임워크가 스프링 내부 소스 코드에 포함했다.
따라서 스프링을 사용한다면 별도의 외부 라이브러리를 추가하지 않아도 사용할 수 있다.
- 참고로 우리가 CGLIB를 직접 사용하는 경우는 거의 없다. 스프링의 `ProxyFactory`라는 것이
이 기술을 편리하게 사용하게 도와주기 때문에, 대략 개념만 잡으면 된다.

## 공통 예제 코드
- 인터페이스와 구현이 있는 서비스 클래스 - `ServiceInterface`, `ServiceImpl`
- 구체 클래스만 있는 서비스 클래스 - `ConcreteService`

### ServiceInterface
```java
package hello.proxy.common.service;

public interface ServiceInterface {
    void save();

    void find();
}
```

### ServiceImpl
```java
package hello.proxy.common.service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ServiceImpl implements ServiceInterface {
    @Override
    public void save() {
        log.info("save 호출");
    }

    @Override
    public void find() {
        log.info("find 호출");
    }
}
```

### ConcreteService
```java
package hello.proxy.common.service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ConcreteService {
    public void call() {
        log.info("ConcreteService 호출");
    }
}
```