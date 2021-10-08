# 웹 애플리케이션에 국제화 적용하기
## 웹으로 호가인하기
- 웹 브라우저의 언어 설정 값을 변경하면서 국제화 적용 확인
- 크롬 브라우저 -> 설정 -> 언어 우선순위 변경
- 웹 브라우저의 언어 설정 값을 변경하면 요청 시 `Accept-Language`의 값이 변경된다.
- `Accept-Language`는 클라이언트가 서버에 기대하는 언어 정보를 담아서 요청하는 HTTP 요청 헤더이다.

## 스프링의 국제화 메시지 선택
- 메시지 기능은 `Locale` 정보를 알아야 언어를 선택할 수 있다.
- 결국 스프링도 `Locale` 정보를 알아야 언어를 선택할 수 있는데, 스프링은 언어 선택시 기본으로 `Accept-Language` 헤더 값을 사용한다.

### LocaleResolver
- 스프링은 `Locale` 선택 방식을 변경할 수 있도록 `LocaleResolver`라는 인터페이스를 제공하는데,
스프링 부트는 기본으로 `Accept-Language`를 활용하는 `AcceptHeaderLocaleResolver`를 사용한다.
- 인터페이스
```java
public interface LocaleResolver {
    Locale resolveLocale(HttpServletRequest request);

    void setLocale(HttpServletRequest request, @Nullable HttpServletResponse
            response, @Nullable Locale locale);
}
```
### LocaleResolver 변경
- 만약 `Locale` 선택 방식을 변경하려면 `LocaleResolver`의 구현체를 변경해서 쿠키나 세션 기반의
`Locale` 선택 기능을 사용할 수 있따. 에를 들어 고객이 직접 `Locale`을 선택하도록 하는 것이다.
- 관련 예제 : `LocaleResolver`
