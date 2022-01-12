# 템플릿 메서드 패턴 - 예제1
## TemplateMethodTest
```java
package hello.advanced.trace.template;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
public class TemplateMethodTest {
    @Test
    void templateMethodV0() {
        logic1();
        logic2();
    }

    private void logic1() {
        long startTime = System.currentTimeMillis();
        //비즈니스 로직 실행
        log.info("비즈니스 로직1 실행");
        //비즈니스 로직 종료
        long endTime = System.currentTimeMillis();
        long resultTime = endTime - startTime;
        log.info("resultTime={}", resultTime);
    }

    private void logic2() {
        long startTime = System.currentTimeMillis();
        //비즈니스 로직 실행
        log.info("비즈니스 로직2 실행");
        //비즈니스 로직 종료
        long endTime = System.currentTimeMillis();
        long resultTime = endTime - startTime;
        log.info("resultTime={}", resultTime);
    }
}
```
- `logic1()`, `logic2()`를 호출하는 단순한 테스트 코드이다.

## 실행 결과
```text
비즈니스 로직1 실행
resultTime=5
비즈니스 로직2 실행
resultTime=1
```
`logic1()`과 `logic2()`는 시간을 측정하는 부분과 비즈니스 로직을 
  실행하는 부분이 함께 존재한다.
- 변하는 부분 : 비즈니스 로직
- 변하지 않는 부분 : 시간 측정
- 이제 템플릿 메서드 패턴을 사용해서 변하는 부분과 변하지 않는 부분을 분리해본다.
