# 서블릿 필터 - 요청 로그
- 필터가 정말 수문장 역할을 잘 하는 지 확인하기 위해 가장 단순한 필터인
모든 요청을 로그로 남기는 필터 개발 / 적용
  
## LogFilter - 로그 필터
```java
package hello.login.web.filter;

import lombok.extern.slf4j.Slf4j;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.UUID;

@Slf4j
public class LogFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        log.info("log filter init");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String requestURI = httpRequest.getRequestURI();
        String uuid = UUID.randomUUID().toString();
        try {
            log.info("REQUEST [{}][{}]", uuid, requestURI);
            chain.doFilter(request, response);
        } catch (Exception e) {
            throw e;
        } finally {
            log.info("RESPONSE [{}][{}]", uuid, requestURI);
        }
    }

    @Override
    public void destroy() {
        log.info("log filter destroy");
    }
}
```
- `public class LogFilter implements Filter {}`
    - 필터를 사용하려면 필터 인터페이스를 구현해야 한다.
- `doFilter(ServletRequest request, ServletResponse response, FilterChain chain)`
    - HTTP 요청이 오면 `doFilter`가 호출된다.
    - `ServletRequest request`는 HTTP 요청이 아닌 경우까지 고려해서 만든 인터페이스이다.
    HTTP를 사용하면 `HttpServletRequest httpRequest = (HttpServletRequest) request;`와 같이 다운캐스팅 하면 된다.
- `String uuid = UUID.randomUUID().toString();`
    - HTTP 요청을 구분하기 위해 요청당 임의의 `uuid`를 생성해둔다.
- `log.info("REQUEST [{}][{}]", uuid, requestURI);`
    - `uuid`와 `requestURI`를 출력한다.
- `chain.doFilter(request, response);`
    - 이 부분이 가장 중요하다. 다음 필터가 있으면 필터를 호출하고, 필터가 없으면 서블릿을 호출한다.
    만약 이 로직을 호출하지 않으면 다음 단계로 진행되지 않는다.
      
## WebConfig - 필터 설정
```java
package hello.login;

import hello.login.web.filter.LogFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.Filter;

@Configuration
public class WebConfig {
    @Bean
    public FilterRegistrationBean logFilter() {
        FilterRegistrationBean<Filter> filterRegistrationBean = new
                FilterRegistrationBean<>();
        filterRegistrationBean.setFilter(new LogFilter());
        filterRegistrationBean.setOrder(1);
        filterRegistrationBean.addUrlPatterns("/*");
        return filterRegistrationBean;
    }
}
```
필터를 등록하는 방법은 여러가지가 있지만, 스프링 부트를 사용한다면 `FilterRegistrationBean`을 사용해서 등록하면 된다.
- `setFilter(new LogFilter())` : 등록할 필터를 지정한다.
- `setOrder(1)` : 필터는 체인으로 동작한다. 따라서 순서가 필요하다. 낮을수록 먼저 동작한다.
- `addUrlPatterns("/*")` : 필터를 적용할 URL 패턴을 지정한다. 한 번에 여러 패턴을 지정할 수 있다.

### 참고
- URL 패턴에 대한 룰은 필터도 서블릿과 동일하다. 자세한 내용은 서블릿 URL 패턴으로 검색 가능

### 참고
- `@ServletComponentScan`, `@WebFilter(filterName = "logFilter", urlPatterns = "/*")`로
필터 등록이 가능하지만 필터 순서 조절이 안된다. 따라서 `FilterRegistrationBean`을 사용하자.

### 실행 로그
```text
hello.login.web.filter.LogFilter: REQUEST [0a3319r2-
cq70-1kp2-90d1-241oqe5033bf][/items]
hello.login.web.filter.LogFilter: RESPONSE [0a3319r2-
cq70-1kp2-90d1-241oqe5033bf][/items]
```
- 필터를 등록할 때 `urlPattern`을 `/*`로 등록했기 때문에 모든 요청에 해당 필터가 적용된다.

### 참고
- 실무에서 HTTP 요청 시 같은 요청의 로그에 모두 같은 식별자를 자동으로 남기는 방법은 logback mdc로 검색 가능