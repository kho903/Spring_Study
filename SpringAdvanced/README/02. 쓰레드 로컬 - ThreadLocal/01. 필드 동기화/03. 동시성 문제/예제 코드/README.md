# 동시성 문제 - 예제 코드
- 동시성 문제가 어떻게 발생하는지 단순화
- 테스트에서 lombok 사용을 위해 build.gradle에 추가
```groovy
dependencies {
     ...
     //테스트에서 lombok 사용
     testCompileOnly 'org.projectlombok:lombok'
     testAnnotationProcessor 'org.projectlombok:lombok'
}
```
- 이렇게 해야 테스트코드에서 `@Slf4j`와 같은 애노테이션 동작

## FieldService
```java
package hello.advanced.trace.threadlocal.code;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FieldService {
    private String nameStore;

    public String logic(String name) {
        log.info("저장 name={} -> nameStore={}", name, nameStore);
        nameStore = name;
        sleep(1000);
        log.info("조회 nameStore={}", nameStore);
        return nameStore;
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
- 매우 단순한 로직. 파라미터로 넘어온 `name`을 필드인 `nameStore`에 저장한다.
그리고 1초간 쉰 다음 필드에 저장된 `nameStore`를 반환한다.

## FieldServiceTest
```java
package hello.advanced.trace.threadlocal;

import hello.advanced.trace.threadlocal.code.FieldService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
public class FieldServiceTest {
    private FieldService fieldService = new FieldService();

    @Test
    void field() {
        log.info("main start");
        Runnable userA = () -> {
            fieldService.logic("userA");
        };
        Runnable userB = () -> {
            fieldService.logic("userB");
        };
        Thread threadA = new Thread(userA);
        threadA.setName("thread-A");
        Thread threadB = new Thread(userB);
        threadB.setName("thread-B");
        threadA.start(); //A실행
        sleep(2000); //동시성 문제 발생X
// sleep(100); //동시성 문제 발생O
        threadB.start(); //B실행
        sleep(3000); //메인 쓰레드 종료 대기
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
## 순서대로 실행
- `sleep(2000)`을 설정해서 `thread-A`의 실행이 끝나고 나서 `thread-B`가 실행되도록 해보자.
- 참고로 `FieldService.logic()`메서드는 내부에 `sleep(1000)`으로 1초의 지연이 있다.
- 따라서 1초 이후에 호출하면 순서대로 실행할 수 있다. 여기서는 넉넉하게 2초 설정.
- `sleep(2000);`
### 실행 결과
```text
[Test worker] main start
[Thread-A] 저장 name=userA -> nameStore=null
[Thread-A] 조회 nameStore=userA
[Thread-B] 저장 name=userB -> nameStore=userA
[Thread-B] 조회 nameStore=userB
[Test worker] main exit
```
실행 결과를 보면 문제가 없다.
- `Thread-A`는 `userA`를 `nameStore`에 저장했다.
- `Thread-A`는 `userA`를 `nameStore`에서 조회했다.
- `Thread-B`는 `userB`를 `nameStore`에 저장했다.
- `Thread-B`는 `userB`를 `nameStore`에서 조회했다.

## 동시성 문제 발생 코드
- 이번에는 `sleep(100)`을 설정해서 `thread-A`의 작업이 끝나기 전에 `thread-B`가 실행되도록 해보자.
- 참고로 `FieldService.logic()`메서드는 내부에 `sleep(1000)`으로 1초의 지연이 있다.
- 따라서 1초 이후에 호출하면 순서대로 실행할 수 있다.
- 다음에 설정한 100(ms)는 0.1초 이기 때문에 `threa-A`의 작업이 끝나기 전에 `thread-B`가 실행된다.
```java
//sleep(2000); //동시성 문제 발생X
sleep(100); //동시성 문제 발생O
```

## 실행결과
```text
[Test worker] main start
[Thread-A] 저장 name=userA -> nameStore=null
[Thread-B] 저장 name=userB -> nameStore=userA
[Thread-A] 조회 nameStore=userB
[Thread-B] 조회 nameStore=userB
[Test worker] main exit
```
- 저장하는 부분은 문제 X, 조회하는 부분에서 문제 발생
- 먼저 `thread-A`가 `userA` 값을 `nameStore`에 보관한다.
- 0.1초 이후에 `thread-B`가 `userB`의 값을 `nameStore`에 보관한다. 기존에 `nameStore`에 
  보관되어 있던 `userA` 값은 제거되고 `userB` 값이 저장된다.
- `thread-A`의 호출이 끝나면서 `nameStore`의 결과를 반환 받는데, 이 때 `nameStore`는 앞의
2번에서 `userB`의 값으로 대체되었다. 따라서 기대했던 `userA`의 값이 아니라 `userB`의 값이 반환된다.
- `thread-B`의 호출이 끝나면서 `nameStore`의 결과인 `userB`를 반환받는다.

### 정리하면 다음과 같다.
1. `Thread-A`는 `userA`를 `nameStore`에 저장했다.
2. `Thread-B`는 `userB`를 `nameStore`에 저장했다.
3. `Thread-A`는 `userB`를 `nameStore`에서 조회했다.
4. `Thread-B`는 `userB`를 `nameStore`에서 조회했다.

## 동시성 문제
- 결과적으로 `Thread-A`입장에서는 저장한 데이터와 조회한 데이터가 다른 문제가 발생한다.
- 이처럼 여러 쓰레드가 동시에 같은 인스턴스의 필드에 접근해야 하기 때문에 트래픽이 적은 상황에서는 
확률상 잘 나타나지 않고, 트래픽이 점점 많아질 수록 자주 발생한다.
- 특히 스프링 빈처럼 싱글톤 객체의 필드를 변경하며 사용할 때 이러한 동시성 문제를 조심해야 한다.

### 참고
- 이런 동시성 문제는 지역 변수에서는 발생하지 않는다. 지역 변수는 쓰레드마다 각각 다른 메모리 영역이 할당된다.
- 동시성 문제가 발생하는 곳은 같은 인스턴스의 필드(주로 싱글톤에서 발생), 또는 static 같은 공용 필드에
접근할 때 발생한다.
- 동시성 문제는 값을 읽기만 하면 발생하지 않는다. 어디선가 값을 변경하기 때문에 발생한다.
- 그렇다면 지금처럼 싱글톤 객체의 필드를 사용하면서 동시성 문제를 해결하려면 어떻게 해야 할까
- 이럴 때 사용하는 것이 바로 쓰레드 로컬이다.

