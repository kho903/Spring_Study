# 오류 코드와 메시지 처리 5
## 오류 코드 관리 전략
### 핵심은 구체적인 것에서 덜 구체적인 것으로
- `MessageCodesResolve`는 `required.item.itemName`처럼 구체적인 것을 먼저 만들어주고,
`required`처럼 덜 구체적인 것을 가장 나중에 만든다.
- 이렇게 하면 앞서 말한것 처럼 메시지와 관련된 공통 전략을 편리하게 도입할 수 있다.

### 왜 이렇게 복잡하게 사용하는가?
- 모든 오류 코드에 대해서 메시지를 각각 다 정의하면 개발자 입장에서 관리하기가 너무 힘들다.
- 크게 중요하지 않은 메시지는 범용성 있는 `required` 같은 메시지로 끝내고, 
  정말 중요한 메시지는 꼭 필요할 때 구체적으로 적어서 사용하는 방식이 더 효과적이다.

### 예시
```properties
#required.item.itemName=상품 이름은 필수입니다.
#range.item.price=가격은 {0} ~ {1} 까지 허용합니다.
#max.item.quantity=수량은 최대 {0} 까지 허용합니다.
#totalPriceMin=가격 * 수량의 합은 {0}원 이상이어야 합니다. 현재 값 = {1}
#==ObjectError==
#Level1
totalPriceMin.item=상품의 가격 * 수량의 합은 {0}원 이상이어야 합니다. 현재 값 = {1}
#Level2 - 생략
totalPriceMin=전체 가격은 {0}원 이상이어야 합니다. 현재 값 = {1}
#==FieldError==
#Level1
required.item.itemName=상품 이름은 필수입니다.
range.item.price=가격은 {0} ~ {1} 까지 허용합니다.
max.item.quantity=수량은 최대 {0} 까지 허용합니다.

#Level2 - 생략
#Level3
required.java.lang.String = 필수 문자입니다.
required.java.lang.Integer = 필수 숫자입니다.
min.java.lang.String = {0} 이상의 문자를 입력해주세요.
min.java.lang.Integer = {0} 이상의 숫자를 입력해주세요.
range.java.lang.String = {0} ~ {1} 까지의 문자를 입력해주세요.
range.java.lang.Integer = {0} ~ {1} 까지의 숫자를 입력해주세요.
max.java.lang.String = {0} 까지의 숫자를 허용합니다.
max.java.lang.Integer = {0} 까지의 숫자를 허용합니다.
#Level4
required = 필수 값 입니다.
min= {0} 이상이어야 합니다.
range= {0} ~ {1} 범위를 허용합니다.
max= {0} 까지 허용합니다.
```
- 크게 객체 오류와 필드 오류를 나눈다. 그리고 범용성에 따라 레벨을 나누어 둔다.
- `itemName`의 경우 `required` 검증 오류 메시지가 발생하면 다음 코드 순서대로 메시지가 생성된다.
1. required.item.itemName
2. required.itemName
3. required.java.lang.String
4. required
- 그리고 이렇게 생성된 메시지 코드를 기반으로 순서대로 `MessageSource`에서 메시지에서 찾는다.
- 구체적인 것에서 덜 구체적인 순서대로 찾는다. 메시지에 1번이 없으면 2번 -> 3번을 찾는다.
- 이렇게 되면 만약 크게 중요하지 않은 오류 메시지는 기존에 정의된 것을 그냥 재활용하면 된다.

## ValidationUtils
### ValidationUtils 사용 전
```java
if (!StringUtils.hasText(item.getItemName())) {
        bindingResult.rejectValue("itemName", "required", "기본: 상품 이름은
        필수입니다.");
}
```
### ValidationUtils 사용 후
`ValidationUtils.rejectIfEmptyOrWhitespace(bindingResult, "itemName", "required");`

### 정리
1. `rejectValue()`호출
2. `MessageCodesResolver`를 사용해서 검증 오류 코드로 메시지 코드들을 생성
3. `new FieldError()`를 생성하면서 메시지 코드들을 보관
4. `th:errors`에서 메시지 코드들로 메시지를 순서대로 메시지에서 찾고, 호충