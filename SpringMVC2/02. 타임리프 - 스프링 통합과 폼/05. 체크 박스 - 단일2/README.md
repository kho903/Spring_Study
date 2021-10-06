# 체크 박스 - 단일2
- 개발할 때마다 히든 필드를 추가하는 것은 상당히 번거롭다.
- 타임리프가 제공하는 폼 기능을 사용하면 이런 부분을 자동으로 처리할 수 있다.

## 타임리프 - 체크 박스 코드 추가
`th:field="*{open}"`
- `<input type="hidden" name="_open" value="on" />`
- 타임리프를 사용하면 체크 박스의 히든 필드와 관련된 부분도 함께 해결해 준다. HTML 생성 결과를 보면 히든 필드 부분이 자동으로 생성되어 있다.
- 실행 로그
```text
FormItemController      : item.open=true    // 체크 박스를 선택하는 경우
FormItemController      : item.open=false   // 체크 박스를 선택하지 않는 경우
```

## 타임리프의 체크 확인
`checked="checked"`
- 체크박스에서 판매 여부를 선택해서 저장하면, 조회시에 `checked` 속성이 추가된 것을 확인할 수 있다.
- 이런 부분을 개발자가 직접 처리하려면 상당히 번거롭다. 타임리프의 `th:field`를 사용하면, 값이 `true`인 경우 체크를 자동으로 처리해준다.

