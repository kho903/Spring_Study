# Bean Validation - 스프링 적용
## 스프링 MVC는 어떻게 Bean Validatior를 사용?
- 스프링 부트가 `spring-boot-starter-validation` 라이브러리를 넣으면 자동으로 
Bean Validator를 인지하고 스프링에 통합한다.
  
## 스프링 부트는 자동으로 글로벌 Validator로 등록한다.
- `LocalValidatorFactoryBean`을 글로벌 Validator로 등록한다.
- 이 Validator는 `@NotNull` 같은 애노테이션을 보고 검증을 수행한다.
- 이렇게 글로벌 Validator가 적용되어 있기 때문에, `@Valid`, `@Validated`만 적용하면 된다.
- 검증 오류가 발생하면 `FieldError`, `ObjectError`를 생성해서 `BindingResult`에 담아준다.

### 주의
- 글로벌 Validator를 직접 등록하면 스프링 부트는 Bean Validator를 글로벌 `Validator`로 등록하지 않는다.
- 따라서 애노테이션 기반의 빈 검증기가 동작하지 않는다.

### 참고
- 검증 시 `@Validated`, `@Valid` 둘 다 사용가능하다.
- `javax.validation.@Valid`를 사용하려면 `build.gradle` 의존관계 추가가 필요하다.
- `implementation 'org.springframework.boot:spring-boot-starter-validation'`
- `@Validated`는 스프링 전용 검증 애노테이션이고, `@Valid`는 자바 표준 검증 애노테이션이다.
- 둘 중 아무거나 사용해도 동일하지만, `@Validated`는 내부에 `groups`라는 기능을 포함하고 있다.

## 검증 순서
1. `@ModelAttribute` 각각의 필드에 타입 변환 시도
    1. 성공하면 다음으로
    2. 실패하면 `typeMismatch`로 `FieldError` 추가
2. Validator 적용

## 바인딩에 성공한 필드만 Bean Validation 적용
- BeanValidator는 바인딩에 실패한 필드는 BeanValidation을 적용하지 않는다.
- 생각해보면 타입 변환에 성공해서 바인딩에 성공한 필드여야 BeanValidation 적용이 의미 있다.
- (일단 모델 객체에 바인딩 받는 값이 정상으로 들어와야 검증도 의미가 있다.)
- `@ModelAttribute` -> 각각의 필드 타입 변환 시도 -> 변환에 성공한 필드만 BeanValidation 적용
- 예)
    - `itemName`에 문자 "A" 입력 -> 타입 변환성공 -> `itemName`필드에 BeanValidation 적용
    - `price`에 문자 "A" 입력 -> "A"를 숫자 타입 변환 시도 실패 -> typeMismatch FieldError 추가
        -> `price` 필드는 BeanValidation 적용 X
