# Bean Validation
- 검증 기능을 매번 코드로 작성하는 것은 상당히 번거롭다.
- 특히 특정 필드에 대한 검증 로직은 대부분 빈 값이 아닌지, 특정 크기를 넘는지 아닌지와 같이 매우 일반적인 로직이다.
```java
public class Item {
    private Long id;
    @NotBlank
    private String itemName;
    
    @NotNull
    @Range(min = 1000, max = 1000000)
    private Integer price;
    
    @NotNull
    @Max(9999)
    private Integer quantity;
    //...
}
```
- 이런 검증 로직을 모든 프로젝트에 적용할 수 있게 공통화하고, 표준화한 것이 바로 Bean Validation이다.
- Bean Validation을 잘 활용하면, 애노테이션 하나로 검증 로직을 매우 편리하게 적용할 수 있다.

## Bean Validation 이란?
- 먼저 Bean Validation은 특정한 구현체가 아니라 Bean Validation 2.0(JSR-380) 이라는 기술 표준이다.
- 쉽게 이야기해서 검증 애노테이션과 여러 인터페이스의 모음이다. (마치 JPA 표준 기술 -> 구현체 하이버네이트가 있는 것과 같다.)
- Bean Validation을 구현한 기술중에 일반적으로 사용하는 구현체는 하이버네이트 Validator이다.
