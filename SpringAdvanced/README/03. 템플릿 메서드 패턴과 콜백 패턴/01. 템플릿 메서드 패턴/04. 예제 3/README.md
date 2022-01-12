# 템플릿 메서드 패턴 - 예제 3
## 익명 내부 클래스 사용하기
- 템플릿 메서드 패턴은 `SubClassLogic1`, `SubClassLogic2`처럼 클래스를 계속 만들어야 하는 단점이 있다.
익명 내부 클래스를 사용하면 이런 단점을 보완할 수 있다.
- 익명 내부 클래스를 사용하면 객체 인스턴스를 생성하면서 동시에 생성할 클래스를 상속 받은 자식 클래스를 정의할 수 있다.
이 클래스는 `SubClassLogice1`처럼 직접 지정하는 이름이 없고 클래스 내부에 선언되는 클래스여서 익명 내부 클래스라 한다.

## TemplateMethodTest - templateMethodV2() 추가
```java
/**
 * 템플릿 메서드 패턴, 익명 내부 클래스 사용
 */
@Test
void templateMethodV2() {
    AbstractTemplate template1 = new AbstractTemplate() {
        @Override
        protected void call() {
            log.info("비즈니스 로직1 실행");
        }
    };
    log.info("클래스 이름1={}", template1.getClass());
    template1.execute();
    AbstractTemplate template2 = new AbstractTemplate() {
            @Override
            protected void call() {
            log.info("비즈니스 로직1 실행");
        }
    };
    log.info("클래스 이름2={}", template2.getClass());
    template2.execute();
}
```
### 실행 결과
```text
클래스 이름1 class hello.advanced.trace.template.TemplateMethodTest$1
비즈니스 로직1 실행
resultTime=3
클래스 이름2 class hello.advanced.trace.template.TemplateMethodTest$2
비즈니스 로직2 실행
resultTime=0
```
- 실행 결과를 보면 자바가 임의로 만들어주는 익명 내부 클래스 이름은 `TemplateMethodTest$1`,
`TemplateMethodTest$2`인 것을 확인할 수 있다.
