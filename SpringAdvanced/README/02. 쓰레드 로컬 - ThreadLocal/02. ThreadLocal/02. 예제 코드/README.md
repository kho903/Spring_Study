# ThreadLocal - 예제 코드
## ThreadLocalService
- 테스트 코드에 생성
```java
package hello.advanced.trace.threadlocal.code;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ThreadLocalService {
    private ThreadLocal<String> nameStore = new ThreadLocal<>();

    public String logic(String name) {
        log.info("저장 name={} -> nameStore={}", name, nameStore.get());
        nameStore.set(name);
        sleep(1000);
        log.info("조회 nameStore={}", nameStore.get());
        return nameStore.get();
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
## ThreadLocal 사용법
- 값 저장 : `ThreadLocal.set(xxx)`
- 값 조회 : `ThreadLocal.get()`
- 값 제거 : `ThreadLocal.remove`

### 주의
- 해당 쓰레드가 쓰레드 로컬을 모두 사용하고 나면 `ThreadLocal.remove()`를 호출해서 쓰레드 로컬에 저장된
값을 제거해 주어야 한다.

## ThreadLocalServiceTest
```java
package hello.advanced.trace.threadlocal;

import hello.advanced.trace.threadlocal.code.ThreadLocalService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
public class ThreadLocalServiceTest {
    private ThreadLocalService service = new ThreadLocalService();

    @Test
    void threadLocal() {
        log.info("main start");
        Runnable userA = () -> {
            service.logic("userA");
        };
        Runnable userB = () -> {
            service.logic("userB");
        };
        Thread threadA = new Thread(userA);
        threadA.setName("thread-A");
        Thread threadB = new Thread(userB);
        threadB.setName("thread-B");
        threadA.start();
        sleep(100);
        threadB.start();
        sleep(2000);
        log.info("main exit");
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
- 실행 결과
```text
[Test worker] main start
[Thread-A] 저장 name=userA -> nameStore=null
[Thread-B] 저장 name=userB -> nameStore=null
[Thread-A] 조회 nameStore=userA
[Thread-B] 조회 nameStore=userB
[Test worker] main exit
```
- 쓰레드 로컬 덕분에 쓰레드마다 각각 별도의 데이터 저장소를 가지게 되었다.
- 결과적으로 동시성 문제도 해결되었다.
