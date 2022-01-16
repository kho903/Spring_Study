# JDK 동적 프록시 - 소개
- 지금까지 프록시를 적용하기 위해 적용 대상의 숫자 만큼 많은 프록시 클래스를 만들었다.
  적용 대상이 100개면 프록시 클래스도 100개 만들었다.
- 그런데 앞서 살펴본 것과 같이 프록시 클래스의 기본 코드와 흐름은 거의 같고, 프록시를 어떤 대상에
적용하는가 정도만 차이가 있었다. 쉽게 말해, 프록시의 로직은 같은데, 적용 대상만 차이가 있는 것이다.
- 이 문제를 해결하는 것이 바로 동적 프록시 기술이다.
- 동적 프록시 기술을 사용하면 개발자가 직접 프록시 클래스를 만들지 않아도 된다. 이름 그대로
프록시 객체를 동적으로 런타임에 개발자 대신 만들어준다. 그리고 동적 프록시에 원하는 실행 로직을
지정할 수 있다.

> 주의 : JDK 동적 프록시는 인터페이스를 기반으로 프록시를 동적으로 만들어준다. 따라서
> 인터페이스가 필수이다.

- 먼저 자바 언어가 기본으로 제공하는 JDK 동적 프록시
## 기본 예제 코드
- JDK 동적 프록시를 이해하기 위한 단순한 코드.
- 간단한 A, B 클래스, JDK 동적 프록시는 인터페이스가 필수이다. 따라서 인터페이스와 구현체로
구분했다.
  
## AInterface
```java
package hello.proxy.jdkdynamic.code;

public interface AInterface {
    String call();
}
```

## AImpl
```java
package hello.proxy.jdkdynamic.code;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AImpl implements AInterface {
    @Override
    public String call() {
        log.info("A 호출");
        return "a";
    }
}
```

## BInterface
```java
package hello.proxy.jdkdynamic.code;

public interface BInterface {
    String call();
}
```

## BImpl
```java
package hello.proxy.jdkdynamic.code;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BImpl implements BInterface {
    @Override
    public String call() {
        log.info("B 호출");
        return "b";
    }
}
```
