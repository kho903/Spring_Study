# Bean Validation - 수정에 적용
## 수정에도 검증 기능 추가
### ValidationItemControllerV3 - edit() 변경
- edit() : Item 모델 객체에 `@Validated` 추가
- 검증오류가 발생하면 `editForm`으로 이동하는 코드 추가

### validation/v3/editForm.html 변경
- .field-error css 추가
- 글로벌 오류 메시지 
- 상품명, 가격, 수량 필드에 검증 기능 추가
