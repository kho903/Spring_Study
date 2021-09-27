# 프로젝트 생성
### 주의 : War X -> Jar
- JSP를 사용하지 않기 때문에 Jar를 사용하는 것이 좋다.
- Jar를 사용하면 항상 내장 서버(톰캣 등)을 사용하고, `webapp`경로도 사용하지 않는다.
- 내장 서버 사용에 최적화 되어 있는 기능이다. 최근에는 주로 이 방식.
- War를 사용하면 내장 서버도 사용가능하지만, 주로 외부 서버에 배포하는 목적으로 사용됨

# 로깅 간단히 알아보기
- 운영 시스템에서는 `System.out.println();`같은 시스템 콘솔을 사용해서 필요한 정보를 출력하지 않고, 
  별도의 로깅 라이브러리를 사용해서 로그를 출력한다.
## 로깅 라이브러리
- 스프링 부트 라이브러리를 사용하면 스프링 부트 로깅 라이브러리(`spring-boot-starter-logging`)가 함께 포함된다.
- 스프링 부트 로깅 라이브러리는 SLF4J, Logback을 기본으로 사용한다.
- 로그 라이브러리는 Logback, Log4J, Log4J2 등등 수많은 라이브러리가 있는데, 그것을 통합해서 인터페이스로 제공하는 것이 SLF4J 라이브러리.
- 쉽게 이야기해서 SLF4J는 인터페이스, 그 구현체로 Logback 같은 로그 라이브러리를 선택하면 된다.
- 실무에서는 스프링 부트가 기본으로 제공하는 Logback을 대부분 사용한다.

## 로그 선언
- `private Logger log = LoggerFactory.getLogger(getClass());`
- `private static final Logger log = LoggerFactory.getLogger(Xxx.class);`
- `@Slf4j` : 롬복 사용가능

## 로그 호출
- `log.info("hello")`
- `System.out.println("hello")`
  - 콘솔 직접 출력보다 로그를 사용한다.

## 매핑 정보
- `@RequestController`
  - `@Controller`는 반환 값이 `String`이면 뷰 이름으로 인식된다. 그래서 뷰를 찾고 뷰가 렌더링 된다.
  - `@RestController`는 반환 값으로 뷰를 찾는 것이 아니라, HTTP 메시지 바디에 바로 입력한다.
  따라서 실행 결과로 ok 메세지를 받을 수 있다. `@RequestBody`와 관련이 있다.

## 테스트
- 로그가 출력되는 포멧 확인
  - 시간, 로그 레벨, 프로세스 ID, 쓰레드명, 클래스명, 로그 메시지
- 로그 레벨 설정을 변경해서 출력 결과를 보자.
  - LEVEL : `TRACE > DEBUG > INFO > WARN > ERROR`
  - 개발 서버는 debug 출력
  - 운영 서버는 info 출력
- `@Slf4j`로 변경

## 로그 레벨 설정
- `application.properties`
- 전체 로그 레벨 설정 (기본 info)
  - logging.level.root=info
- hello.springmvc 패키지와 그 하위 로그 레벨 설정
  - logging.level.springmvc=debug

## 올바른 로그 사용법
- `log.debug("data="+data)`
  - 로그 출력 레벨을 info로 설정해도 해당 코드에 있는 "data="+data가 실제 실행이 되어버린다. 
  - 결과적으로 문자 더하기 연산이 발생한다.
- `log.debug("data={}", data)`
  - 로그 출력 레벨을 info로 설정하면 아무일도 발생하지 않는다. 따라서 앞과 같은 의미없는 연산이 발생하지 않는다.

## 로그 사용시 장점
- 쓰레드 정보, 클래스 이름 같은 부가 정보를 함께 볼 수 있고, 출력 모양을 조정할 수 있다.
- 로그 레벨에 따라 개발 서버에서는 모든 로그를 출력하고, 운영서버에서는 출력하지 않는 등 로그 상황에 맞게 조절할 수 있다.
- 시스템 아웃 콘솔에만 출력하는 것이 아니라, 파일이나 네트워크 등, 로그를 별도의 위치에 남길 수 있다.
특히 파일로 남길 때는 일별, 특정 용량에 따라 로그를 분할하는 것도 가능하다.
- 성능도 System.out 보다 좋다. (내부 버퍼링, 멀티 쓰레드 등등) -> 그래서 실무에서는 꼭 로그!

### 참고 자료
- 스프링 부트가 제공하는 로그 기능
  - https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.logging
- slf4j, Logback

# 요청 매핑
## 매핑 정보
- @RestController
  - @Controller는 반환값이 String이면 뷰 이름으로 인식된다. 그래서 뷰를 찾고 뷰가 렌더링된다.
  - @RestController는 반환 값으로 뷰를 찾는 것이 아니라, HTTP 메시지 바디에 바로 입력한다.
    따라서 실행결과로 ok메세지를 받을 수 있다. @ResponseBody와 관련있다.
- @RequestMapping("/hello-basic")
  - /hello-basic URL 호출이 오면 이 메서드가 실행되도록 매핑한다.
  - 대부분의 속성을 배열[]로 제공하므로 다중 설정이 가능하다. `{"/hello-basic", "/hello-go"}`
  
## 둘다 허용
- 다음 두가지 요청은 다른 url 이지만, 스프링은 다음 url 요청들을 같은 요청으로 매핑한다.
- 매핑 : '/hello-basic'
- url 요청 : '/hello-basic', '/hello-basic/'

## HTTP 메서드
- '@RequestMapping'에 'method' 속성으로 HTTP 메서드를 지정하지 않으면 HTTP 메서드와 무관하게 호출된다.
- 모두 허용 : GET, HEAD, POST, PUT, PATCH, DELETE
- HTTP 메서드 매핑을 했을 때 다른 요청이 들어오면 HTTP 405 상태코드(Method Not Allowed)를 반환한다.

## PathVariable(경로 변수) 사용
- @GetMapping("/mapping/{userId}")
- @PathVariable("userId")
- 최근에는 HTTP API는 다음과 같이 리소스 경로에 식별자를 넣는 스타일을 선호한다.
  - '/mapping/userA'
  - '/users/1'
- @RequestMapping은 URL 경로를 템플릿화 할 수 있는데, @PathVariable을 사용하면 매칭되는 부분을 편리하게 조회할 수 있다.
- @PathVariable의 이름과 파라미터 이름이 같으면 생략할 수 있다.

# 요청 매핑 - API 예시
- 회원관리를 HTTP API로 만든다고 생각

## 회원관리 API
- 회원 목록 조회 : GET '/users'
- 회원 등록 : POST '/users'
- 회원 조회 : GET '/users/{userID}'
- 회원 수정 : PATCH '/users/{userID}'
- 회원 삭제 : DELETE '/users/{userID}'

# HTTP 요청 - 기본, 헤더 조회
- 애노테이션 기반의 스프링 컨트롤러는 다양한 파라미터를 지원한다.
- `HttpServletRequest`
- `HttpServletResponse`
- `HttpMethod` : HTTP 메서드를 조회한다. `org.springframework.http.HttpMethod`
- `Locale` : Locale 정보를 조회한다.
- `@RequestHeader MultiValueMap<String, String> headerMap`
  - 모든 HTTP 헤더를 MultiValueMap 형식으로 조회한다.
- `@RequestHeader("host") String host`
  - 특정 HTTP 헤더를 조회한다.
  - 속성
    - 필수 값 여부 : `required`
    - 기본 값 속성 : `defaultValue`
- `@CookieValue(value= = "myCooke", required = false) String cookie`
  - 특정 쿠키를 조회한다.
  - 속성
    - 필수 값 여부 : `required`
    - 기본 값 속성 : `defaultValue`

### MultiValueMap
- Map과 유사한데 하나의 키에 여러 값을 받을 수 있다.
- HTTP header, HTTP 쿼리 파라미터와 같이 하나의 키에 여러 값을 받을 때 사용한다.
  - keyA=value1&key=value2
```java
MultiValueMap<String, String> map = new LinkedMultiValueMap();
map.add("keyA", "value1");
map.add("keyA", "value2");

// [value1, value2]
List<String, String> values = map.get("keyA");
```

### @Slf4j
- 다음 코드를 자동으로 생성해서 로그를 선언해준다. 개발자는 편리하게 'log'라고 사용하면 된다.
```java
private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(RequestHeaderController.class);
```

# HTTP 요청 파라미터 - 쿼리 파라미터, HTML Form
## 클라이언ㅌ크에서 서버로 요청 데이터를 전달할 때는 주로 다음 3가지 방법
- GET - 쿼리 파라미터
  - /url?username=hello&age=20
  - 메시지 바디 없이, url의 쿼리 파라미터에 데이터를 포함해서 전달
  - 예) 검색, 필터, 페이징 등에서 많이 사용하는 방식

- POST - HTML Form
  - content-type:application/x-www-form-urlencoded
  - 메시지 바디에 쿼리 파라미터 형식으로 전달 username=hello&age=20
  - 예)회원 가입, 상품 주문, HTML Form 사용

- HTTP message body에 데이터를 직접 담아서 요청
  - HTTP API 에서 주로 사용, JSON, XML, TEXT
  - 데이터 형식은 주로 JSON 사용
  - POST, PUT, PATCH

### 요청 파라미터 - 쿼리 파라미터, HTML Form
- `HttpServletRequest`의 `request.getParameter()`를 사용하면 다음 두 가지의 파라미터를 조회할 수 있다.
- GET, 쿼리 파라미터 전송
  - 예 : http://localhost:8080/request-param?username=hello&age=20
- POST, HTML Form 전송
```http request
POST /request-param ...
content-type: application/x-www-form-urlencoded

username=hello&age=20
```
- GET 쿼리 파라미터 전송 방식이든, POST HTML Form 전송 방식이든 둘 다 형식이 같으므로 구분없이 조회할 수 있다.
- 이것을 간단히 요청 파리미터(request parameter) 조회라고 한다.
