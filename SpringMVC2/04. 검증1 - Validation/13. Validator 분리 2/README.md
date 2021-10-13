# Validator 분리 2
- 스프링이 `Validator`인터페이스를 별도로 제공하는 이유는 체계적으로 검증 기능을 도입하기 위해서다.
- 검증기를 직접 불러서 사용해도 되지만, `Validator` 인터페이스를 사용해서 검증기를 만들면 스프링의 추가적인 도움을 받을 수 있다.

## WebDataBinder를 통해서 사용하기
- `WebDataBinder`는 스프링의 파라미터 바인딩의 역할을 해주고 검증 기능도 내부에 포함된다.

## ValidationItemControllerV2
```java
@InitBinder
public void init(WebDataBinder dataBinder) {
 log.info("init binder {}", dataBinder);
 dataBinder.addValidators(itemValidator);
}
```
- 이렇게 `WebDataBinder`에 검증기를 추가하면 해당 컨트롤러에서는 검증기를 자동으로 적용할 수 있다.
- `@InitBinder` -> 해당 컨트롤러에만 영향을 준다. 글로벌 설정을 별도로 해야한다,

## @Validated
- `@Validated`는 검증기를 실행하라는 애노테이션이다.
- 이 애노테이션이 붙으면 앞서 `WebDataBinder`에 등록한 검증기를 찾아서 실행한다.
- 그런데 여러 검증기를 등록한다면 그 중에 어떤 검증기가 실행되어야 할 지 구분이 필요하다.
- 이 때, `supports()`가 사용된다.

```java
@Component
public class ItemValidator implements Validator {
    @Override
    public boolean supports(Class<?> clazz) {
        return Item.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {...}
}
```

## 글로벌 설정 - 모든 컨트롤러에 다 적용
```java
@SpringBootApplication
public class ItemServiceApplication implements WebMvcConfigurer {
    public static void main(String[] args) {
        SpringApplication.run(ItemServiceApplication.class, args);
    }

    @Override
    public Validator getValidator() {
        return new ItemValidator();
    }
}
```
- 이렇게 글로벌 설정을 추가할 수 있따. 기존 컨트롤러의 `@IniteBinder`를 제거해도 글로벌 설정으로 정상 동작한다.
- 글로벌 설정을 하면 다음에 설명한 BeanValidator가 자동 등록되지 않는다.

### 참고
- 검증 시 `@Validated`, `@Valid` 둘 다 사용가능하다.
- `javax.validation.@Valid`를 사용하려면 `build.gradle` 의존관계 추가가 필요하다.
- `implementation 'org.springframework.boot:spring-boot-starter-validation'`
- `@Validated`는 스프링 전용 검증 애노테이션이고, `@Valid`는 자바 표준 검증 애노테이션이다.
