# 오류 코드와 메시지 처리 1
## FieldError 생성자
- `FieldError`는 두 가지 생성자를 제공한다.
```java
public FieldError(String objectName, String field, String defaultMessage);
public FieldError(String objectName, String field, @Nullable Object rejectedValue, boolean bindingFailure, @Nullable String[] codes, @Nullable Object[] arguments, @Nullable String defaultMessage);
```
파라미터 목록
- `objectName` : 오류가 발생한 객체 이름
- `field` : 오류 필드
- `rejectedValue` : 사용자가 입력한 값 (거절된 값)
- `bindingFailure` : 타입 오류 같은 바인딩 실패인지, 검증 실패인지 구분 값
- `codes` : 메시지 코드
- `arguments` : 메시지에서 사용하는 인자
- `defaultMessage` : 기본 오류 메시지
> `FieldError` , `ObjectError`의 생성자는 `errorCode`, `arguments`를 제공한다.
> 이것은 오류 발생 시 오류 코드로 메시지를 찾기 위해 사용된다.

## errors 메시지 파일 생성
- `messages.properties`를 사용해도 되지만, 오류 메시지를 구분하기 쉽게 `errors.properties`라는 별도의 파일로 관리하자.
- 먼저 스프링 부트가 해당 메시지 파일을 인식할 수 있게 `spring.messages.basename=messages,errors` 추가
-> 생략시 messages.properties를 기본으로 인식

### errors.properties 추가
```text
required.item.itemName=상품 이름은 필수입니다. 
range.item.price=가격은 {0} ~ {1} 까지 허용합니다. 
max.item.quantity=수량은 최대 {0} 까지 허용합니다. 
totalPriceMin=가격 * 수량의 합은 {0}원 이상이어야 합니다. 현재 값 = {1}
```
