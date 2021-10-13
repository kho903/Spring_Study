# Bean Validation - 시작
## Bean Validation 의존관계 추가
### 의존 관계 추가
- Bean Validation을 사용하려면 다음 의존관계를 추가해야 한다.<br>
`implementation 'org.springframework.boot:spring-boot-starter-validation'`
### Jakarta Bean Validation
- `jakarta.validation-api` : Bean Validation 인터페이스
- `hibernate-validator` : 구현체

## 검증 애노테이션
- `@NotBlank` : 빈값 + 공백만 있는 경우를 허용하지 않는다.
- `@NotNull` : `null`을 허용하지 않는다.
- `@Range(min = 1000, max = 1000000)` : 범위 안의 값이어야 한다.
- `@Max(9999)` : 최대 9999까지만 허용한다.

### 참고
- `javax.validation.contraints.NotNull`
- `org.hibernate.validator.contraints.Range`
- `javax.validation`으로 시작하면 특정 구현에 관계없이 제공되는 표준 인터페이스이고,
- `org.hibernate.validator`로 시작하면 하이버네이트 validator 구현체를 사용할 때만 제공되는 검증 기능이다.
- 실무에서는 대부분 하이버네이트 validator를 사용하므로 자유롭게 사용해도 된다.

### 검증기 생성
- 다음 코드와 같이 검증기를 생성한다.
- 스프링과 통합하면 우리가 직접 이런 코드를 작성하지는 않으므로, 참고정도만 함.
```java
ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
Validator validator = factory.getValidator();
```

### 검증 실행
- 검증 대상 (`item`)을 직접 검증기에 넣고 그 결과를 받는다. `Set`에는 `ConstraintViolation`이라는 검증 오류가 담긴다.
- 따라서 결과가 비어있으면 검증 오류가 없는 것이다.<br>
`Set<ConstraintViolation<Item>> violations = validator.validate(item);`
- `ConstraintViolation` 출력 결과를 보면, 검증 오류가 발생한 객체, 필드, 메시지 정보 등 다양한 정보를 확인할 수 있다.

### 정리
- 스프링은 이미 개발자를 위해 빈 검증기를 스프링에 완전히 통합해두었다.
