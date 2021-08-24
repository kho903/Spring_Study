# HTML Form 데이터 전송
## POST 전송 - 저장
```html
<form action="/save" method="post">
  <input type="text" name="username" />
  <input type="text" name="age" />
  <button type="submit">전송</button>
</form>
```
- 웹 브라우저가 생성한 요청 HTTP 메시지
```http request
POST /save HTTP/1.1
Host: localhost:8080
Content-Type: application/x-www-form-urlencoded

username=kim&age=20
```

- 서버에서 HTTP 응답 메시지 생성
```http request
HTTP/1.1 200 OK
Content-Type: text/html;charset=UTF-8
Content-Length: 3423

<html>
  <body>...</body>
</html>
```
-> 웹 애플리케이션 서버 직접 구현 
- 서블릿의 등장 (서블릿을 지원하는 WAS 사용)

# 서블릿
## 특징
```java
@WebServlet(naem = "helloServlet", urlPatterns = "/hello")
public class HelloServlet extends HttpServlet {

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) {
	// 애플리케이션 로직
    }
}
```
- urlPatterns(/hello)의 URL이 호출되면 서블릿 코드가 실행
- HTTP 요청 정보를 편리하게 사용할 수 있는 HttpServletRequest
- Http 응답 정보를 편리하게 제공할 수 있는 HttpServletResponse
- 개발자는 HTTP 스펙을 매우 편리하게 사용

## 전체 과정
- 웹브라우저에 localhost:8080/hello라는 주소로 요청
- WAS 서버에서 HTTP  요청 메시지를 기반으로 request, response 객체를 생성
- 서블릿 컨테이너 내에 있는 내가 만든 helloServlet을 실행시켜 준다. 
- helloServlet이 return을 하면 response 객체 정보로 HTTP 응답 생성
- 웹 브라우저로 전송

# 서블릿 
## HTTP 요청, 응답흐름 정리
- HTTP 요청 시
	- WAS는 Request, Response 객체를 새로 만들어서 서블릿 객체 호출
	- 개발자는 Request 객체에서 HTTP 요청 정보를 편리하게 꺼내서 사용
	- 개발자는 Response 객체에 HTTP 응답 정보를 편리하게 입력
	- WAS는 Response 객체에 담겨있는 내용으로 HTTP 응답 정보를 생성

# 서블릿
## 서블릿 컨테이너
- 톰캣처럼 서블릿을 지원하는 WAS를 서블릿 컨테이너라고 함
- 서블릿 컨테이너는 서블릿 객체를 생성, 초기화, 호출, 종료하는 생명주기 관리
- 서블릿 객체는 싱글톤으로 관리
	- 고객의 요청이 올 때마다 계속 객체를 생성하는 것은 비효율
	- 최초 로딩 시점에 서블릿 객체를 미리 만들어두고 재활용
	- 모든 고객 요청은 동일한 서블릿 객체 인스턴스에 접근
	- 공유 변수 사용 주의
- JSP도 서블릿으로 변환되어서 사용
- 동시 요청을 위한 멀티 쓰레드 처리 지원

