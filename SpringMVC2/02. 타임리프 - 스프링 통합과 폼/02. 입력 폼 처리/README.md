# 입력 폼 처리
- `th:object` : 커맨드 객체를 지정한다.
- `*{...}` : 선택 변수 식이라고 한다. `th:object`에서 선택한 객체에 접근한다.
- `th:field`
    - HTML 태그의 `id`, `name`, `value` 속성을 자동으로 처리해준다.
### 렌더링 전
`<input type="text" th:field="*{itemName}" />`

### 렌더링 후
`<input type="text" id="itemName" name="itemName" th:value="*{itemName}" />`

## 등록 폼
- `th:object`를 적용하려면 먼저 해당 오브젝트 정보를 넘겨주어야 한다.
- 등록 폼이기 때문에 데이터가 비어있는 빈 오브젝트를 만들어서 뷰에 전달한다.
- `th:object="${item}` : `<form>`에서 사용할 객체를 지정한다. 식 (`*{...}`)을 적용할 수 있다.
- `th:field="*{itemName}"`
  - `*{itemName}`는 선택 변수 식을 사용했는데, `${item.itemName}`과 같다. 앞서 `th:object`로 `item`을 선택했기 때문에 선택 변수 식을 적용할 수 있다.
  - `th:field`는 `id`, `name`, `value` 속성을 모두 자동으로 만들어준다.
    - `id` : `th:field`에서 지정한 변수 이름과 같다. `id="itemName"`
    - `name` : `th:field`에서 지정한 변수 이름과 같다. `name="itemName"`
    - `value` : `th:field`에서 지정한 변수의 값을 사용한다. `value=""`

### 정리
- `th:object`, `th:field` 덕분에 폼을 개발할 때 약간의 편리함을 얻었다.
- 이것은 검증(Validation)에서 더욱 더 편리한 면을 보여준다.
