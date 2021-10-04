# 유틸리티 객체와 날짜
- 타임리프는 문자, 숫자, 날짜 URI 등을 편리하게 다루는 다양한 유틸리티 객체들을 제공한다.

## 타임리프 유틸리티 객체들
- `#message` : 메시지, 국제화 처리
- `#uris` : URI 이스케이프 지원
- `#dates` : `java.util.Date` 서식 지원
- `#calendars` : `java.util.Calendar` 서식 지원
- `#temporals` : 자바8 날짜 서식 지원
- `#numbers` : 숫자 서식 지원
- `#strings` : 문자 관련 편의 기능
- `#objects` : 객체 관련 기능 제공
- `#bools` : boolean 관련 기능 제공
- `#arrays` : 배열 관련 기능 제공
- `#lists`, `#sets`, `#maps` : 컬렉션 관련 기능 제공
- `#ids` : 아이디 처리 관련 기능 제공

## 자바8 날짜
- 타임리프에서 자바8 날짜인 `LocaleDate`, `LocaleDateTime`, `Instant`를 사용하려면 추가 라이브러리가 필요하다.
- 스프링 부트 타임리프를 사용하면 해당 라이브러리가 자동으로 추가되고 통합된다.

### 타임리프 자바8 날짜 지원 라이브러리
`thymeleaf-extras-java8time`
- 자바8 날짜용 유틸리티 객체 : `#temporals`
