# 스프링 인터셉터 - 인증 체크
- 서블릿 필터에서 사용했던 인증 체크 기능을 스프링 인터셉터로 개발

## LoginCheckInterceptor
```java
package hello.login.web.interceptor;

import hello.login.web.SessionConst;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Slf4j
public class LoginCheckInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse
            response, Object handler) throws Exception {
        String requestURI = request.getRequestURI();
        log.info("인증 체크 인터셉터 실행 {}", requestURI);
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute(SessionConst.LOGIN_MEMBER)
                == null) {
            log.info("미인증 사용자 요청");
            //로그인으로 redirect
            response.sendRedirect("/login?redirectURL=" + requestURI);
            return false;
        }
        return true;
    }
}
```
- 인터셉터를 적용하거나 하지 않을 부분은 `addPathPatterns`와 `excludePathPatterns`에 작성하면 된다.
- 기본적으로 모든 경로에 해당 인터셉터를 적용하되 (`/**`), 홈 (`/`), 회원가입 (`/members/add`), 로그인 (`/login`),
리소스 조회 (`/css/**`), 오류 (`/error`)와 같은 부분은 로그인 체크 인터셉터를 적용하지 않는다.
- 서블릿 필터와 비교하면 매우 편리

### 정리
- 서블릿 필터와 스프링 인터셉터는 웹과 관련된 공통 관심사를 해결하기 위한 기술이다.
- 서블릿 필터와 비교해서 스프링 인터셉터가 개발자 입장에서 훨씬 간편, 특별한 문제가 없다면 인터셉터를 사용하는 것이 좋다.
