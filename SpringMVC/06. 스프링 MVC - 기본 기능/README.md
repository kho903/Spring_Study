# 프로젝트 생성
### 주의 : War X -> Jar
- JSP를 사용하지 않기 때문에 Jar를 사용하는 것이 좋다.
- Jar를 사용하면 항상 내장 서버(톰캣 등)을 사용하고, `webapp`경로도 사용하지 않는다.
- 내장 서버 사용에 최적화 되어 있는 기능이다. 최근에는 주로 이 방식.
- War를 사용하면 내장 서버도 사용가능하지만, 주로 외부 서버에 배포하는 목적으로 사용됨

# 로깅 간단히 알아보기
- 운영 시스템에서는 `System.out.println();`같은 시스템 콘솔을 사용해서 필요한 정보를 출력하지 않고, 
  별도의 로깅 라이브러리를 사용해서 로그를 출력한다.
## 로깅 라이브러리
- 스프링 부트 라이브러리를 사용하면 스프링 부트 로깅 라이브러리(`spring-boot-starter-logging`)가 함께 포함된다.
- 스프링 부트 로깅 라이브러리는 SLF4J, Logback을 기본으로 사용한다.
- 로그 라이브러리는 Logback, Log4J, Log4J2 등등 수많은 라이브러리가 있는데, 그것을 통합해서 인터페이스로 제공하는 것이 SLF4J 라이브러리.
- 쉽게 이야기해서 SLF4J는 인터페이스, 그 구현체로 Logback 같은 로그 라이브러리를 선택하면 된다.
- 실무에서는 스프링 부트가 기본으로 제공하는 Logback을 대부분 사용한다.

## 로그 선언
- `private Logger log = LoggerFactory.getLogger(getClass());`
- `private static final Logger log = LoggerFactory.getLogger(Xxx.class);`
- `@Slf4j` : 롬복 사용가능

## 로그 호출
- `log.info("hello")`
- `System.out.println("hello")`
  - 콘솔 직접 출력보다 로그를 사용한다.

## 매핑 정보
- `@RequestController`
  - `@Controller`는 반환 값이 `String`이면 뷰 이름으로 인식된다. 그래서 뷰를 찾고 뷰가 렌더링 된다.
  - `@RestController`는 반환 값으로 뷰를 찾는 것이 아니라, HTTP 메시지 바디에 바로 입력한다.
  따라서 실행 결과로 ok 메세지를 받을 수 있다. `@RequestBody`와 관련이 있다.

## 테스트
- 로그가 출력되는 포멧 확인
  - 시간, 로그 레벨, 프로세스 ID, 쓰레드명, 클래스명, 로그 메시지
- 로그 레벨 설정을 변경해서 출력 결과를 보자.
  - LEVEL : `TRACE > DEBUG > INFO > WARN > ERROR`
  - 개발 서버는 debug 출력
  - 운영 서버는 info 출력
- `@Slf4j`로 변경

## 로그 레벨 설정
- `application.properties`
- 전체 로그 레벨 설정 (기본 info)
  - logging.level.root=info
- hello.springmvc 패키지와 그 하위 로그 레벨 설정
  - logging.level.springmvc=debug

## 올바른 로그 사용법
- `log.debug("data="+data)`
  - 로그 출력 레벨을 info로 설정해도 해당 코드에 있는 "data="+data가 실제 실행이 되어버린다. 
  - 결과적으로 문자 더하기 연산이 발생한다.
- `log.debug("data={}", data)`
  - 로그 출력 레벨을 info로 설정하면 아무일도 발생하지 않는다. 따라서 앞과 같은 의미없는 연산이 발생하지 않는다.

## 로그 사용시 장점
- 쓰레드 정보, 클래스 이름 같은 부가 정보를 함께 볼 수 있고, 출력 모양을 조정할 수 있다.
- 로그 레벨에 따라 개발 서버에서는 모든 로그를 출력하고, 운영서버에서는 출력하지 않는 등 로그 상황에 맞게 조절할 수 있다.
- 시스템 아웃 콘솔에만 출력하는 것이 아니라, 파일이나 네트워크 등, 로그를 별도의 위치에 남길 수 있다.
특히 파일로 남길 때는 일별, 특정 용량에 따라 로그를 분할하는 것도 가능하다.
- 성능도 System.out 보다 좋다. (내부 버퍼링, 멀티 쓰레드 등등) -> 그래서 실무에서는 꼭 로그!

### 참고 자료
- 스프링 부트가 제공하는 로그 기능
  - https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.logging
- slf4j, Logback
