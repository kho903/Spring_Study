# 체크박스 - 단일
## 단순 HTML 체크박스
```html
<div>
    <div class="form-check">
        <input type="checkbox" id="open" name="open" class="form-check-input">
        <label for="open" class="form-check-label">판매 오픈</label>
    </div>
</div>
```
- FormItemController에 로그를 찍어 본 결과
```text
FormItemController      : item.open=true    // 체크박스를 선택하는 경우
FormItemCOntroller      : item.open=null    // 체크박스를 선택하지 않는 경우
```
- 체크박스를 체크하면 HTML Form에서 `open=on`이라는 값이 넘어간다.
- 스프링은 `on`이라는 문자를 `true`타입으로 변환해준다. (스프링 타입 컨버터가 이 기능을 수행한다.)

### 주의 - 체크 박스를 선택하지 않을 때
- HTML애서 체크 박스를 선택하지 않고 폼을 전송하면 `open`이라는 필드 자체가 서버로 전송되지 않는다.

## HTTP 요청 메시지 로깅
- HTTP 요청 메시지를 서버에서 보고 싶으면 application.properties 다음 설정을 추가한다.<br>
`logging.level.org.apache.coyote.http11=debug`
- 체크박스를 체카하지 않으면 HTTP 메시지 바디에 open의 이름도 전송이 되지 않는다.<br>
`itemName=itemA&price=10000&quantity=10`
- 서버에서 Boolean 타입을 찍어보면 null인 것을 확인할 수 있다.<br>
`log.info("item.open={}", item.getOpen());`
- HTML chekbox는 선택이 안되면 클라이언트에서 서버로 값 자체를 보내지 않는다. 수정의 경우에는 상황에 따라서 이 방식이 문제가 될 수 있다. 
사용자가 의도적으로 체크되어 있던 값을 체크를 해제에도 저장시 아무 값도 넘어가지 않기 때문에, 서버 구현에 따라서 
값이 오지 않은 것으로 판단해서 값을 변경하지 않을 수도 있다.
- 이런 문제를 해결하기 위해서 스프링 MVC는 약간의 트릭을 사용하는데, 히든필드를 하나 만들어서, 
`_open` 처럼 기존 체크박스 이름 앞에 언더스코어(`_`)를 붙여서 전송하면 체크를 해제했다고 인식할 수 있다.
히든 필드는 항상 전송된다. 따라서 체크를 해제한 경우 여기에서 `open`은 전송되지 않고, `_open`만 전송되는데, 
이 경우 스프링 MVC는 체크를 해제했다고 판단한다.

### 체크 해제를 인식하기 위한 히든 필드
`<input type="hidden" name="_open" value="on" />`

### 실행 로그
```text
FormItemController      : item.open=true    // 체크 박스를 선택하는 경우
FormItemController      : item.open=false   // 체크 박스를 선택하지 않는 경우
```

### 체크 박스 체크 / 미체크
- `open=on&_open=on`
    - 체크 박스를 체크하면 스프링 MVC가 `open`에 값이 있는 것을 확인하고 사용한다. 이 때 `_open`은 무시한다.
- `_open=on`
    - 체크 박스를 체크하지 않으면 스프링 MVC가 `_open`만 있는 것을 확인하고, `open`의 값이 체크되지 않았다고 인식한다.
    - 이 경우 서버에서 `Boolean` 타입을 찍어보면 결과가 `null`이 아니라 `false`인 것을 확인할 수 있다.
