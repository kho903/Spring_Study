# 스프링 부트 - 오류 페이지 2
## BasicErrorController가 제공하는 기본 정보들
- `BasicErrorController` 컨트롤러는 다음 정보를 model에 담아서 뷰에 전달한다.
- 뷰 템플릿은 이 값을 활용해서 출력할 수 있다.
```text
* timestamp: Fri Feb 05 00:00:00 KST 2021
* status: 400
* error: Bad Request
* exception: org.springframework.validation.BindException
* trace: 예외 trace
* message: Validation failed for object='data'. Error count: 1
* errors: Errors(BindingResult)
* path: 클라이언트 요청 경로 (`/hello`)
```

## 오류 정보 추가 
```html
<ul>
 <li>오류 정보</li>
 <ul>
 <li th:text="|timestamp: ${timestamp}|"></li>
 <li th:text="|path: ${path}|"></li>
 <li th:text="|status: ${status}|"></li>
 <li th:text="|message: ${message}|"></li>
 <li th:text="|error: ${error}|"></li>
 <li th:text="|exception: ${exception}|"></li>
 <li th:text="|errors: ${errors}|"></li>
 <li th:text="|trace: ${trace}|"></li>
 </ul>
 </li>
 </ul>
```
- 오류 관련 내부 정보들을 고객에게 노출하는 것은 좋지 않다. 고객이 해당 정보를 읽어도 혼란만 더해지고, 보안상 문제가 될 수도 있다.
- 그래서 `BasicErrorController` 오류 컨트롤러에서 다음 오류 정보를 `model`에 포함할 지 여부 선택할 수 있다.

`application.properties`
- `server.error.include-exception=false` : `exception` 포함 여부 (`true`, `false`)
- `server.error.include-message=never` : `message` 포함 여부
- `server.error.include-stacktrace=never` : `trace` 포함 여부
- `server.error.include-binding-errors=never` : `errors` 포함 여부

`application.properties`
```properties
server.error.include-exception=true
server.error.include-message=on_param
server.error.include-stacktrace=on_param
server.error.include-binding-errors=on_param
```
- 기본 값이 `never`인 부분은 다음 3가지 옵션을 사용할 수 있다.
- `never, always, on_param`
- `never` : 사용하지 않음
- `always` : 항상 사용
- `on_param` : 파라미터가 있을 때 사용

- `on_param`은 파라미터가 있으면 해당 정보를 노출한다. 디버그 시 문제를 확인하기 위해 사용할 수 있다.
- 그런데 이 부분도 개발 서버에서 사용할 수 있지만, 운영 서버에서는 권장하지 않는다.
- `on_param`으로 설정하고 다음과 같이 HTTP 요청 시 파라미터를 전달하면 해당 정보들이 `model`에 담겨서 뷰 템플릿에서 출력된다.
`message=&errors=&trace=`
- 실무에서는 이것들을 노출하면 안된다. 사용자에게는 이쁜 오류 화면과 고객이 이해할 수 있는 간단한 오류 메시지를
보여주고, 오류는 서버에 로그로 남겨서 로그로 확인해야 한다.

### 스프링 부트 오류 관련 옵션
- `server.error.whitelabel.enabled=true` : 오류 처리 화면을 못 찾을 시, 스프링 whitelabel 오류 페이지 적용
- `server.error.path=/error` : 오류 페이지 경로, 스프링이 자동 등록하는 서블릿 글로벌 오류 페이지 경로와
`BasicErrorController` 오류 컨트롤러 경로에 함께 사용된다.
  
### 확장 포인트
- 에러 공통 처리 컨트롤러의 기능을 변경하고 싶으면 `ErrorController` 인터페이스를 상속 받아서
구현하거나 `BasicErrorController`상속 받아서 기능을 추가하면 된다.
  
### 정리
- 스프링 부트가 기본으로 제공하는 오류 페이지를 확용하면 오류 페이지와 관련된 대부분의 문제는 손쉽게 해결할 수 있다.

