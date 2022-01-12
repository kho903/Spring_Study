# 쓰레드 로컬 동기화 - 개발
- `FieldLogTrace`에서 발생했던 동시성 문제를 `ThreadLocal`로 해결해보자.
- `TraceId traceIdHolder` 필드를 쓰레드 로컬을 사용하도록 `ThreadLocal<TraceId> traceHolder`로
변경하면 된다.
- 필드 대신에 쓰레드 로컬을 사용해서 데이터를 동기화하는 `ThreadLocalLogTrace`를 새로 만들자.

## ThreadLocalLogTrace
```java
package hello.advanced.trace.logtrace;

import hello.advanced.trace.TraceId;
import hello.advanced.trace.TraceStatus;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ThreadLocalLogTrace implements LogTrace {
    private static final String START_PREFIX = "-->";
    private static final String COMPLETE_PREFIX = "<--";
    private static final String EX_PREFIX = "<X-";
    private ThreadLocal<TraceId> traceIdHolder = new ThreadLocal<>();

    @Override
    public TraceStatus begin(String message) {
        syncTraceId();
        TraceId traceId = traceIdHolder.get();
        Long startTimeMs = System.currentTimeMillis();
        log.info("[{}] {}{}", traceId.getId(), addSpace(START_PREFIX,
                traceId.getLevel()), message);
        return new TraceStatus(traceId, startTimeMs, message);
    }

    @Override
    public void end(TraceStatus status) {
        complete(status, null);
    }

    @Override
    public void exception(TraceStatus status, Exception e) {
        complete(status, e);
    }

    private void complete(TraceStatus status, Exception e) {
        Long stopTimeMs = System.currentTimeMillis();
        long resultTimeMs = stopTimeMs - status.getStartTimeMs();
        TraceId traceId = status.getTraceId();
        if (e == null) {
            log.info("[{}] {}{} time={}ms", traceId.getId(),
                    addSpace(COMPLETE_PREFIX, traceId.getLevel()), status.getMessage(),
                    resultTimeMs);
        } else {
            log.info("[{}] {}{} time={}ms ex={}", traceId.getId(),
                    addSpace(EX_PREFIX, traceId.getLevel()), status.getMessage(), resultTimeMs,
                    e.toString());
        }
        releaseTraceId();
    }

    private void syncTraceId() {
        TraceId traceId = traceIdHolder.get();
        if (traceId == null) {
            traceIdHolder.set(new TraceId());
        } else {
            traceIdHolder.set(traceId.createNextId());
        }
    }

    private void releaseTraceId() {
        TraceId traceId = traceIdHolder.get();
        if (traceId.isFirstLevel()) {
            traceIdHolder.remove();//destroy
        } else {
            traceIdHolder.set(traceId.createPreviousId());
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
- `traceIdHolder`가 필드에서 `ThreadLocal`로 변경되었다. 따라서 값을 저장할 때는 `set(...)`을
사용하고, 값을 조회할 때는 `get()`을 사용한다.
  
## ThreadLocal.remove()
- 추가로 쓰레드 로컬을 모두 사용하고 나면 꼭 `ThreadLocal.remove()`를 호출해서 쓰레드 로컬에
저장된 값을 제거해주어야 한다.
- 쉽게 이야기해서 다음의 마지막 로그를 출력하고 나면 쓰레드 로컬의 값을 제거해야 한다.
```text
[3f902f0b] hello1
[3f902f0b] |-->hello2
[3f902f0b] |<--hello2 time=2ms
[3f902f0b] hello1 time=6ms //end() -> releaseTraceId() -> level==0, ThreadLocal.remove() 호출
```
- 여기서는 `releaseTraceId()`를 통해 `level`이 점점 낮아져서 2 -> 1 -> 0이 되면 로그를 처음으로
호출한 부분으로 돌아온 것이다.
- 따라서 이 경우 연관된 로그 출력이 끝난 것이다. 이제 더 이상 `TraceId`값을 추적하지 않아도 된다.
- 그래서 `traceId.isFirstLevel()` (level == 0)인 경우 `ThreadLocal.remove()`를 호출해서 쓰레드 로컬에 저장된 값을 제거해준다.

## ThreadLocalTraceTest
```java
package hello.advanced.trace.logtrace;

import hello.advanced.trace.TraceStatus;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
class ThreadLocalLogTraceTest {
    ThreadLocalLogTrace trace = new ThreadLocalLogTrace();

    @Test
    void begin_end_level2() {
        TraceStatus status1 = trace.begin("hello1");
        TraceStatus status2 = trace.begin("hello2");
        trace.end(status2);
        trace.end(status1);
    }

    @Test
    void begin_exception_level2() {
        TraceStatus status1 = trace.begin("hello");
        TraceStatus status2 = trace.begin("hello2");
        trace.exception(status2, new IllegalStateException());
        trace.exception(status1, new IllegalStateException());
    }
}
```
## begin_end_level2() 실행 결과
```text
[3f902f0b] hello1
[3f902f0b] |-->hello2
[3f902f0b] |<--hello2 time=2ms
[3f902f0b] hello1 time=6ms
```
## begin_exception_level2() 실행 결과
```text
[3dd9e4f1] hello
[3dd9e4f1] |-->hello2
[3dd9e4f1] |<X-hello2 time=3ms ex=java.lang.IllegalStateException
[3dd9e4f1] hello time=8ms ex=java.lang.IllegalStateException
```
- 멀티쓰레드 상황에서 문제가 없는지는 애플리케이션에 `ThreadLocalLogTrace`를 적용해서 확인해보자.
