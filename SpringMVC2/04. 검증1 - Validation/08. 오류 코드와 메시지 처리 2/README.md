# 오류 코드와 메시지 처리 2
## 목표
- `FieldError`, `ObjectError`는 다루기 너무 번거롭다.
- 오류 코드도 좀 더 자동화 할 수 있지 않을까? 예) `item.itemName`처럼
- 컨트롤러에서 `BindingResult`는 검증해야 할 객체인 `target` 바로 다음에 온다. 
  따라서 `BindingResult`는 이미 본인이 검증해야 할 객체인 `target`을 알고 있다.

## `rejectValue()`, `reject()`
- `BindingResult`가 제공하는 `rejectValue()`, `reject()`를 사용하면 `FieldError`, 
  `ObjectError`를 직접 생성하지 않고, 깔끔하게 검증 오류를 다룰 수 있다.

## `rejectValue()`
```java
void rejectValue(@Nullable String field, String errorCode, @Nullable Object[] errorArgs, @Nullable String defaultMessage);
```
- `field` : 오류 필드명
- `errorCode` : 오류 코드 (이 오류 코드는 메시지에 등록된 코드가 아니다. messageResolver를 위한 오류 코드)
- `errorArgs` : 오류 메시지에서 `{0}`을 치환하기 위한 값
- `defaultMessage` : 오류 메시지를 찾을 수 없을 때 사용하는 기본 메시지

### 축약된 오류 코드
- `FieldError()`를 직접 다룰 때는 오류 코드를 `range.item.price`와 같이 모두 입력했다.
- 그런데 `rejectValue()`를 사용하고부터는 오류 코드를 `range`로 간닿나게 입력했다.
- 그래도 오류 메시지를 잘 찾아서 출력한다. 무언가 규칙이 있는 것 처럼 보인다.
- 이 부분을 이해하려면 `MessageCodesResolver`를 이해해야 한다.

### reject()
`void reject(String errorCode, @Nullable Object[] errorArgs, @Nullable String defaultMessage);`
