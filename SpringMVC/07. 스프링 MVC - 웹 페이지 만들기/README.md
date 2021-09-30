# 요구사항 분석
- 상품을 관리할 수 있는 서비스
## 상품 도메인 분석
- 상품 ID
- 상품명
- 가격
- 수량

## 상품 관리 기능
- 상품 목록
- 상품 상세
- 상품 등록
- 상품 수정

## 서비스 제공 흐름
- 클라이언트 -> 상품목록
    - ->상품 등록 폼 -> 상품 저장 -> 내부호출 -> 상품 상세 -> 상품 수정 폼 -> 상품 수정
    - -> 상품 상세 -> 상품 수정 폼 -> 상품 수정 ->redirect 상품 상세
- 요구사항이 정리되고 디자이너, 웹 퍼블리셔, 백엔드 개발자가 업무를 나누어 진행한다.
- 디자이너 : 요구사항에 맞도록 디자인하고, 디자인 결과물을 웹 퍼블리셔에게 넘겨준다.
- 웹 퍼블리셔 : 디자이너에게 받은 디자인을 기반으로 HTML, CSS를 만들어 개발자에게 제공한다.
- 백엔드 개발자 : 디자이너, 웹 퍼블리셔를 통해서 HTML 화면이 나오기 전까지 시스템을 설계하고, 핵심 비즈니스 모델을 개발한다.
이후 HTML이 나오면 이 HTML을 뷰 템플릿으로 변환해서 동적으로 화면을 그리고, 또 웹 화면의 흐름을 제어한다.
  
## 참고
- React, Vue.js 같은 웹 클라이언트 기술을 사용하고, 웹 프론트엔드 개발자가 별도로 있으면, 웹 프론트엔드 개발자가
  웹 퍼블리셔 역할까지 포함해서 하는 경우도 있다.
- 웹 클라이언트 기술을 사용하면, 웹 프론트엔드 개발자가 HTML을 동적으로 만드는 역할과 웹 화면의 흐름을 담당한다. 이 경우, 백엔드 개발자는
HTML 뷰 템플릿을 직접 만지는 대신에, HTTP API를 통해 웹 클라이언트가 필요로 하는 데이터와 기능을 제공하면 된다.

# 타임리프 간단히 알아보기
## 타임리프 사용 선언
`<html xmlns:th="http://www.thymeleaf.org">`
##  속성 변경 - th:href
`th:href="@{/css/bootstrap.min.css}"`
- `href=value1`을 `th:href="value2"`의 값으로 변경한다.
- 타임리프 뷰 템플릿을 거치게 되면 원래 값을 `th:xxx` 값으로 변경한다. 만약 값이 없다면 새로 생성한다.
- HTML을 그대로 볼 때는 `href` 속성이 사용되고, 뷰 템플릿을 거치면 `th:href`의 값이 `href`로 대체되면서 동적으로 변경할 수 있다.
- 대부분의 HTML 속성을 `th:xxx`로 변경할 수 있다.

## 타임리프 핵심
- 핵심은 `th:xxx`가 붙은 부분은 서버사이드에서 렌더링되고, 기존 것을 대체한다. `th:xxx`이 없으면 기존 html의 `xxx` 속성이 그대로 사용된다.
- HTML을 파일로 직저 열었을 때, `th:xxx`가 있어도 웹 브라우저는 `ht:` 속성을 알지 못하므로 무시한다.
- 따라서 HTML을 파일 보기를 유지하면서 템플릿 기능도 할 수 있다.

## URL 링크 표현식 - @{...}
`th:href="@{/css/bootstrap.min.css}"`
- `@{...}` : 타임리프는 URL 링크를 사용하는 경우 `@{...}`를 사용한다. 이것을 URL 링크 표현식이라 한다.
- URL 링크 표현식을 사용하면 서블릿 컨텍스트를 자동으로 포함한다.

## 상품 등록 폼으로 이동
## 속성 변경 - th:onclick
- `onclick="location.href='addForm.html'"`
- `th:onclick="|location.href='@{/basic/items/add}'|"`<br>
여기에는 리터럴 대체 문법이 사용되었다.

## 리터럴 대체 - |...|
`|...|` : 이렇게 사용한다.
- 타임리프에서 문자와 표현식 등은 분리되어 있기 때문에 더해서 사용해야 한다.
  - `<span th:text="|Welcome to our application, ' + ${user.name} + '!'">`
- 다음과 같이 리터럴 대체 문법을 사용하면, 더하기 없이 편리하게 사용할 수 있다.
  - `<span th:text="|Welcome to our application, ${user.name}!|">`
- 결과를 다음과 같이 만들어야 하는데
  - `location.href='/basic/items/add'`
- 그냥 사용하면 문자와 표현식을 각각 따로 더해서 사용해야 하므로 다음과 같이 복잡해진다.
  - `th:onclick="'location.href=' + '\'' + @{/basic/items/add} + '\''"`
- 리터럴 대체 문법을 사용하면 다음과 같이 편리하게 사용할 수 있다.
  - `th:onclick="|location.href='@{/basic/items/add}'|"`

## 반복 출력 - th:each
`<tr th:each="item : ${items}">`
- 반복은 `th:each`를 사용한다. 이렇게 하면 모델에 포함된 `items` 컬렉션 데이터가 `item` 변수에 하나씩 포함되고, 
  반복문 안에서 `item` 변수를 사용할 수 있다.
- 컬렉션의 수 만큼 `<tr>...</tr>`이 하위 태그를 포함해서 생성된다.

## 변수 표현식 - ${...}
`<td th:text="${item.price}">10000</td>`
- 모델에 포함된 값이나, 타임리프 변수로 선언한 값을 조회할 수 있다.
- 프로퍼티 접근법을 사용한다. (`item.getPrice()`)

## 내용 변경 - th:text
- `<td th:text="${item.price}">10000</td>`
- 내용의 값을 `th:text`의 값으로 변경한다.
- 여기서는 10000을 `${item.price}`의 값으로 변경한다.

## URL 링크표현식 2 - @{...}
- `th:href="@{/basic/items/{itemId}(itemId=${item.id})}"`
- 상품 ID를 선택하는 링크를 확인해보자
- URL 링크 표현식을 사용하면 경로를 템플릿처럼 편리하게 사용할 수 있다.
- 경로 변수(`{itemId}`) 뿐만 아니라 쿼리 파라미터도 생성한다.
- 예) `th:href="@{/basic/items/{itemId}(itemId=${item.id}, query='test')}"`
  - 생성 링크 : `http://localhost:8080/basic/items/1?query=test`

## URL 링크 간단히
- `th:href="@{|/basic/items/${item.id}|}`
- 상품 이름을 선택하는 링크를 확인해보자.
- 리터럴 대체 문법을 활용해서 간단히 사용할 수도 있다.

## 참고
- 타임리프는 순수 HTML 파일을 웹 브라우저에서 열어도 내용을 확인할 수 있고, 서버를 통해 뷰 템플릿을 거치면 동적으로 변경된 결과를
확인할 수 있다. JSP를 생각해보면, JSP 파일은 웹 브라우저에서 그냥 열면 JSP 소스코드와 HMTL이 뒤죽박죽 되어서 정상적인 확인이 불가능하다.
오직 서버를 통해서 JSP를 열어야 한다.
- 이렇게 순수 HTML을 그대로 유지하면서 뷰 템플릿도 사용할 수 있는 타임리프의 특징을 네츄럴 템플릿(natural template) 이라 한다.

## 속성 변경 - th:value
`th:value="${item.id}"`
- 모델에 있는 item 정보를 획득하고 프로퍼티 접근법으로 출력한다. (item.getId())
- value 속성은 th:value 속성으로 변경한다.

## 상품 수정 링크
- `th:onclick="|location.href='@{/basic/items/{itemId}/edit(itemId=${item.id)}'|"`

## 목록으로 링크
- `th:onclick="|location.href='@{/basic/items}'|"`

## 속성 변경 - th:action
- `th:action`
- HTML form에서 `action`에 값이 없으면 현재 URL에 데이터를 전송한다.
- 상품 등록 폼의 URL과 실제 상품 등록을 처리하는 URL을 똑같이 맞추고 HTTP 메서드로 두 기능을 구분한다.
  - 상품 등록 폼 : GET `/basic/items/add`
  - 상품 등록 처리 : POST `/basic/items/add`
- 이렇게 하면 하나의 URL로 등록 폼과 등록 처리를 깔끔하게 처리할 수 있다.

## 취소
- 취소 시 상품 목록으로 이동한다.
- `th:onclick="|location.href='@{/basic/items}'|"`

# 상품 등록 처리 - @ModelAttribute
## POST - HTML Form
- `content-type: application/x-www-form-urlencoded`
- 메시지 바디에 쿼리 파라미터 형식으로 전달 `itemName=itemA&price=10000&quantity=10`
  - 요청 파라미터 형식을 처리해야 하므로 `@RequestParam` 사용
- 예) 회원 가입, 상품 주문, HTML Form 사용

## @RequestParam
- `@RequestParma String itemName` : itemName 요청 파라미터 데이터를 해당 변수에 받는다.
- `Item` 객체를 생성하고 `itemRepository`를 통해서 저장한다.
- 저장된 `item`을 모델에 담아서 뷰에 전달한다.

## @ModelAttribute
- `@RequestParam`으로 변수를 하나하나 받아서 `Item`을 생성하는 과정은 불편하다.
- @ModelAttribute - 요청 파라미터 처리
  - `@ModelAttribute`는 `Item` 객체를 생성하고, 요청 파라미터의 값을 프로퍼티 접근법(setXxx)으로 입력해준다.
- @ModelAttribute - Model 추가
  - 모델(Model)에 `@ModelAttribute`로 지정한 객체를 자동으로 넣어준다.
