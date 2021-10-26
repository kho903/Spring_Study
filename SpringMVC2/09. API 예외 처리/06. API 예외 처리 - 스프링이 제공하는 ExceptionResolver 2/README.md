# API 예외 처리 - HandlerExceptionResolver 2
- `DefaultHandlerExceptionResolver`는 스프링 내부에서 발생하는 스프링 예외를 해결한다.
- 대표적으로 파라미터 바인딩 시점에 타입이 맞지 않으면 내부에서 `TypeMismatchException`이 발생하는데, 
이 경우 예외가 발생했기 때문에 그냥 두면 서블릿 컨테이너까지 오류가 올라가고, 결과적으로 500 오류가 발생한다.
- 그런데 파라미터 바인딩을 대부분 클라이언트가 HTTP 요청 정보를 잘못 호출해서 발생하는 문제이다.
HTTP 에서는 이런 경우 HTTP 상태 코드 400을 사용하도록 되어 있다.
- `DefaultHandlerExceptionResolver`는 이것을 500 오류가 아니라 HTTP 상태 코드 400 오류로 변경한다.
- 스프링 내부 오류를 어떻게 처리할 지 수 많은 내용이 정의되어 있다.

## 코드 확인
- `DefaultHandlerExceptResolver.handTypeMismatch`를 보면 다음과 같은 코드를 확인할 수 있다.
- `response.sendError(HttpServletResponse.SC_BAD_REQUEST`(400)
- 결국 `response.sendError()`를 통해서 문제를 해결한다.

## APIException - 추가
```java
@GetMapping("/api/default-handler-ex")
public String defaultException(@RequestParam Integer data){
        return"ok";
}
```
- `Integer data`에 문자를 입력하면 내부에서 `TypeMismatchException`이 발생한다.

### 실행
- http:localhost:8080/api/default-handler-ex?data=hello?message=
```json
{
 "status": 400,
 "error": "Bad Request",
 "exception":
"org.springframework.web.method.annotation.MethodArgumentTypeMismatchException"
,
 "message": "Failed to convert value of type 'java.lang.String' to required type 'java.lang.Integer'; nested exception is java.lang.NumberFormatException: For input string: \"hello\"",
 "path": "/api/default-handler-ex"
}
```
- 실행 결과를 보면 HTTP 상태 코드가 400인 것을 확인할 수 있다.

### 정리
`ExceptionResolver`
1. `ExceptionHandlerExcpetionResolver`
2. `ResponseStatusExceptionResolver` -> HTTP 응답 코드 변경
3. `DefaultHandlerExceptionResolver` -> 스프링 내부 예외 처리

- `HandlerExceptionResolver`를 사용해 HTTP 상태 코드 변경, 스프링 내부 예외의 상태코드 변경 기능도 가능
- but, 직접 사용은 복잡. API 오류 응답의 경우 `response`에 직접 데이터를 넣어야 해서 매우 불편하고 번거롭다.
- `ModelAndView`를 반환해야 하는 것도 API에는 잘 맞지 않는다.
- 스프링은 이 문제를 해결하기 위해 `@ExceptionHandler`라는 매우 혁신적인 예외 처리 기능을 제공한다. -> `ExceptionHandlerExceptionResolver`