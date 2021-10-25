# API 예외 처리 - HandlerExceptionResolver 활용
## 예외를 여기서 마무리하기
- 예외가 발생하면 WAS까지 예외가 던져지고, WAS에서 오류 페이지 정보를 찾아서 다시 `/error`를 호출하는 과정은
생각해보면 너무 복잡하다.
- `ExceptionResolver`를 활용하면 예외가 발생했을 때 이런 복잡한 과정 없이 여기에서 문제를 깔끔하게 해결할 수 있다.

## 예제
### UserException - 사용자 정의 예외 추가
```java
package hello.exception.exception;

public class UserException extends RuntimeException {
    public UserException() {
        super();
    }

    public UserException(String message) {
        super(message);
    }

    public UserException(String message, Throwable cause) {
        super(message, cause);
    }

    public UserException(Throwable cause) {
        super(cause);
    }

    protected UserException(String message, Throwable cause, boolean
            enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
```

## ApiExceptionController - 예외 추가
```java
package hello.exception.api;

import hello.exception.exception.UserException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class ApiExceptionController {
    @GetMapping("/api/members/{id}")
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
```
- http://localhost:8080/api/members/user-ex 호출 시 `UserException`이 발생하도록 해두었다.
- 이 예외를 처리하는 `UserHandlerExceptionResolver`
### UserHandlerExceptionResolver
```java
package hello.exception.resolver;

import com.fasterxml.jackson.databind.ObjectMapper;
import hello.exception.exception.UserException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class UserHandlerExceptionResolver implements HandlerExceptionResolver {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public ModelAndView resolveException(HttpServletRequest request,
                                         HttpServletResponse response, Object handler, Exception ex) {
        try {
            if (ex instanceof UserException) {
                log.info("UserException resolver to 400");
                String acceptHeader = request.getHeader("accept");
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                if ("application/json".equals(acceptHeader)) {
                    Map<String, Object> errorResult = new HashMap<>();
                    errorResult.put("ex", ex.getClass());
                    errorResult.put("message", ex.getMessage());
                    String result =
                            objectMapper.writeValueAsString(errorResult);
                    response.setContentType("application/json");
                    response.setCharacterEncoding("utf-8");
                    response.getWriter().write(result);
                    return new ModelAndView();
                } else {
                    //TEXT/HTML
                    return new ModelAndView("error/500");
                }
            }
        } catch (IOException e) {
            log.error("resolver ex", e);
        }
        return null;
    }
}
```
- HTTP 요청 헤더의 `ACCEPT` 값이 `application/json이면 JSON으로 오류를 내려주고, 
  그 외 경우에는 error/500에 있는 HTML 오류 페이지를 보여준다.
  
### WebConfig에 UserHandlerExceptionResolver 추가
```java
@Override
public void extendHandlerExceptionResolvers(List<HandlerExceptionResolver>
resolvers){
        resolvers.add(new MyHandlerExceptionResolver());
        resolvers.add(new UserHandlerExceptionResolver());
}
```
Postman 실행<br>
`http://localhost:8080/api/members/user-ex`

`ACCEPT` : `application/json`
```json
{
 "ex": "hello.exception.exception.UserException",
 "message": "사용자 오류"
}
```
`ACCEPT` : `text/html`
```html
<!DOCTYPE HTML>
<html>
...
</html>
```

### 정리
- `ExceptionResolver`를 사용하면 컨트롤러에서 예외가 발생해도 `ExceptionResolver`에서 예외를 처리해버린다.
- 따라서 예외가 발생해도 서블릿 컨테이너까지 예외가 전달되지 않고, 스프링 MVC에서 예외 처리는 끝이 난다.
- 결과적으로 WAS 입장에서는 정상 처리가 된 것이다. 이렇게 예외를 이 곳에서 모두 처리할 수 있다는 것이 핵심이다.
- 서블릿 컨테이너까지 예외가 올라가면 복잡하고 지저분하게 추가 프로세스가 실행된다. 반면에 `ExceptionResolver`를 사용하면 예외처리가 상당히 깔끔해진다.
- 그런데 직접 `ExceptionResolver`를 구현하려고 하니 상당히 복잡하다. -> 스프링이 제공하는 `ExceptionResolver`
