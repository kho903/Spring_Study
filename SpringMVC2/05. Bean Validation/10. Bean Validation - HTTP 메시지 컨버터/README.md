# Bean Validation - HTTP 메시지 컨버터
- `@Valid`, `@Validated`는 `HttpMessageConverter` (`@RequestBody`)에도 적용할 수 있다.

### 참고
- `@ModelAttribute`는 HTTP 요청 파라미터 (URL 쿼리 스트링, POST Form)를 다룰 때 사용한다.
- `@RequestBody`는 HTTP Body의 데이터를 객체로 변환할 때 사용한다. 주로 API JSON 요청을 다룰 떄 사용한다.

## ValidationItemApiController
```java
package hello.itemservice.web.validation;

import hello.itemservice.web.validation.form.ItemSaveForm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/validation/api/items")
public class ValidationItemApiController {
    @PostMapping("/add")
    public Object addItem(@RequestBody @Validated ItemSaveForm form,
                          BindingResult bindingResult) {
        log.info("API 컨트롤러 호출");
        if (bindingResult.hasErrors()) {
            log.info("검증 오류 발생 errors={}", bindingResult);
            return bindingResult.getAllErrors();
        }
        log.info("성공 로직 실행");
        return form;
    }
}
```
## API의 경우 3가지 경우를 나누어 생각해야 한다.
- 성공 요청 : 성공
- 실패 요청 : JSON을 객체로 생성하는 것 자체가 실패함
- 검증 오류 요청 : JSON을 객체로 생성하는 것은 성공했고, 검증에서 실패함


### 성공 요청 및 로그
```
POST http://localhost:8080/validation/api/items/add
{"itemName":"hello", "price":1000, "quantity": 10}
```

```text
API 컨트롤러 호출
성공 로직 실행
```

### 실패 요청 / 결과 / 로그
```
POST http://localhost:8080/validation/api/items/add
{"itemName":"hello", "price":"A", "quantity": 10}
```
```json
{
 "timestamp": "2021-04-20T00:00:00.000+00:00",
 "status": 400,
 "error": "Bad Request",
 "message": "",
 "path": "/validation/api/items/add"
}
```
```
.w.s.m.s.DefaultHandlerExceptionResolver : Resolved
[org.springframework.http.converter.HttpMessageNotReadableException: JSON parse
error: Cannot deserialize value of type `java.lang.Integer` from String "A":
not a valid Integer value; nested exception is
com.fasterxml.jackson.databind.exc.InvalidFormatException: Cannot deserialize
value of type `java.lang.Integer` from String "A": not a valid Integer value
 at [Source: (PushbackInputStream); line: 1, column: 30] (through reference
chain: hello.itemservice.domain.item.Item["price"])]
```
- `HttpMessageConverter`에서 요청 JSON을 `Item` 객체로 생성하는 데 실패한다.
- 이 경우는 `Item` 객체를 만들지 못하기 때문에 컨트롤러 자체가 호출되지 않고 그 전에 예외가 발생한다.
- 물론 Validator도 실행되지 않는다.

### 검증 오류 요청
- `HttpMessageConverter`는 성공하지만 검증(Validator)에서 오류가 발생하는 경우
```
POST http://localhost:8080/validation/api/items/add
{"itemName":"hello", "price":1000, "quantity": 10000}
```
- 수량(`quantity`)이 `10000`이면 BeanValidation `@Max(9999)`에서 걸린다.

### 검증 오류 결과
```json
[
 {
 "codes": [
 "Max.itemSaveForm.quantity",
 "Max.quantity",
 "Max.java.lang.Integer",
 "Max"
 ],
 "arguments": [
 {
 "codes": [
 "itemSaveForm.quantity",
 "quantity"
 ],
 "arguments": null,
 "defaultMessage": "quantity",
 "code": "quantity"
 },
 9999
 ],
 "defaultMessage": "9999 이하여야 합니다",
 "objectName": "itemSaveForm",
 "field": "quantity",
 "rejectedValue": 10000,
 "bindingFailure": false,
 "code": "Max"
 }
]
```
- `return bindingResult.getAllErrors();`는 `ObjectError`와 `FieldError`를 반환한다.
- 스프링이 이 객체를 JSON으로 변환해서 클라이언트에 전달했다.
- 여기서는 예시로 보여주기 위해 검증 오류 객체들을 그대로 반환했다.
- 실제 개발할 때는 이 객체들을 그대로 사용하지 말고, 필요한 데이터만 뽑아서 별도의 API 스펙을
정의하고 그에 맞는 객체를 만들어서 반환해야 한다.
  
### 검증 오류 요청 로그
```
API 컨트롤러 호출
검증 오류 발생, errors=org.springframework.validation.BeanPropertyBindingResult: 1 
errors
Field error in object 'itemSaveForm' on field 'quantity': rejected value
[99999]; codes
[Max.itemSaveForm.quantity,Max.quantity,Max.java.lang.Integer,Max]; arguments
[org.springframework.context.support.DefaultMessageSourceResolvable: codes
[itemSaveForm.quantity,quantity]; arguments []; default message
[quantity],9999]; default message [9999 이하여야 합니다]
```

### @ModelAttribute vs @RequestBody
- HTTP 요청 파라미터를 처리하는 `@ModelAttribute`는 각각의 필드 단위로 세밀하게 적용된다. 
  그래서 특정 필드에 타입이 맞지 않는 오류가 발생해도 나머지 필드는 정상 처리할 수 있었다.
- `HttpMessageConverter`는 `@ModeAttribute`와 다르게 각각의 필드 단위로 적용되는 것이 아니라, 전체 객체 단위로 적용된다.
- 따라서 메시지 컨버터의 작동이 성공해서 `Item`객체를 만들어야 `@Valid`, `@Validated`가 적용된다.
- `@ModelAttribute`는 필드 단위로 정교하게 바인딩이 적용된다. 특정 필드가 바인딩 되지 않아도 나머지 필드는 정상 바인딩 되고, 
  Validator를 사용한 검증도 적용할 수 있다.
- `@RequestBody`는 HttpMessageConverter 단계에서 JSON 데이터를 객체로 변경하지 못하면 이후 단계 자체가
진행되지 않고 예외가 발생한다. 컨트롤러도 호출되지 않고, Validator도 적용할 수 없다.
  

### 참고
- `HttpMessageConverter` 단계에서 실패하면 예외가 발생한다.
