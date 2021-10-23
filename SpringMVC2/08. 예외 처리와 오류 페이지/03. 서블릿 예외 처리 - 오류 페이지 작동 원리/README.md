# 서블릿 예외 처리 - 오류 페이지 작동 원리
- 서블릿은 `Exception` (예외)가 발생해서 서블릿 밖으로 전달되거나 또는
`response.sendError()`가 호출되었을 때 설정된 오류 페이지를 찾는다.
  
## 예외 발생 흐름
```text
WAS(여기까지 전파) <- 필터 <- 서블릿 <- 인터셉터 <- 컨트롤러 (예외 발생)
```

## sendError 흐름
```text
WAS (sendError 호출 기록 확인) <- 필터 <- 서블릿 <- 인터셉터 <- 컨트롤러 (response.sendError())
```
- WAS는 해당 예외르ㅜㄹ 처리하는 오류 페이지 정보를 확인한다.<br>
`new ErrorPage(RuntimeException.class, "/error-page/500")`
- 예를 들어서 `RuntimeException`예외가 WAS까지 전달되면, WAS는 오류 페이지 정보를 확인한다. 확인해보니
`RuntimeException`의 오류 페이지로 `/error-page/500`이 지정되어 있다.
WAS는 오류 페이지를 출력하기 위해 `/error-page/500`을 다시 요청한다ㅣ,

## 오류 페이지 요청 흐름
```text
WAS `/error-page/500` 다시 요청 -> 필터 -> 서블릿 -> 인터셉터 -> 컨트롤러 (/error-page/500) -> View
```
## 예외 발생과 오류 페이지 요청 흐름
```text
1. WAS (여기까지 전파) <- 필터 <- 서블릿 <- 인터셉터 <- 컨트롤러 (예외 발생)
2. WAS `/error-page/500` 다시 요청 -> 필터 -> 서블릿 -> 인터셉터 -> 컨트롤러 (/error-page/500) -> View
```
- 중요한 점은 웹 브라우저 (클라이언트)는 서버 내부에서 이런 일이 일어나는지 전혀 모른다는 점이다. 오직 서버 내부에서 오류 페이지를 찾기 위해 추가적인 호출을 한다.
- 정리하면
1. 예외가 발생해서 WAS까지 전파된다.
2. WAS는 오류 페이지 경로를 찾아서 내부에서 오류 페이지를 호출한다. 이 때 오류 페이지 경로로 필터, 서블릿, 인터셉터, 컨트롤러가 모두 다시 호출된다.

## 오류 정보 추가
- WAS는 오류 페이지를 단순히 다시 요청만 하는 것이 아니라, 오류 정보를 `request`의 `attribute`에 추가해서 넘겨준다.
- 필요하면 오류 페이지에서 이렇게 전달된 오류 정보를 사용할 수 있다.

### ErrorPageController - 오류 출력
```java
package hello.exception.servlet;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@Controller
public class ErrorPageController {
    //RequestDispatcher 상수로 정의되어 있음
    public static final String ERROR_EXCEPTION =
            "javax.servlet.error.exception";
    public static final String ERROR_EXCEPTION_TYPE =
            "javax.servlet.error.exception_type";
    public static final String ERROR_MESSAGE = "javax.servlet.error.message";
    public static final String ERROR_REQUEST_URI =
            "javax.servlet.error.request_uri";
    public static final String ERROR_SERVLET_NAME =
            "javax.servlet.error.servlet_name";
    public static final String ERROR_STATUS_CODE =
            "javax.servlet.error.status_code";

    @RequestMapping("/error-page/404")
    public String errorPage404(HttpServletRequest request, HttpServletResponse
            response) {
        log.info("errorPage 404");
        printErrorInfo(request);
        return "error-page/404";
    }

    @RequestMapping("/error-page/500")
    public String errorPage500(HttpServletRequest request, HttpServletResponse
            response) {
        log.info("errorPage 500");
        printErrorInfo(request);
        return "error-page/500";
    }

    private void printErrorInfo(HttpServletRequest request) {
        log.info("ERROR_EXCEPTION: ex=",
                request.getAttribute(ERROR_EXCEPTION));
        log.info("ERROR_EXCEPTION_TYPE: {}",
                request.getAttribute(ERROR_EXCEPTION_TYPE));
        log.info("ERROR_MESSAGE: {}", request.getAttribute(ERROR_MESSAGE)); //
        ex의 경우 NestedServletException 스프링이 한번 감싸서 반환
        log.info("ERROR_REQUEST_URI: {}",
                request.getAttribute(ERROR_REQUEST_URI));
        log.info("ERROR_SERVLET_NAME: {}",
                request.getAttribute(ERROR_SERVLET_NAME));
        log.info("ERROR_STATUS_CODE: {}",
                request.getAttribute(ERROR_STATUS_CODE));
        log.info("dispatchType={}", request.getDispatcherType());
    }
}
```
### request.attribute에 서버가 담아준 정보
- `javax.servlet.error.exception` : 예외
- `javax.servlet.error.exception_type` : 예외 타입
- `javax.servlet.error.message` : 오류 메시지
- `javax.servlet.error.request_uri` : 클라이언트 요청 URI
- `javax.servlet.error.servlet_name` : 오류가 발생한 서블릿 이름
- `javax.servlet.error.status_code` : HTTP 상태 코드

