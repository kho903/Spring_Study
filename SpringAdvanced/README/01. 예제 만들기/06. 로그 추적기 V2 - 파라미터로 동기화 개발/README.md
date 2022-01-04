# 로그 추적기 V2 - 파라미터로 동기화 개발
- 트랜잭션ID와 메서드 호출의 깊이를 표현하는 가장 단순한 방법은 첫 로그에서 사용한 
  `트랜잭션ID`와 `level`을 다음 로그에 넘겨주면 된다.
- 현재 로그의 상태 정보인 `트랜잭션ID`와 `level`은 `TraceId`에 포함되어 있다. 따라서
`TraceId`를 다음 로그에 넘겨주면 된다. -> 이 기능을 추가한 `HelloTraceV2`

## HelloTraceV2
```java
package hello.advanced.trace.hellotrace;

import hello.advanced.trace.TraceId;
import hello.advanced.trace.TraceStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class HelloTraceV2 {
  private static final String START_PREFIX = "-->";
  private static final String COMPLETE_PREFIX = "<--";
  private static final String EX_PREFIX = "<X-";

  public TraceStatus begin(String message) {
    TraceId traceId = new TraceId();
    Long startTimeMs = System.currentTimeMillis();
    log.info("[" + traceId.getId() + "] " + addSpace(START_PREFIX,
            traceId.getLevel()) + message);
    return new TraceStatus(traceId, startTimeMs, message);
  }

  //V2에서 추가
  public TraceStatus beginSync(TraceId beforeTraceId, String message) {
    TraceId nextId = beforeTraceId.createNextId();
    Long startTimeMs = System.currentTimeMillis();
    log.info("[" + nextId.getId() + "] " + addSpace(START_PREFIX,
            nextId.getLevel()) + message);
    return new TraceStatus(nextId, startTimeMs, message);
  }

  public void end(TraceStatus status) {
    complete(status, null);
  }

  public void exception(TraceStatus status, Exception e) {
    complete(status, e);
  }

  private void complete(TraceStatus status, Exception e) {
    Long stopTimeMs = System.currentTimeMillis();
    long resultTimeMs = stopTimeMs - status.getStartTimeMs();
    TraceId traceId = status.getTraceId();
    if (e == null) {
      log.info("[" + traceId.getId() + "] " + addSpace(COMPLETE_PREFIX,
              traceId.getLevel()) + status.getMessage() + " time=" + resultTimeMs + "ms");
    } else {
      log.info("[" + traceId.getId() + "] " + addSpace(EX_PREFIX,
              traceId.getLevel()) + status.getMessage() + " time=" + resultTimeMs + "ms" + "
              ex = " + e);
    }
  }

  private static String addSpace(String prefix, int level) {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < level; i++) {
      sb.append((i == level - 1) ? "|" + prefix : "| ");
    }
    return sb.toString();
  }
}
```
- `HelloTraceV2`는 기존 코드인 `HelloTraceV2`와 같고, `beginSync(..)`가 추가되었다.
```java
//V2에서 추가
public TraceStatus beginSync(TraceId beforeTraceId, String message) {
    TraceId nextId = beforeTraceId.createNextId();
    Long startTimeMs = System.currentTimeMillis();
    log.info("[" + nextId.getId() + "] " + addSpace(START_PREFIX, nextId.getLevel()) + message);
    return new TraceStatus(nextId, startTimeMs, message);
}
```
### beginSync(...)
- 기존 `TraceId`에서 `createNextId()`를 통해 다음 Id를 구한다.
- `createNextId()`의 `TraceId` 생성 로직은 다음과 같다.
    - 트랜잭션Id는 기존과 같이유지한다.
    - 깊이를 표현하는 Level은 하나 증가한다. (0 -> 1)
  
## HelloTraceV2Test
```java
package hello.advanced.trace.hellotrace;

import hello.advanced.trace.TraceStatus;
import org.junit.jupiter.api.Test;

class HelloTraceV2Test {
  @Test
  void begin_end_level2() {
    HelloTraceV2 trace = new HelloTraceV2();
    TraceStatus status1 = trace.begin("hello1");
    TraceStatus status2 = trace.beginSync(status1.getTraceId(), "hello2");
    trace.end(status2);
    trace.end(status1);
  }

  @Test
  void begin_exception_level2() {
    HelloTraceV2 trace = new HelloTraceV2();
    TraceStatus status1 = trace.begin("hello");
    TraceStatus status2 = trace.beginSync(status1.getTraceId(), "hello2");
    trace.exception(status2, new IllegalStateException());
    trace.exception(status1, new IllegalStateException());
  }
}
```
- 처음에는 `begin(...)`을 사용하고, 이후에는 `beginSync(...)`를 사용하면 된다.
`beginSync(...)`를 호출할 때 직전 로그의 `traceId` 정보를 넘겨주어야 한다.

### begin_end_level2() - 실행 로그
```text
[0314baf6] hello1
[0314baf6] |-->hello2
[0314baf6] |<--hello2 time=2ms
[0314baf6] hello1 time=25ms
```
### begin_exception_level2() - 실행 로그
```text
[37ccb357] hello
[37ccb357] |-->hello2
[37ccb357] |<X-hello2 time=2ms ex=java.lang.IllegalStateException
[37ccb357] hello time=25ms ex=java.lang.IllegalStateException
```
- 실행로그를 보면 같은 `트랜잭션ID`를 유지하고, `level`을 통해 메서드 호출의 깊이를 표현하는 것을 확인할 수 있다.
