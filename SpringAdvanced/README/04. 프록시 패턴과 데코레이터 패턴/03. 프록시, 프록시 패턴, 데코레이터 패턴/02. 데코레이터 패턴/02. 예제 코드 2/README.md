# 데코레이터 패턴 - 예제 코드 2
## 부가 기능 추가
- 프록시를 통해서 할 수 있는 기능은 크게 접근 제어와 부가 기능 추가라는 2가지로 구분한다.
- 앞서 프록시 패턴에서 캐시를 통한 접근 제어를 알아보았고, 이번에는 프록시를 활용해서 부가
기능을 추가한다. 이렇게 프록시로 부가 기능을 추가하는 것을 데코레이터 패턴이라 한다.

- 데코레이터 패턴 : 원래 서버가 제공하는 기능에 더해서 부가 기능을 수행한다.
    - 예) 요청 값이나, 응답 값을 중간에 변형한다.
    - 예) 실행 시간을 측정해서 추가 로그를 남긴다.

## 응답 값을 꾸며주는 데코레이터
### MessageDecorator
```java
package hello.proxy.pureproxy.decorator.code;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MessageDecorator implements Component {
    private Component component;

    public MessageDecorator(Component component) {
        this.component = component;
    }

    @Override
    public String operation() {
        log.info("MessageDecorator 실행");
        String result = component.operation();
        String decoResult = "****" + result + "****";
        log.info("MessageDecorator 꾸미기 적용 전={}, 적용 후={}", result, decoResult);
        return decoResult;
    }
}
```
- `MessageDecorator`는 `Component` 인터페이스를 구현한다.
- 프록시가 호출해야 하는 대상을 `component`에 저장한다.
- `operation()`을 호출하면 프록시와 연결된 대상을 호출(`component.operation()`)하고,
그 응답 값에 `****`를 더해서 꾸며준 다음 반환한다.
    - 예) 응답값이 `data`라면 -> `****data****`

### DecoratorPatternTest - 추가
```java
@Test
void decorator1() {
    Component realComponent = new RealComponent();
    Component messageDecorator = new MessageDecorator(realComponent);
    DecoratorPatternClient client = new DecoratorPatternClient(messageDecorator);
    client.execute();
}
```
- `client -> messageDecorator -> realComponent`의 객체 의존 관계를 만들고,
`client.execute()`를 호출한다.
  
### 실행 결과
```text
MessageDecorator - MessageDecorator 실행
RealComponent - RealComponent 실행
MessageDecorator - MessageDecorator 꾸미기 적용 전=data, 적용 후=*****data*****
DecoratorPatternClient - result=*****data*****
```
- 실행 결과를 보면 `MessageDecorator`가 `RealComponent`를 호출하고 반환한 응답 메시지를 
  꾸며서 반환한 것을 확인할 수 있다.
