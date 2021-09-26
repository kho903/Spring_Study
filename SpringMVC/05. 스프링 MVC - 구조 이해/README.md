# 스프링 MVC - 구조 이해
## 스프링 MVC 전체 구조
### DispatcherServlet 구조 살펴보기
`org.springframework.web.servlet.DispatcherServlet`
- 스프링 MVC도 프론트 컨트롤러 패턴으로 구현되어 있다.
- 스프링 MVC의 프론트 컨트롤러가 바로 디스패처 서블릿(DispatcherServlet)이다.
- 그리고 이 디스패처 서블릿이 바로 스프링 MVC의 핵심이다.

### DispatcherServlet 서블릿 등록
- `DispatcherServlet`도 부모 클래스에서 `HttpServlet`을 상속 받아서 사용하고, 서블릿으로 동작한다.
    - DispatcherServlet -> FrameworkServlet -> HttpServletBean -> HttpServlet
- 스프링 부트는 `DispatcherServlet`을 서블릿으로 자동으로 등록하면서 모든경로(urlPatterns="/")에 대해서 매핑한다.
    - 참고 : 더 자세한 경로가 우선순위가 높다. 그래서 기존에 등록한 서블릿도 함께 동작한다.

### 요청 흐름
- 서블릿이 호출되면 `HttpServlet`이 제공하는 `service()`가 호출된다.
- 스프링 MVC는 `DispatcherServlet`의 부모인 `FrameworkServlet`에서 `service()`를 오버라이드 해두었다.
- `FrameworkServlet.service()`을 시작으로 여러 메소드가 호출되면서 `DispatcherServlet.doDispatch()`가 호출된다.

### 동작 순서
1. 핸들러 조회 : 핸들러 매핑을 통해 요청 URL에 매핑된 핸들러(컨트롤러)를 조회한다.
2. 핸들러 어댑터 조회 : 핸들러를 실행할 수 있는 핸들러 어댑터를 조회한다.
3. 핸들러 어댑터 실행 : 핸들러 어댑터를 실행한다.
4. 핸들러 실행 : 핸들러 어댑터가 실제 핸들러를 실행한다.
5. ModelAndView 반환 : 핸들러 어댑터는 핸들러가 반환하는 정보를 ModelAndView로 변환해서 반환한다.
6. viewResolver 호출 : 뷰 리졸버를 찾고 실행한다.
    - JSP의 경우 `InternalResourceViewResolver`가 자동 등록되고, 사용된다.
7. View 반환 : 뷰 리졸버는 뷰의 논리 이름을 물리 이름으로 바꾸고, 렌더링 역할을 담당하는 뷰 객체를 반환한다.
    - JSP의 경우 `InternalResourceView(JstlView)`를 반환하는데, 내부에 `forward()`로직이 있다.
8. 뷰 렌더링 : 뷰를 통해서 뷰를 렌더링 한다.

### 인터페이스
- 스프링 MVC의 큰 강점은 `DispatcherServlet`코드의 변경 없이, 원하는 기능을 변경하거나 확장할 수 있다는 점이다.
- 대부분의 기능을 확장 가능할 수 있게 인터페이스로 제공한다.
- 인터페이스들만 구현해서 `DispatcherServlet`에 등록하면 자신의 컨트롤러를 만들 수도 있다.

### 주요 인터페이스 목록
- 핸들러 매핑 : `org.springframework.web.servlet.HandlerMapping`
- 핸들러 어댑터 : `org.springframework.web.servlet.HandlerAdapter`
- 뷰 리졸버 : `org.springframework.web.servlet.ViewResolver`
- 뷰 : `org.springframework.web.servlet.View`

## 핸들러 매핑과 핸들러 어댑터
### Controller 인터페이스
- 과거 : org.springframework.web.servlet.mvc.Controller
    - 스프링도 처음에는 딱딱한 형식의 컨트롤러를 제공했다.
> 참고 : Controller 인터페이스는 @Controller 애노테이션과는 전혀 다르다.

- HandlerMapping(핸들러 매핑)
    - 핸들러 매핑에서 이 컨트롤러를 찾을 수 있어야 한다.
    - 예) 스프링 빈의 이름으로 핸들러를 찾을 수 있는 핸들러 매핑이 필요하다.
- HandlerAdapter(핸들러 어댑터)
    - 핸들러 매핑을 통해서 찾은 핸들러를 실행할 수 있는 핸들러 어댑터가 필요하다.
    - 예) 'Controller' 인터페이스를 실행할 수 있는 핸들러 어댑터를 찾고 실행해야 한다.
> 스프링은 이미 필요한 핸들러 매핑과 핸들러 어댑터를 대부분 구현해두었다.
> 개발자가 직접 핸들러 매핑과 핸들러 어댑터를 만드는 일은 거의 없다.

### 스프링 부트가 자동으로 등록하는 핸들러 매핑과 핸들러 어댑터 
-  HandlerMapping
```
0 = RequestMappingHandlerMapping    : 애노테이션 기반의 컨트롤러인 @RequestMapping 에서 사용
1 = BeanNameUrlHanlderMapping       : 스프링 빈의 이름으로 핸들러를 찾는다.
```
- HadlerAdapter
```
0 = RequestMappingHandlerAdapter    : 애노테이션 기반의 컨트롤러인 @RequestMapping 에서 사용
1 = HttpRequestsHadlerAdapter       : HttpRequestHandler 처리
2 = SimpleControllerHandlerAdapter  : Controller 인터페이스 (애노테이션 X, 과거에 사용) 처리
```
- 핸들러 매핑도, 핸들러 어댑터도 모두 순서대로 찾고 만약 없으면 다음 순서로 넘어간다.

### 1. 핸들러 매핑으로 핸들러 조회
1. HandlerMapping 을 순서대로 실행해서, 핸들러를 찾는다.
2. 이 경우 빈 이름으로 핸들러를 찾아야 하기 때문에 이름 그대로 빈 이름으로 핸들러를 찾아주는
`BeanNameUrlHandlerMapping`이 실행에 성공하고 핸들러인 `OldController`를 반환한다.

### 2. 핸들러 어댑터 조회
1. `HandlerAdapter`의 `supports()`를 순서대로 호출한다.
2. `SimpleControllerHandlerAdapter`가 `Controller`인터페이스를 지원하므로 대상이 된다.

### 3. 핸들러 어댑터 실행
1. 디스패처 서블릿이 조회한 `SimpleControllerHandlerAdapter`를 실행하면서 핸들러 정보도 함께 넘겨준다.
2. `SimpleControllerHandlerAdapter`는 핸들러인 `OldController`를 내부에서 실행하고, 그 결과를 반환한다.

### 정리 - OldController 핸들러매핑, 어댑터
- `OldController`를 실행하면서 사용된 객체는 다음과 같다.
- `HandlerMapping = BeanNameUrlHandlerMapping`
- `HandlerAdapter = SimpleControllerHandlerAdapter`

### HttpRequestHandler
- 핸들러 매핑과 어댑터를 더 잘 이해하기 위해 Controller 인터페이스가 아닌 다른 핸들러를 알아보자.
- `HttpRequestHandler`핸들러(컨트롤러)는 서블릿과 가장 유사한 형태의 핸들러이다.

### 1. 핸들러 매핑으로 핸들러 조회
1. `HandlerMapping`을 순서대로 실행해서 핸들러를 찾는다.
2. 이 경우 빈 이름으로 핸들러를 찾아야 하기 때문에 이름 그대로 빈 이름으로 핸들러를 찾아주는
   `BeanNameUrlHandlerMapping`이 실행에 성공하고 핸들러인 `MyHttpRequestHandler`를 반환한다.
### 2. 핸들러 어댑터 조회
1. `HandlerAdapter`의 `supports()`를 순서대로 호출한다.
2. `HttpRequestHandlerAdapter`가 `HttpRequestHandler`인터페이스를 지원하므로 대상이 된다.
### 3. 핸들러 어댑터 실행
1. 디스패처 서블릿이 조회한 `HttpRequestHandlerAdapter`를 실행하면서 핸들러 정보도 함께 넘겨준다.
2. `HttpRequestHandlerAdpater`는 핸들러인 `MyHttpRequestHandler`를 내부에서 실행하고, 그 결과를 반환한다.

### 정리 - MyHttpRequest 핸들러매핑, 어댑터
- `MyHttpRequestHandler`를 실행하면서 사용된 객체는 다음과 같다.
- `HandlerMapping = BeanNameUrlHandlerMapping`
- `HandlerAdapter = HttpRequestHandlerAdapter`

### @RequestMapping
- 가장 우선순위가 높은 핸들러 매핑과 핸들러 어댑터는 `RequestMappingHandlerMapping`,
`RequestMappingHandlerAdapter` 이다.
- `@RequestMapping`의 앞 글자를 따서 만든 이름인데 이것이 바로 지금 스프링에서 주로 사용하는 애노테이션 기반의
컨트롤러를 지원하는 매핑과 어댑터이다. 실무에서는 99.99% 이 방식의 컨트롤러를 사용한다.
