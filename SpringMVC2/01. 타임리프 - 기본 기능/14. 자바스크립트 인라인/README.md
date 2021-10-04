# 자바스크립트 인라인
- 타임리프는 자바스크립트에서 타임리프를 편리하게 사용할 수 있는 자바스크립트 인라인 기능을 제공한다.
- 자바스크립트 인라인 기능은 다음과 같이 적용하면 된다.<br>
`<script th:inline="javascript">`

## 자바스크립트 인라인 사용 전후
### 텍스트 렌더링
- `var username = [[${user.username}]];`
    - 인라인 사용 전 -> `var username = userA;`
    - 인라인 사용 후 -> `var username = "userA";`
- 인라인 사용 전 렌더링 결과를 보면 `userA`라는 변수 이름이 그대로 남아 있다. 타임리프 입장에서는 정확하게 렌더링 한 것이지만 
아마 개발자가 기대한 것은 다음과 같은 "userA"라는 문자일 것이다. 결과적으로 userA가 변수명으로 사용되어서 자바스크립트
오류가 발생한다. 다음으로 나오는 숫자 age의 경우에는 `"`가 필요 없기 때문에 정상 렌더링 된다.
- 인라인 사용 후 렌더링 결과를 보면 문자 타입인 경우 `"`를 포함해준다.
추가로 자바스크립트에서 문제가 될 수 있는 문자가 포함되어 있으면 이스케이프 처리도 해준다.
    - 예 : `"` -> `\"`
    
### 자바스크립트 내추럴 템플릿
- 타임리프는 HTML 파일을 직접 열어도 동작하는 내추럴 템플릿 기능을 제공한다.
- 자바스크립트 인라인 기능을 사용하면 주석을 활용해서 이 기능을 사용할 수 있다.
- `var username2 = /*[[${user.username]]*/ "test username";`
    - 인라인 사용 전 -> `var username2 = /*userA*/ "test username";`
    - 인라인 사용 후 -> `var username2 = "userA";`
- 인라인 사용 전 결과를 보면 정말 순수하게 그대로 해석을 해버렸다. 따라서 내추럴 템플릿 기능이 동작하지
않고, 심지어 렌더링 내용이 주석처리 되어 버린다.
- 인라인 사용 후 결과를 보면 주석 부분이 제거되고, 기대한 "userA"가 정확하게 적용된다.

### 객체
- 타임리프의 자바스크립트 인라인 기능을 사용하면 객체를 JSON으로 자동으로 변환해준다.
- `var user = [[${user}]]`
    - 인라인 사용 전 -> `var user = BasicController.User(username=userA, age=10)`
    - 인라인 사용 후 -> `var user = {"username" : "userA", "age" : 10};`
- 인라인 사용 전은 객체의 toString()이 호출된 값이다.
- 인라인 사용 후는 객체를 JSON으로 변환해준다.

## 자바스크립트 인라인 each
```html
<!-- 자바스크립트 인라인 each --> 
<script th:inline="javascript">
  [# th:each="user, stat : ${users}"]
  var user[[${stat.count}]] = [[${user}]];
  [/]
</script>
```
