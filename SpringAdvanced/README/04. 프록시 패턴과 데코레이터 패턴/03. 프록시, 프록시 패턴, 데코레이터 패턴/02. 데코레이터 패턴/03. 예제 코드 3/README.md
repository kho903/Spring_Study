# 데코레이터 패턴 - 예제 코드 3
## 실행 시간을 측정하는 데코레이터
- 이번에는 기존 데코레이터에 더해서 실행 시간을 측정하는 기능까지 추가.
## TimeDecorator
```java
package hello.proxy.pureproxy.decorator.code;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TimeDecorator implements Component {
    private Component component;

    public TimeDecorator(Component component) {
        this.component = component;
    }

    @Override
    public String operation() {
        log.info("TimeDecorator 실행");
        long startTime = System.currentTimeMillis();
        String result = component.operation();
        long endTime = System.currentTimeMillis();
        long resultTime = endTime - startTime;
        log.info("TimeDecorator 종료 resultTime={}ms", resultTime);
        return result;
    }
}
```
- `TimeDecorator`는 실행 시간을 측정하는 부가 기능을 제공한다. 대상을 호출하기 전에 시간을 가지고
있다가, 대상의 호출이 끝나면 호출 시간을 로그로 남겨준다.

## DecoratorPatternTest - 추가
```java
@Test
void decorator2() {
    Component realComponent = new RealComponent();
    Component messageDecorator = new MessageDecorator(realComponent);
    Component timeDecorator = new TimeDecorator(messageDecorator);
    DecoratorPatternClient client = new DecoratorPatternClient(timeDecorator);
    client.execute();
}
```
- `client -> timeDecorator -> messageDecorator -> realComponent`의 객체 의존 관계를
설정하고 실행한다.

### 실행 결과
```text
TimeDecorator 실행
MessageDecorator 실행
RealComponent 실행
MessageDecorator 꾸미기 적용 전=data, 적용 후=*****data*****
TimeDecorator 종료 resultTime=7ms
result=*****data*****
```
- 실행 결과를 보면 `TiemDecorator`가 `MessageDecorator`를 실행하고 실행 시간을 측정해서 출력한 것을
확인할 수 있다.
