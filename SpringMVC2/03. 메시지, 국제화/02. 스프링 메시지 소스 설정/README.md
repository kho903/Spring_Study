# 스프링 메시지 소스 설정
- 스프링은 기본적인 메시지 관리 기능을 제공한다.
- 메시지 관리 기능을 사용하려면 스프링이 제공하는 `MessageSource`를 스프링 빈으로 등록하면 되는데,
`MessageSource`는 인터페이스이다. 따라서 구현체인 `ResourceBundleMessageSource`를 스프링 빈으로 등록하면 된다.

## 직접 등록
```java
@Bean
public MessageSource messageSource(){
    ResourceBundleMessageSource messageSource=new ResourceBundleMessageSource();
    messageSource.setBasenames("messages","errors");
    messageSource.setDefaultEncoding("utf-8");
    return messageSource;
}
```
- `basename` : 설정 파일의 이름을 지정한다.
    - `messages`로 지정하면 `messages.properties` 파일을 읽어서 사용한다.
    - 추가로 국제화 기능을 적용하려면 `messages_en.properties`, `messages_ko.properties`와 같이 파일명 마지막에 언어 정보를 주면 된다.
    만약 찾을 수 있는 국제화 파일이 없으면 `messages.properties`(언어정보가 없는 파일명)을 기본으로 한다.
    - 파일의 위치는 `/resource/messages.properties`에 두면 된다.
    - 여러 파일을 한 번에 지정할 수 있다. 여기서는 `messages`, `errors` 둘을 지정했다.
- `defaultEncoding` : 인코딩 정보를 지정한다. `utf-8`을 사용하면 된다.

## 스프링 부트
- 스프링 부트를 사용하면 스프링 부트가 `MessageSource`를 자동으로 스프링 빈으로 등록한다.

### 스프링 부트 메시지 소스 설정
- application.properties
`spring.messages.basename=messages,config.i18n.messages`

### 스프링 부트 메시지 소스 기본 값
- `spring.messages.basename=messages`
- `MessageSource`를 스프링 빈을 등록하지 않고, 스프링 부트와 관련된 별도의 설정을 하지 않으면 `messages`라는 이름으로 기본 등록된다.
- 따라서 `messages_en.properties`, `messages_ko.properties`, `messages.properties`파일만 등록하면 자동으로 인식된다.

## 메시지 파일 만들기
- `resources/messages.properties` : 기본 값으로 사용 (한글)
  ```properties
  hello=안녕
  hello.name=안녕 {0}
  ```
- `resources/messages_en.properties` : 영어 국제화 사용
  ```properties
  hello=hello
  hello.name=hello {0}
  ```