# BindingResult1
## ValidationItemControllerV2 - addItemV1
- `if (!StringUtils.hasText(item.getItemName())) { bindingResult.addError(new FieldError("item", "itemName", "상품 이름은
  필수입니다.")`
- `public String addItemV1(@ModelAttribute Item item, BindingResult bindingResult,
  RedirectAttributes redirectAttributes)`
  - 주의 : `BindingResult bindingResult` 파라미터의 위치는 `@ModelAttribute Item item` 다음에 와야 한다.
### 필드 오류 - FieldError
- `bindingResult.addError(new FieldError("item", "itemName", "상품 이름은 필수입니다."));`
- FieldError 생성자 요약 <br>
`public FieldError(String objectName, String field, String defaultMessage) {}`
- 필드에 오류가 있으면 `FieldError` 객체를 생성해서 `bindingResult`에 담아두면 된다.
    - `objectName` : `@ModelAttribute` 이름
    - `field` : 오류가 발생한 필드 이름
    - `defaultMessage` : 오류 기본 메시지

### 글로벌 오류 - ObjectError
- `bindingResult.addError(new ObjectError("item", "가격 * 수량의 합은 10,000원 이상이어야
  합니다. 현재 값 = " + resultPrice));`

### ObjectError 생성자 요약
`public ObjectError(String objectName, String defaultMessage) {}`
- 특정 필드를 넘어서는 오류가 있으면 `objectError` 객체를 생성해서 `bindingResult`에 담아두면 된다.
    - `objectName` : `@ModelAttribute`의 이름
    - `defaultMessage` : 오류 기본 메시지

### 타임리프 스프링 검증 오류 통합 기능
- 타임리프는 스프링의 `BindingResult`를 활용해서 편리하게 검증 오류를 표현하는 기능을 제공한다.
  - `#fields` : `#fields`로 `BindingResult`가 제공하는 검증 오류에 접근할 수 있다.
  - `th:errors` : 해당 필드에 오류가 있는 경우에 태그를 출력한다. `th:if`의 편의 버전이다.
  - `th:errorclass` : `th:field`에서 지정한 필드에 오류가 있으면 `class` 정보를 추가한다.

