# 서블릿 예외 처리 - 시작
- 스프링이 아닌 순수 서블릿 컨테이너의 예외처리
## 서블릿은 다음 2가지 방식으로 예외처리를 지원한다.
- `Exception` (예외)
- `response.sendError(HTTP 상태 코드, 오류 메시지)`

## Exception (예외)
### 자바 직접 실행
- 자바의 메인 메서드를 직접 실행하는 경우 `main`이라는 이름의 쓰레드가 실행된다.
- 실행 도중에 예외를 잡지 못하고 처음 실행한 `main()` 메서드를 넘어서 예외가 던져지면, 예외정보를 남기고 해당 쓰레드는 종료된다.

### 웹 애플리케이션
- 웹 애플리케이션은 사용자 요청별로 별도의 쓰레드가 할당되고, 서블릿 컨테이너 안에서 실행된다.
- 애플리케이션에서 예외가 발생했는데, 어디선가 try~catch로 예외를 잡아서 처리하면 아무런 문제가 없다.
- 그런데 만약에 애플리케이션에서 예외를 잡지 못하고, 서블릿 밖으로까지 예외가 전달된다면?<br>
`WAS(여기까지 전파) <- 필터 <- 서블릿 <- 인터셉터 <- 컨트롤러(예외발생)`
- 결굴 톰캣같은 WAS까지 예외가 전달된다. WAS는 예외가 올라오면 어떻게 처리해야 할까?
### 참고
- 스프링 부트가 제공하는 기본예외페이지 off

`server.error.whitelabel.enabled=false`

## ServletExController - 서블릿 예외 컨트롤러
```java
package hello.exception.servlet;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Slf4j
@Controller
public class ServletExController {
    @GetMapping("/error-ex")
    public void errorEx() {
        throw new RuntimeException("예외 발생!");
    }
}
```
- 실행해보면 다음처럼 tomcat이 기본으로 제공하는 오류 화면을 볼 수 있다.

`HTTP Status 500 – Internal Server Error`

- 웹 브라우저에서 개발자 모드로 확인해보면 HTTP 상태 코드가 500으로 보인다.
- `Exception`의 경우 서버 내부에서 처리할 수 없는 오류가 발생한 것으로 생각해서 HTTP 상태 코드 500을 반환한다.
- 존재하지 않는 주소 호출시

`HTTP Status 404 – Not Found`
- 톰캣이 기본으로 제공하는 404 오류 화면을 볼 수 있다.

## response.sendError(HTTP 상태 코드, 오류 메시지)
- 오류가 발생했을 때 `HttpServletResponse`가 제공하는 `sendError`라는 메서드를 사용해도 된다.
- 이것을 호출한다고 당장 예외가 발생하는 것은 아니지만, 서블릿 컨테이너에게 오류가 발생했다는 점을 전달할 수 있다.
- 이 메서드를 사용하면 HTTP 상태 코드와 오류 메시지도 추가할 수 있다.
- response.sendError(HTTP 상태 코드)
- response.sendError(HTTP 상태 코드, 오류 메시지)

### ServletExController - 추기
```java
@GetMapping("/error-404")
public void error404(HttpServletResponse response)throws IOException{
        response.sendError(404,"404 오류!");
}
@GetMapping("/error-500")
public void error500(HttpServletResponse response)throws IOException{
        response.sendError(500);
}
```
## sendError 흐름
`WAS(sendError 호출 기록 확인) <- 필터 <- 서블릿 <- 인터셉터 <- 컨트롤러(response.sendError())`
- `response.sendError()`를 호출하면 `response` 내부에는 오류가 발생했다는 상태를 저장해둔다.
- 그리고 서블릿 컨테이너는 고객에게 응답 전에 `response`에 `sendError()`가 호출되었는지 확인한다.
- 그리고 호출되었다면 설정한 오류 코드에 맞추어 기본 오류 페이지를 보여준다.
```text
HTTP Status 404 – Bad Request
HTTP Status 500 – Internal Server Error
```

### 정리
- 서블릿 컨테이너가 제공하는 기본 예외 처리 화면은 사용자가 보기에 불편하다. 의미 있는 오류화면 제공 필요.