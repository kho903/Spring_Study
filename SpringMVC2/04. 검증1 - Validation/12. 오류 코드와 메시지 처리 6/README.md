# 오류 코드와 메시지 처리 6
## 스프링이 직접 만든 오류 메시지 처리
검증 오류 코드는 다음과 같이 2가지로 나눌 수 있다.
- 개발자가 직접 설정한 오류 코드 -> rejectValue()를 직접 호출
- 스프링이 직접 검증 오류에 추가한 경우 (주로 타입 정보가 맞지 않음)

### price 필드에 문자 입력시
- 로그를 확인해보면 `BindingResult`에 `FieldError`가 담겨있고, 다음과 같은 메시지 코드들이 생성된 것을 확인할 수 있다.
`codes[typeMismatch.item.price,typeMismatch.price,typeMismatch.java.lang.Integer,typeMismatch]`
- 다음과 같이 4가지 메시지 코드가 입력되어 있다.
    - typeMismatch.item.price
    - typeMismatch.price
    - typeMismatch.java.lang.Integer
    - typeMismatch
- 스프링은 타입 오류가 발생하면 `typeMismatch`라는 오류 코드를 사용한다.
- 이 오류 코드가 `MessageCodesResolver`를 통하면서 4가지 메시지 코드가 생성된것이다.
- error.properties에 각 에러별로 message를 설정 해 줄 수 있다.

