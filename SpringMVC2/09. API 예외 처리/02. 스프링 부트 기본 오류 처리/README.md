# API 예외 처리 - 스프링 부트 기본 오류 처리
- API 예외 처리도 스프링 부트가 제공하는 기본 오류 방식을 사용할 수 있다.
- 스프링 부트가 제공하는 `BasicErrorController`

## BasicErrorControlle`
```java
@RequestMapping(produces = MediaType.TEXT_HTML_VALUE)
public ModelAndView errorHtml(HttpServletRequest request,HttpServletResponse
        response){}
@RequestMapping
public ResponseEntity<Map<String, Object>>error(HttpServletRequest request){}
```
`/error` 동일한 경로를 처리하는 `errorHtml()`, `error()` 두 메서드를 확인할 수 있다.
- `errorHtml()` : `produces = MediaType.TEXT_HTML_VALUE` : 클라이언트 요청의 Accept 헤더 값이 
`text/html`인 경우에는 `errorHtml()`을 호출해서 view를 제공한다.
- `error()` : 그외 경우에 호출되고 `ResponseEntity()`로 HTTP Body에 JSON 데이터를 반환한다.

## 스프링 부트이 예외 처리
- 스프링 부트의 기본 설정은 오류 발생 시 (`/error`)를 오류 페이지로 요청한다.
- `BasicErrorController`는 이 경로를 기본으로 받는다. (`server.error.path`로 수정가능, 기본 경로 `/error`)

### Postman 으로 실행
```json
{
  "timestamp": "2021-04-28T00:00:00.000+00:00",
  "status": 500,
  "error": "Internal Server Error",
  "exception": "java.lang.RuntimeException",
  "trace": "java.lang.RuntimeException: 잘못된 사용자\n\tat
  hello.exception.web.api.ApiExceptionController.getMember(ApiExceptionController
  .java: 19...,
  "message": "잘못된 사용자",
  "path": "/api/members/ex"
}
```
스프링 부트는 `BasicErrorController`가 제공하는 기본 정보들을 활용해서 오류 API를 생성해준다.<br>
옵션
- server.error.include-binding-errors=always
- server.error.include-exception=true
- server.error.include-message=always
- server.error.include-stacktrace=always

### HTML 페이지 vs API 오류
- `BasicErrorController`를 확장하면 JSON 메시지도 변경할 수 있다.
- 그런데 API는 `@ExceptionHandler`가 제공하는 기능을 사용하는 것이 더 나음 방법이므로 
`BasicErrorController`를 확장해서 JSON 오류 메시지를 변경할 수 있다 정도로만 이해.
- 스프링 부트가 제공하는 `BasicErrorController`는 HTML 페이지를 제공하는 경우에는 먀우 편리하다.
- 4xx, 5xx 등 모두 잘 처리해준다. 그런데 API 오류 처리는 다른 차원의 이야기이다.
- API마다, 각각의 예외가 발생할 때 응답과, 상품과 관련된 API에서 발생하는 예외에 따라 그 결과가 달라질 수 있다.
- 결과적으로 매우 세밀하고 복잡하다. 따라서 이 방법은 HTML 화면을 처리할 때 사용하고, API는 오류처리는 `@ExceptionHandler`를 사용하자.