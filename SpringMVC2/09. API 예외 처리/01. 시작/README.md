# API 예외 처리 - 시작
## 목표
- API 예외 처리는 어떻게 해야할까?
- HTML 페이지의 경우 지금까지 설명했던 것처럼 4xx, 5xx와 같은 오류 페이지만 있으면 대부분의 문제를 해결할 수 있다.
- 그런데 API의 경우에는 생각할 내용이 더 많다. 오류 페이지는 단순히 고객에게 오류 화면을 보여주고 끝이지만, 
  API는 각 오류 상황에 맞는 오류 응답 스펙을 정하고 JSON으로 데이터를 내려주어야 한다.
  
## WebServerCustomizer 다시 동작
- `WebServerCustomizer`가 다시 사용되도록 하기 위해 `@Component` 애노테이션에 있는 주석을 풀면
- 이제 WAS에 예외가 전달되거나, `response.sendError()`가 호출되면 위에 등록한 예외 페이지 경록 호출된다.

## ApiExceptionController - API 예외 컨트롤러
```java
package hello.exception.api;

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
- 단순히 회원을 조회하는 기능.
- 예외 테스트를 위해 `id`의 값이 `ex`이면 예외가 발생하도록 코드를 심어두었다.

### Postman으로 테스트
- HTTP Header에 `Accept`가 `application/json`인 것을 꼭 확인.

### 정상호출
`http://localhost:8080/api/members/ex`
```json
{
 "memberId": "spring",
 "name": "hello spring"
}
```

### 예외 발생 호출
`http://localhost:8080/api/members/ex`
```html
<!DOCTYPE HTML>
<html>
<head>
</head>
<body>
...
</body>
```
- API를 요청했는데, 정상의 경우 API로 JSON 형식으로 데이터가 정상 반환된다.
- 그런데 오류가 발생하면 우리가 미리 만들어둔 오류 페이지 HTML이 반환된다. 이것은 기대하는 바가 아니다.
- 클라이언트는 정상 요청이든, 오류 요청이든 JSON이 반환되기를 기대한다.
- 웹 브라우저가 아닌 이상 HTML을 직접 받아서 할 수 있는 것은 별로 없다.

## ErrorPageController - API 응답 추가
```java
@RequestMapping(value = "/error-page/500", produces = MediaType.APPLICATION_JSON_VALUE)
public ResponseEntity<Map<String, Object>> errorPage500Api(HttpServletRequest
request, HttpServletResponse response) {
     log.info("API errorPage 500");
     
     Map<String, Object> result = new HashMap<>();
     Exception ex = (Exception) request.getAttribute(ERROR_EXCEPTION);
     result.put("status", request.getAttribute(ERROR_STATUS_CODE));
     result.put("message", ex.getMessage());
        
     Integer statusCode = (Integer) request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

     return new ResponseEntity(result, HttpStatus.valueOf(statusCode));
}
```
- `produces = MediaType.APPLICATION_JSON_VALUE`의 뜻은 클라이언트가 요청하는 HTTP Header의 `Accept`의 값이 
  `application/json`일 때 해당 메서드가 호출된다는 것이다.
- 결국 클라이언트가 받고 싶은 미디어타입이 json이면 이 컨트롤러의 메서드가 호출된다.
  

- 응답 데이터를 위해서 `Map`을 만들고, `status`,`message` 키에 값을 할당했다.
- Jackson 라이브러리는 `Map`을 JSON 구조로 변환할 수 있다.
- `ResponseEntity`를 사용해서 응답하기 때문에 메시지 컨버터가 동작하면서 클라이언트에 JSON이 반환된다.

### 포스트맨을 통해 다시 테스트
- HTTP Header에 `Accept`가 `application/json`인 것을 꼭 확인
`http://localhost:8080/api/members/ex`
```json
{
 "message": "잘못된 사용자",
 "status": 500
}
```
- HTTP Header에 `Accept`가 `application/json`이 아니면, 기존 오류 응답인 HTML 응답이 출력되는 것을 확인할 수 있다.
