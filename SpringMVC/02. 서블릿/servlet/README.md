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