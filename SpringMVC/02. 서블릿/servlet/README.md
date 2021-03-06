# HttpServletRequest
## 역할
- HTTP 요청 메시지를 개발자가 직접 파싱해서 사용해도 되지만, 매우 불편할 것이다.
- 서블릿은 개발자가 HTTP 요청 메시지를 편리하게 사용할 수 있도록 개발자 대신에 요청 메시지를 파싱한다.
- 그리고 그 결과를 HTTPServletRequest 객체에 담아서 제공한다.

## HTTP 요청 메시지
- START LINE
    - HTTP 메소드
    - URL
    - 쿼리 스트링
    - 스키마, 프로토콜
- 헤더
    - 헤더 조회
- 바디
    - form 파라미터 형식 조회
    - message body 데이터 직접 조회

- HttpServletRequest 객체는 추가로 여러가지 부가 기능도 함께 제공한다.

## 임시저장소 기능
- 해당 HTTP 요청이 시작부터 끝날때까지 유지되는 임시저장소 기능
  - 저장 : request.setAttribute(name, value)
  - 조회 : request.getAttribute(name)

## 세션 관리 기능
- request.getSession(create: true);

## 중요
- HttpServletRequest, HttpServletResponse를 사용할 때, 가장 중요한 점은 이 객체들이
HTTP 요청 메시지, HTTP 응답 메시지를 편리하게 사용하도록 도와주는 객체라는 점이다.
- 따라서 이 기능에 대해서 깊이 있는 이해를 하려면 HTTP 스펙이 제공하는 요청, 응답 메시지 자체를 이해해야 한다.

# HTTP 요청 데이터 - 개요
- HTTP 요청 메시지를 통해 클라이언트에서 서버로 데이터를 전달하는 방법은 주로 3가지가 있다.
## GET - 쿼리 파라미터
- /url?username=hello&age=20
- 메시지 바디 없이, URL의 쿼리 파라미터에 데이터를 포함해서 전달
- 예) 검색, 필터, 페이징 등에서 많이 사용하는 방식
## POST - HTML Form
- content-type : application/x-www-form-urlencoded
- 메시지 바디에 쿼리 파라미터 형식으로 전달 username=hello&age=20
- 예) 회원 가입, 상품 주문, HTML Form 사용
## HTPP message body에 데이터를 직접 담아서 요청
- HTTP API에서 주로 사용 JSON, XML, TEXT
- 데이터 형식은 주로 JSON 사용
  - POST, PUT, PATCH

# HTTP 요청 데이터 - GET 쿼리 파라미터
- 전달 데이터
  - username=hello
  - age=20
- 메시지 바디 없이, URL의 쿼리 파라미터를 사용해서 데이터를 전달하자
- 예) 검색, 필터, 페이징 등에서 많이 쓰이는 방식
- 쿼리파라미터는 URL에 다음과 같이 '?'를 시작으로 보낼 수 있다. 추가 파라미터는 '&'로 구분하면 된다.
- http://localhost:8080/request-param?username=hello&age=20
- 서버에서는 'HttpServletRequest'가 제공하는 메서드를 통해 쿼리 파라미터를 편리하게 조회할 수 있다.

### 복수 파라미터에서 단일 파라미터 조회
- username=hello&username=kim 과 같이 파라미터 이름은 하나인데 값이 중복될 때,
- 'request.getParameter()'는 하나의 파라미터 이름에 대해서 단 하나의 값만 있을 때 사용해야 한다.
- 중복일 때는 'request.getParameterValues()'를 사용해야 한다.
- 참고로 이렇게 중복일 때, 'request.getParameter()'를 사용하면 'request.getParameterValues()'의 첫번째 값을 반환한다.

# HTTP 요청 데이터 - POST HTML Form
- HTML Form을 이용해 클라이언트에서 서버로 데이터 전송
- 주로 회원 가입, 상품 주문 등에서 사용하는 방식이다.
## 특징
- content-type : 'application/x-www-form-urlencoded'
- 메시지 바디에 쿼리 파라미터 형식으로 데이터를 전달한다. 'username=hello&age=20'

## 정리
- 'application/x-www-form-urlencoded' 형식은 앞서 GET에서 살펴본 쿼리 파라미터 형식과 같다.
- 따라서 '쿼리 파라미터 조회 메서드를 그대로 사용'하면 된다.
- 클라이언트(웹 브라우저) 입장에서는 두 방식에 차이가 있지만, 서버 입장에서는 둘의 형식이 동일하므로,
- 'request.getParameter()'로 편리하게 구분없이 조회할 수 있다.
- 정리하면 'request.getParameter()'는 GET URL 쿼리 파라미터 형식도 지원하고, 
  POST HTML Form 형식도 둘 다 지원한다

## 참고
- content-type 은 HTTP 메시지 바디의 데이터 형식을 지정한다.
- 'GET URL 쿼리 파라미터 형식'으로 클라이언트에서 서버로 데이터를 전달할 때는 HTTP 메시지 바디를 사용하지 않기 때문에 content-type이 없다.
- 'POST HTML Form 형식'으로 데이터를 전달하면 HTTP 메시지 바디에 해당 데이터를 포함해서 보내기 때문에 바디에 포함된 데이터가 어떤 형식인지 content-type을 꼭 지정해야 한다.
- 이렇게 폼으로 데이터를 전송하는 형식을 'application/x-www-form-urlencoded'라 한다.

## HTTP 요청 데이터 - API 메시 바디 - 단순 텍스트
- HTTP message body에 데이터를 직접 담아서 요청
  - HTTP API 에서 주로 사용, JSON, XML, TEXT
  - 데이터 형식은 주로 JSON 사용
  - POST, PUT, PATCH
- HTTP 메시지 바디의 데이터를 InputStream을 사용해서 직접 읽을 수 있다.

## 참고
- inputStream은 byte 코드를 우리가 읽을 수 잇는 문자(String)로 보려면 문자표(Charset)를 지정해주어야 한다.

# HTTP 요청 데이터 - API 메시지 바디 - JSON
## JSON 형식 전송
- POST http://localhost:8080/request-body-json
- content-type: application/json
- message body : {"username": "hello", "age": 20}
- 결과 : 'messageBody = {"username": "hello", "age": 20}'

## 참고
- JSON 결과를 파싱해서 사용할 수 있는 자바 객체로 변환하려면 Jackson, Gson 같은 JSON 변환 라이브러리를 추가해서 사용해야 한다.
스프링 부트로 Spring MVC를 선택하면 기본으로 Jackson 라이브러리 ('ObjectMapper')를 함께 제공한다.
- HTML form 데이터도 메시지 바디를 통해 전송되므로 직접 읽을 수 있다. 하지만 편리한 파라미터 조회 기능
  ('request.getParameter(...)')을 이미 제동하기 때문에 파라미터 조회 기능을 사용하면 된다.

# HTTPServletResponse - 기본 사용법
## HTTPServletResponse의 역할
- HTTP 응답 메시지 생성
  - HTTP 응답코드 지정
  - 헤더 생성
  - 바디 생성
- 편의 기능 제공
  - Content-Type, 쿠키, Redirect

# Http 응답 데이터 - 단순 텍스트, HTML
## HTTP 응답 메시지는 주로 다음 내용을 담아서 전달한다.
- 단순 텍스트 응답
  - 앞에서 살펴봄 ('writer.println("ok");')
- HTML 응답
- HTTP API - MessageBody JSON 응답

# HTTP 응답 데이터 - API JSON
- HTTP 응답으로 JSON을 반환할 때는 content-type을 'application/json'로 지정해야 한다.
- Jackson 라이브러리가 제공하는 'objectMapper.writeValueAsString()'를 사용하면 객체를 JSON 문자로 변경할 수 있다.

## 참고
- 'application/json'은 스펙상 utf-8 형식을 사용하도록 정의되어 있다. 그래서 스펙에서 charset=utf-8과 같은 추가 파라미터를 지원하지 않는다.
따라서 'application/json'이라고만 사용해야지 'application/json;charset=utf-8'이라고 전달하는 것은 의미 없는 파라미터를 추가한 것이 된다.
response.getWriter()를 사용하면 추가 파라미터를 자동으로 추가해버린다. 이때는 response.getOutputStream()으로 출력하면 그런 문제가 없다.

# 회원 관리 웹 애플리케이션 요구 사항
## 회원 정보
- 이름 : 'username'
- 나이 : 'age'

## 기능 요구사항
- 회원 저장
- 회원 목록 조회

# 서블릿으로 회원 관리 웹 애플리케이션 만들기
- MemberFormServlet 은 단순하게 회원 정보를 입력할 수 있는 HTML Form을 만들어서 응답한다.
자바 코드로 HTML을 제공해야 하므로 쉽지 않은 작업이다.
  
## 'MemberSaveServlet'은 다음 순서로 동작한다.
1. 파라미터를 조회해서 Member 객체를 만든다.
2. Member 객체를 MemberRepository를 통해서 저장한다.
3. Member 객체를 사용해서 결과 화면용 HTML을 동적으로 만들어서 응답한다.

## 'MemberListServlet'은 다음 순서로 동작한다.
1. 'memberRepository.findAll()'을 통해 모든 회원을 조회한다.
2. 회원목록 HTML을 for루프를 통해서 회원 수만큼 동적으로 생성하고 응답한다.

## 템플릿 엔진으로...
- 서블릿에서 자바 코드만으로 동적으로 원하는 HTML 만들 수 있다. 정적인 HTML 문서라면 화면이 계속 달라지는 회원의 저장 결과라던가, 회원 목록 같은 동적인
HTML을 만드는 일은 불가능 할 것이다.
- 이와 같은 방법은 복잡하고 비효율적. 자바 코드로 HTML을 만드는 것보다 HTML 문서에 동적으로 변경해야 하는 부분만 자바 코드를 넣을 수 있다면 더 편리할 것이다.
- 이것이 바로 템플릿 엔진이 나온 이유이다. 템플릿 엔진을 사용하면 HTML 문서에서 필요한 곳만 코드를 적용해서 동적으로 변경할 수 있다.
- 템플릿 엔진에는 JSP, Thymeleaf, Freemarker, Velocity 등이 있다.
- JSP는 성능과 기능면에서 다른 템플릿 엔진과의 경쟁에서 밀리면서 점점 사장되어 가는 추세이다.

# JSP
- <%@ page contentType="text/html;charset=UTF-8" language="java" %>
  - 첫 줄은 JSP문서라는 뜻이다. JSP 문서는 이렇게 시작해야 한다.
- JSP는 자바 코드를 그대로 다 사용할 수 있다.
- <%@ page import="hello.servlet.domain.member.MemberRepository" %>
  - 자바의 import 문과 같다.
- <% ~~ %>
  - 이 부분에는 자바 코드를 입력할 수 있다.
- <%= ~~ %>
  - 이 부분에는 자바 코드를 출력할 수 있다.

# 서블릿과 JSP의 한계
- 서블릿으로 개발할 때는 뷰(View)화면을 위한 HTML을 만드는 작업이 자바 코드에 섞여서 지저분하고 복잡했다. 
  JSP를 사용한 덕분에 뷰를 생성하는 HTML 작업을 깔끔하게 가져가고, 중간중간 동적으로 필요한 부분에만 자바코드를 적용했다.
  그런데 이렇게 해도 해결되지 않는 몇가지 고민이 남는다.
- 회원 저장 JSP를 보면 코드의 상위 절반은 회원을 저장하기 위한 비즈니스 로직이고, 나머지 하위 절반만 결과를 HTML로 보여주기 위한 뷰 영역이다.
회원 목록의 경우도 마찬가지다.
- 코드를 잘 보면, JAVA 코드, 데이터를 조회하는 리포지토리 등등 다양한 코드가 모두 JSP에 노출되어 있다. JSP가 너무 많은 역할을 한다ㅏ.
- 수백 수천줄이 넘어가게 되면 유지보수가 어려워 짐.

## MVC 패턴의 등장
- 비즈니스 로직은 서블릿처럼 다른곳에서 처리하고, JSP는 목적에 맞게 HTML로 화면(view)을 그리는 일에 집중하도록 하자.
- 과거 개발자들도 모두 비슷한 고민이 있었고, MVC 패턴이 등장했다.

# MVC 패턴 - 개요
## 너무 많은 역할
- 하나의 서블릿이나 JSP 만으로 비즈니스 로직과 뷰 렌더링까지 모두 처리하게 되면, 너무 많은 역할을 하게 되고, 결과적으로 유지보수가 어려워진다.
- 비즈니스 로직을 호출하는 부분에 변경이 발생해도 해당 코드를 손대야 하고, UI를 변경할 일이 있어도 비즈니스 로직이 함께 있는 해당 파일을 수정해야 한다.

## 변경의 라이프 사이클
- 진짜 문제는 둘 사이에 변경의 라이프 사이클이 다르다는 점이다. 
- 예를 들어서 UI를 일부 수정하는 일과 비즈니스 로직을 수정하는 일은 각각 다르게 발생할 가능성이 매우높고 대부분 서로에게 영향을 주지 않는다.
- 이렇게 변경의 라이프 사이클이 다른 부분을 하나의 코드로 관리하는 것은 유지보수하기 좋지 않다. (물론 UI가 많이 변하면 함께 변경될 가능성도 있다.)

## 기능 특화
- 특히 JSP 같은 뷰 템플릿은 화면을 렌더링하는데 최적화 되어 있기 때문에 이 부분의 업무만 담당하는 것이 가장 효과적이다.

## Model View Controller
- MVC 패턴은 지금까지 학습한 것처럼 하나의 서블릿이나, JSP로 처리하던 것을 컨트롤러(Controller)와 뷰(View)라는 영역으로 서로 역할을 나눈 것을 말한다.
- 웹 에플리케이션은 보통 이 MVC 패턴을 사용한다.
- 컨트롤러
  - HTTP 요청을 받아서 파라미터를 검증하고, 비즈니스 로직을 실행한다. 그리고 뷰에 전달할 결과 데이터를 조회해서 모델에 담는다.
- 모델
  - 뷰에 출력할 데이터를 담아둔다.
  - 뷰가 필요한 데이터를 모두 모델에 담아서 전달해주는 덕분에 뷰는 비즈니스 로직이나 데이터 접근은 몰라도 되고, 화면을 렌더링 하는 일에 집중할 수 있다.
- 뷰
  - 모델에 담겨있는 데이터를 사용해서 화면을 그리는 일에 집중한다. 여기서는 HTML을 생성하는 부분을 말한다.
  
### 참고
- 컨트롤러에 비즈니스 로직을 둘 수도 있지만, 이렇게 되면 컨트롤러가 너무 많은 역할을 담당한다.
- 그래서 일반적으로 비즈니스 로직은 서비스(Service)라는 게층을 별도로 만들어서 처리한다.
- 그리고 컨트롤러는 비즈니스 로직이 있는 서비스를 호출하는 역할을 담당한다.
- 참고로 비즈니스 로직을 호출하는 컨트롤러의 코드도 변경될 수 있다.

# MVC 패턴 - 적용
- 서블릿을 컨트롤러로 사용하고, JSP를 뷰로 사용해서 MVC 패턴 적용
- Model은 HttpServletRequest 객체를 사용한다. request 내부에 데이터 저장소를 가지고 있는데,
'request.setAttribute()', 'request.getAttribute()'를 사용하면 데이터를 보관하고, 조회할 수 있다.
- 'dispatcher.forward()' : 다른 서블릿이나 JSP로 이동할 수 있는 기능이다. 서버 내부에서 다시 호출이 발생한다.
- '/WEB-INF' : 이 경로 안에 JSP가 있으면 외부에서 직접 JSP를 호출할 수 없다. 우리가 기대하는 것은 항상 컨트롤러를 통해서 JSP를 호출하는 것이다.
## redirect vs forward
- 리다이렉트는 실제 클라이언트(웹 브라우저)에 응답이 나갔다가, 클라이언트가 redirect 경로로 다시 요청한다.
- 따라서 클라이언트가 인지할 수 있고, URL 경로도 실제로 변경된다.
- 반면에 포워드는 서버 내부에서 일어나는 호출이기 때문에 클라이언트가 전혀 인지하지 못한다.

# MVC 패턴 - 한계
- MVC 패턴을 적용한 덕분에 컨트롤러의 역할과 뷰를 렌더링하는 역할을 명확하게 구분할 수 있다.
- 특히 뷰는 화면을 그리는 역할에 충실한 덕분에, 코드가 깔끔하고 직관적이다. 단순하게 모델에서 필요한 데이터를 꺼내고, 화면을 만들면 된다.
- 그런데 컨트롤러는 딱봐도 중복이 많고, 필요하지 않는 코드들도 많이 보인다.
## MVC 컨트롤러의 단점
### 포워드 중복
- View로 이동하는 코드가 항상 중복 호출되어야 한다.물론 이 부분을 메서드로 공통화해도 되지만, 해당 메서드도 항상 직접 호출해야 한다.
```java
RequestDispatcher dispatcher = request.getRequestDispatcher(viewPath);
dispatcher.forward(request, response);
```
## ViewPath에 중복
```java
String viewPath = "/WEB-INF/views/new-form.jsp";
```
- prefix : /WEB-INF/views/
- suffix : .jsp
- 그리고 만약 jsp가 아닌 thymeleaf같은 다른 뷰로 변경한다면 전체 코드를 다 변경해야 한다.

## 사용하지 않는 코드
- 다음 코드를 사용할 때도 있고, 사용하지 않을 때도 있다.
```java
HttpServletRequest request, HttpServletResponse response
```
- 그리고 이런 HttpServletRequest, HttpServletResponse를 사용하는 코드는 테스트 케이스를 작성하기도 어렵다.

## 공통 처리가 어렵다.
- 기능이 복잡해질수록 컨트롤러에서 공통으로 처리해야 하는 부분이 점점 더 많이 증가할 것이다. 
- 단순히 공통 기능을 메서드로 뽑으면 될 것 같지만, 결과적으로 해당 메서드를 항상 호출해야 하고, 실수로 호출하지 않으면 문제가 될 것이다.
- 그리고 호출하는 것 자체도 중복이다.

### 정리하면 공통처리가 어렵다는 문제가 있다.
- 이 문제를 해결하려면 컨트롤러 호출 전에 먼저 공통 기능을 처리해야 한다. 소위 수문장 역할을 하는 기능이 필요하다. 
- 프론트 컨트롤러(Front Controller) 패턴을 도입하면 이런 문제를 깔끔하게 해결할 수 있다. (입구를 하나로)
- 스프링 MVC의 핵심도 바로 이 프론트 컨트롤러에 있다.
