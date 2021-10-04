# 템플릿 조각
- 웹 페이지를 개발할 때는 공통영역이 많이 있다. (상단 / 하단 영역 / 죄측 카테고리 등)
- 이런 부분을 코드를 복사해서 사용한다면 변경 시 여러 페이지를 다 수정해야 하므로 상당히 비효율적이다.
- 타임리프는 이런 문제를 해결하기 위해 템플릿 조각과 레이아웃 기능을 지원한다.
- `th:fragment`가 있는 태그는 다른곳에 포함되는 코드 조각으로 이해하면 된다.
- `template/fragment/footer :: copy` : `template/fragment/footer.html` 템플릿에 있는
`th:fragment="copy"`라는 부분을 템플릿 조각으로 가져와서 사용한다는 의미이다.

## 부분 포함 insert
- `<div th:insert="~{template/fragment/footer :: copy}"></div>`
- `th:insert`를 사용하면 현재 태그(`div`) 내부에 추가한다.

## 부분 포함 replace
- `<div th:replace="~{template/fragment/footer :: copy}"></div>`
- `th:replace`를 사용하면 현재 태그(`div`)를 대체한다.

## 부분 포함 단순 표현식
- `<div th:replace="template/fragment/footer :: copy"></div>`
- `~{...}`를 사용하는 것이 원칙이지만 템플릿 조각을 사용하는 코드가 단순하면 이 부분을 생략할 수 있다.

## 파라미터 사용
- 다음과 같이 파라미터를 전달해서 동적으로 조각을 렌터링 할 수도 있다.
- `<div th:replace="~{template/fragment/footer :: copyParam ('데이터1', '데이터2')}"></div>`
