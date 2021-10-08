# 스프링 메시지 소스 사용
- `MessageSource` 인터페이스를 보면 코드를 포함한 일부 파라미터로 메시지를 읽어오는 기능을 제공한다.
## Test code
- ms.getMessage("hello", null, null)
    - code : hello
    - args : null
    - locale : null
- 가장 단순한 테스트는 메시지 코드로 `hello`를 입력하고 나머지 값은 null 입력
- Locale 정보가 없으면 `basename`에서 설정한 기본 이름 메시지 파일을 조회한다.
- `basename`으로 `messages`를 지정했으므로 `message.properties` 파일에서 데이터를 조회한다.

### MessageSourceTest
- 메시지가 없는 경우에는 `NoSuchMessageException` 발생
- 메시지가 없어도 기본 메시지 (`defaultMessage`)를 사용하면 기본 메시지가 반환된다.

### MessageSourceTest
- 메시지의 {0} 부분은 매개변수를 전달해서 치환할 수 있다.
- `hello.name=안녕 {0}` -> Spring 단어를 매개변수로 전달 -> `안녕 Spring`

### 국제화 파일 선택
- Locale 정보를 기반으로 국제화 파일을 선택한다.
- `Locale`이 `en_US`의 경우 `messages_en_US` -> `messages_en` -> `messages` 순서로 찾는다.
- `Locale`에 맞추어 구체적인 것이 있으면 구체적인 것을 찾고, 없으면 디폴트를 찾는다고 이해하면 된다.

### 국제화 파일 선택
- `ms.getMessage("hello", null, null)` : locale 정보가 없으므로 `messages`를 사용
- `ms.getMessage("hello", null, Locale.KOREA)` : locale 정보가 있지만 `message.ko`가 없으므로
`messages`를 사용
- `ms.getMessage("hello", null, Locale.ENGLISH)` : locale 정보가 
  `Locale.ENGLISH`이므로 `message_en`을 찾아서 사용
