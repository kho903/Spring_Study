# Bean Validation - 에러 코드
- Bean Validation이 기본으로 제공하는 오류 메시지를 좀 더 자세히 변경하고 싶을 때,
- Bean Validation을 적용하고 `bindingResult`에 등록된 검증 오류를 보면
오류 코드가 애노테이션 이름으로 등록된다. `typeMismatch`와 유사하다.
- `NotBlank`라는 오류 코드를 기반으로 `MessageCodesResolver`를 통해 다양한 메시지 코드가 순서대로 생성된다.

## @NotBlank
- NotBlank.item.itemName
- NotBlank.itemName
- NotBlank.java.lang.String
- NotBlank

## @Range
- Range.item.price
- Range.price
- Range.java.lang.Integer
- Range

## 메시지 등록
`errors.properties`
```properties
#Bean Validation 추가
NotBlank={0} 공백X
Range={0}, {2} ~ {1} 허용
Max={0}, 최대 {1}
```
- `{0}`은 필드명이고, `{1}`, `{2}`... 는 각 애노테이션마다 다르다.

## BeanValidation 메시지 찾는 순서
1. 생성된 메시지 코드 순서대로 `messageSource`에서 메시지 찾기
2. 애노테이션의 `message`속성 사용 -> `@NotBlank(message = "공백! {0}")`
3. 라이브러리가 제공하는 기본 값 사용 -> 공백일 수 없습니다.

### 애노테이션의 messag 사용 예
```java
@NotBlank(message="공백은 입력할 수 없습니다.")
private Stirng itemName;
```
