# API 예외 처리 - @ExceptionHandler
## HTML 화면 오류 vs API 오류
- 웹 브라우저에 HTML 화면을 제공할 때는 오류가 발생하면 `BasicErrorController`를 사용하는게 편하다.
- 이 때는 단순히 5xx, 4xx 관련된 오류 화면을 보여주면 된다. `BasicErrorController`는 이런 매커니즘을 모두 구현해두었다.
- 그런데 API는 각 시스템 마다 응답의 모양도 다르고, 스펙도 모두 다르다.
- 예외 상황에 단순히 오류 화면을 보여주는 것이 아니라, 예외에 따라서 각각 다른 데이터를 출력해야 할 수도 있다.
- 그리고 같은 예외라고 해도 어떤 컨트롤러에서 발생했는가에 따라서 다른 다른 예외 응답을 내려주어야 할 수 있다.
- 한마디로 매우 세밀한 제어가 필요하다.

## API 예외처리의 어려운 점
- `HandlerExceptionResolver`를 떠올려 보면 `ModelAndView`를 반환해야 했다. 이것은 API 응답에는 필요하지 않다.
- API 응답을 위해서 `HttpServletResponse`에 직접 응답 데이터를 넣어주었다. 이것은 매우 불편하다.
스프링 컨트롤러에 비유하면 마치 과거 서블릿을 사용하던 시절로 돌아간 것 같다.
- 특정 컨트롤러에서만 발생하는 예외를 별도로 처리하기 어렵다. 예를 들어서 회원을 처리하는 컨트롤러에서 발생하는 
`RuntimeException`예외와 상품을 관리하는 컨트롤러에서 발생하는 동일한 `RuntimeException`예외를 서로 다른 방식으로 처리하고 싶다면?
  
## @ExceptionHandler
- 스프링은 API 예외 처리 문제를 해결하기 위해 `@ExceptionHandler`라는 애노테이션을 사용하는 매우 편리한 예외 처리 기능을 제공하는데, 
이것이 바로 `ExceptionHandlerExceptionResolver`이다. 스프링은 `ExceptionHandlerExceptionResolver`를 기본으로 제공하고,
기본으로 제공하는 `ExceptionResolver`중에 우선순위도 가장 높다. 실무에서 API 예외 처리는 대부분 이 기능을 사용한다.

### ErrorResult
```java
package hello.exception.exhandler;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ErrorResult {
    private String code;
    private String message;
}
```
- 예외가 발생했을 때 API 응답으로 사용하는 객체 정의

### ApiExceptionV2Controller
```java
package hello.exception.exhandler;

import hello.exception.exception.UserException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
public class ApiExceptionV2Controller {
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IllegalArgumentException.class)
    public ErrorResult illegalExHandle(IllegalArgumentException e) {
        log.error("[exceptionHandle] ex", e);
        return new ErrorResult("BAD", e.getMessage());
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResult> userExHandle(UserException e) {
        log.error("[exceptionHandle] ex", e);
        ErrorResult errorResult = new ErrorResult("USER-EX", e.getMessage());
        return new ResponseEntity<>(errorResult, HttpStatus.BAD_REQUEST);
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler
    public ErrorResult exHandle(Exception e) {
        log.error("[exceptionHandle] ex", e);
        return new ErrorResult("EX", "내부 오류");
    }

    @GetMapping("/api2/members/{id}")
    public MemberDto getMember(@PathVariable("id") String id) {
        if (id.equals("ex")) {
            throw new RuntimeException("잘못된 사용자");
        }
        if (id.equals("bad")) {
            throw new IllegalArgumentException("잘못된 입력 값");
        }
        if (id.equals("user-ex")) {
            throw new UserException("사용자 오류");
        }
        return new MemberDto(id, "hello " + id);
    }

    @Data
    @AllArgsConstructor
    static class MemberDto {
        private String memberId;
        private String name;
    }
}
        }
```

### @ExceptionHandler 예외 처리 방법
- `@ExceptionHandler`애노테이션을 선언하고, 해당 컨트롤러에서 처리하고 싶은 예외를 지정해주면 된다.
- 해당 컨트롤러에서 예외가 발생하면 이 메서드가 호출된다. 참고로 지정한 예외 또는 그 예외의 자식 클래스는 모두 잡을 수 있다.
- 다음 예제는 `IllegalArgumentException` 또는 그 하위 자식 클래스를 모두 처리할 수 있다.
```java
@ExceptionHandler(IllegalArgumentException.class)
public ErrorResult illegalExHandle(IllegalArgumentException e){
        log.error("[exceptionHandle] ex",e);
        return new ErrorResult("BAD",e.getMessage());
}
```

### 우선순위
- 스프링의 우선순위는 항상 자세한 것이 우선권을 가진다. 예를 들어서 부모, 자식 클래스가 있고 다음과 같이 예외가 처리된다.
```java
@ExceptionHandler(부모예외.class)
public String 부모예외처리()(부모예외 e){}
@ExceptionHandler(자식예외.class)
public String 자식예외처리()(자식예외 e){}
```
- `@ExceptionHandler`에 지정한 부모 클래스는 자식 클래스까지 처리할 수 있다.
- 따라서 `자식예외`가 발생하면 `부모예외처리()`, `자식예외처리()` 둘 다 호출대상이 된다.
- 그런데 둘 중 더 자세한 것이 우선권을 가지므로 `자식예외처리()`가 호출된다.
- 물론 `부모예외`가 호출되면 `부모예외처리()`만 호출대상이 되므로 `부모예외처리()`가 호출된다.

### 다양한 예외
- 다음과 같이 다양한 예외를 한 번에 처리할 수 있다.
```java
@ExceptionHandler({AException.class, BException.class})
public String ex(Exception e){
        log.info("exception e",e);
}
```

### 예외 생략
- `@ExceptionHandler`에 예외를 생략할 수 있다. 생략하면 메서드 파라미터의 예외가 지정된다.
```java
@ExceptionHandler
public ResponseEntity<ErrorResult> userExHandle(UserException e) {}
```

### 파라미터와 응답
- `@ExceptionHandler`에는 마치 스프링의 컨트롤러의 파라미터 응답처럼 다양한 파라미터와 응답을 지정할 수 있다.

### IllegalArgumentException 처리
```java
@ResponseStatus(HttpStatus.BAD_REQUEST)
@ExceptionHandler(IllegalArgumentException.class)
public ErrorResult illegalExHandle(IllegalArgumentException e) {
    log.error("[exceptionHandle] ex", e);
    return new ErrorResult("BAD", e.getMessage());
}
```
실행 흐름
- 컨트롤러를 호출한 결과 `IllegalArgumentException`예외가 컨트롤러 밖으로 던져진다.
- 예외가 발생했으므로 `ExceptionResolver`가 작동한다. 가장 우선순위가 높은
`ExceptionHandlerExceptionResolver`가 실행된다.
- `ExceptionHandlerExceptionResolver`는 해당 컨트롤러에 `IllegalArgumentException`을
처리할 수 있는 `@ExceptionHandler`가 있는 지 확인한다.
- `illegalExHandle()`을 실행한다. `@RestController`이므로 `illegalExHandle()`에도
`@ResponseBody`가 적용된다. 따라서 HTTP 컨버터가 사용되고, 응답이 다음과 같은 JSON으로 반환된다.
- `@ResponseStatus(HttpStatus.BAD_REQUEST)`를 지정했으므로 HTTP 상태코드 400으로 응답한다.

### 결과
```json
{
 "code": "BAD",
 "message": "잘못된 입력 값"
}
```

### UserException 처리
```java
@ExceptionHandler
public ResponseEntity<ErrorResult> userExHandle(UserException e){
    log.error("[exceptionHandle] ex",e);
    ErrorResult errorResult=new ErrorResult("USER-EX",e.getMessage());
    return new ResponseEntity<>(errorResult,HttpStatus.BAD_REQUEST);
}
```
- `@ExceptionHandler`에 예외를 지정하지 않으면 해당 메서드 파라미터 예외를 사용한다.
여기서는 `UserException`을 사용한다.
- `ResponseEntity`를 사용해서 HTTP 메시지 바디에 직접 응답한다. 물론 HTTP 컨버터가 사용된다.
- `ResponseEntity`를 사용하면 HTTP 응답 코드를 프로그래밍해서 동적으로 변경할 수 있다.
- 앞서 살펴본 `@ResponseStatus`는 애노테이션이므로 HTTP 응답 코드를 동적으로 변경할 수 없다.

### Exception
```java
@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
@ExceptionHandler
public ErrorResult exHandle(Exception e){
    log.error("[exceptionHandle] ex",e);
    return new ErrorResult("EX","내부 오류");
}
```
- `throw new RuntimeException("잘못된 사용자")`이 코드가 실행되면서, 컨트롤러 밖으로
`RuntimeException`이 던져진다.
- `RuntimeException`은 `Exception`의 자식 클래스이다. 따라서 이 메서드가 호출된다.
- `@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)`로 HTTP 상태 코드를 500으로 응답한다.

## 기타
### HTML 오류 화면
- 다음과 같이 `ModelAndView`를 사용해서 오류 화면(HTML)을 응답하는데 사용할 수도 있다.
```java
@ExceptionHandler(ViewException.class)
public ModelAndView ex(ViewException e){
    log.info("exception e",e);
    return new ModelAndView("error");
}
```
