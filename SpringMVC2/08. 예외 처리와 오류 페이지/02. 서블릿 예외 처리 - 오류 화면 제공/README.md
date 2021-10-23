# 서블릿 예외 처리 - 오류 화면 제공
- 서블릿 컨테이너가 재공하는 기본 예와 처리 화면은 고객 친화적이지 않다.
- 서블릿이 제공하는 오류 화면 기능을 사용해본다.
- 서블릿은 `Exception` (예외)가 발생해서 서블릿 밖으로 전달되거나 또는 
`response.sendError()`가 호출되었을 때 각각의 상황에 맞춘 오류 처리 기능을 제공한다.
- 이 기능을 사용하면 친절한 오류 처리 화면을 준비해서 고객에게 보여줄 수 있다.
- 과거에는 `web.xml`이라는 파일에 다음과 같이 오류 화면을 등록했다.
```xml
<web-app>
    <error-page>
        <error-code>404</error-code>
        <location>/error-page/404.html</location>
    </error-page>
    <error-page>
        <error-code>500</error-code>
        <location>/error-page/500.html</location>
    </error-page>
    <error-page>
        <exception-type>java.lang.RuntimeException</exception-type>
        <location>/error-page/500.html</location>
    </error-page>
</web-app>
```
- 지금은 스프링 부트를 통해서 서블릿 컨테이너를 실행하기 때문에, 
  스프링 부트가 제공하는 기능을 사용해서 서블릿 오류 페이지에 등록하면 된다.
  
## 서블릿 오류 페이지 등록
```java
package hello.exception;

import org.springframework.boot.web.server.ConfigurableWebServerFactory;
import org.springframework.boot.web.server.ErrorPage;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class WebServerCustomizer implements
        WebServerFactoryCustomizer<ConfigurableWebServerFactory> {
    @Override
    public void customize(ConfigurableWebServerFactory factory) {
        ErrorPage errorPage404 = new ErrorPage(HttpStatus.NOT_FOUND, "/errorpage/404");
        ErrorPage errorPage500 = new
                ErrorPage(HttpStatus.INTERNAL_SERVER_ERROR, "/error-page/500");
        ErrorPage errorPageEx = new ErrorPage(RuntimeException.class, "/errorpage/500");
        factory.addErrorPages(errorPage404, errorPage500, errorPageEx);
    }
}
```
- `response.sendError(404)` : `errorPage404` 호출
- `response.sendError(500)` : `errorPage500` 호출
- `RuntimeException` 또는 그 자식 타입의 예외 : `errorPageEx` 호출

> 오류가 페이지는 예외를 다룰 때 해당 예외와 그 자식 타입의 오류를 함께 처리한다. 예를 들어서
> 위의 경우 `RuntimeException`은 물론이고 `Runtimeexception`의 자식도 함께 처리한다.

> 오류가 발생했을 때 처리할 수 있는 컨트롤러가 필요하다. 예를 들어서 `RuntimException` 예외가 발생하면
> `errorPageEx`에서 지정한 `/error-page/500`이 호출된다.
- 해당 오류를 처리할 컨트롤러
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
  @RequestMapping("/error-page/404")
  public String errorPage404(HttpServletRequest request, HttpServletResponse
          response) {
    log.info("errorPage 404");
    return "error-page/404";
  }

  @RequestMapping("/error-page/500")
  public String errorPage500(HttpServletRequest request, HttpServletResponse
          response) {
    log.info("errorPage 500");
    return "error-page/500";
  }
}
```
