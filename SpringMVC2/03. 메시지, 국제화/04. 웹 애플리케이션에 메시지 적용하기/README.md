# 웹 애플리케이션에 메시지 적용하기
```properties
label.item=상품 
label.item.id=상품 ID
label.item.itemName=상품명 
label.item.price=가격
label.item.quantity=수량
page.items=상품 목록 
page.item=상품 상세 
page.addItem=상품 등록
page.updateItem=상품 수정
button.save=저장
button.cancel=취소
```

## 타임리프 메시지 적용
- 타임리프의 메시지 표현식 `#{...}`를 사용하면 스프링의 메시지를 편리하게 조회할 수 있다.
- 예 : 상품 이름 조회 - `#{label.items}`
- 렌더링 전
`<div th:text="#{label.item}"></h2>`
- 렌더링 후
`<div>상품</h2>`

### 파라미터 사용
- hello.name=안녕 {0}
- `<p th:text="#{hello.name(${item.itemName})}"></p>`
