# 라디오 버튼
- 라디오 버튼은 여러 선택지 중에 하나를 선택할 때 사용할 수 있다. 라디오 버튼을 자바 ENUM을 활용해 개발한다.
- 상품 종류
    - 도서, 식품, 기타
    - 라디오 버튼으로 하나만 선택할 수 있다.
- `itemTypes`를 등록 폼, 조회, 수정 폼에서 모두 사용하므로 `@ModelAttribute`의 특별한 사용법 적용
- `ItemType.values()`를 사용하면 해당 ENUM의 모든 정보를 배열로 반환한다.
  - 예) `[BOOK, FOOD, ETC]`
- 체크 박스는 수정 시 체크를 해제하면 아무 값도 넘어가지 않기 때문에, 별도의 히든 필드로 이런 문제를 해결했다.
- 라디오 버튼은 이미 선택이 되어 있다면, 수정 시에도 항상 하나를 선택하도록 되어 있으므로 체크박스와 달리 별도의 히든 필드를 사용할 필요가 없다.

### 타임리프에서 ENUM 직접 접근
```html
<div th:each="type : ${T(hello.itemservice.domain.item.ItemType).values()}">
```
- `${T(hello.itemservice.domain.item.ItemType).values()}` 스프링EL 문법으로 ENUM을 직접 사용할 수 있다.
- ENUM에 `values()`를 호출하면 해당 ENUM의 모든 정보가 배열로 반환된다.
- 그런데 이렇게 사용하면 ENUM의 패키지 위치가 변경되거나 할 때 자바 컴파일러가 타임리프까지 컴파일 오류를 잡을 수 없으므로 추천하지는 않는다.
