# 스프링 데이터 JPA 사용
## 설정
- `@EnableJpaAuditing` -> 스프링 부트 설정 클래스에 적용해야 함
- `@EntityListeners(AuditingEntityListener.class` -> 엔티티에 적용

## 사용 어노테이션
- `@CreatedDate`
- `@LastModifiedDate`
- `@CreatedBy`
- `@LastModifiedBy`

## 스프링 데이터 Auditing 적용 - 등록일, 수정일
```java
package study.datajpa.entity;

@EntityListeners(AuditingEntityListener.class)
@MappedSuperclass
@Getter
public class BaseEntity {
    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdDate;
    @LastModifiedDate
    private LocalDateTime lastModifiedDate;
}
```
## 스프링 데이터 Auditing 적용 - 등록자, 수정자
```java
package jpabook.jpashop.domain;

@EntityListeners(AuditingEntityListener.class)
@MappedSuperclass
public class BaseEntity {
    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdDate;
    
    @LastModifiedDate
    private LocalDateTime lastModifiedDate;
    
    @CreatedBy
    @Column(updatable = false)
    private String createdBy;
    
    @LastModifiedBy
    private String lastModifiedBy;
}
```
- 등록자, 수정자를 처리해주는 `AuditorAware` 스프링 빈 등록
```java
@Bean
public AuditorAware<String> auditorProvider() {
    return () -> Optional.of(UUID.randomUUID().toString());
}
```
- 실무에서는 세션 정보나, 스프링 시큐리티 로그인 정보에서 ID를 받음
> 참고 : 실무에서 대부분의 엔티티는 등록시간, 수정시간이 필요하지만, 등록자, 수정자는 없을 수도
> 있다. 그래서 다음과 같이 Base 타입을 분리하고, 원하는 타입을 선택해서 상속한다.

```java
public class BaseTimeEntity {
 @CreatedDate
 @Column(updatable = false)
 private LocalDateTime createdDate;
 @LastModifiedDate
 private LocalDateTime lastModifiedDate;
}
```
```java
public class BaseEntity extends BaseTimeEntity {
 @CreatedBy
 @Column(updatable = false)
 private String createdBy;
 @LastModifiedBy
 private String lastModifiedBy;
}
```
> 참고 : 저장시점에 등록일, 등록자는 물론이고, 수정일, 수정자도 같은 데이터가 저장된다.
> 데이터가 중복으로 저장되는 것 같지만, 이렇게 해두면 변경 컬럼만 확인해도 마지막에 업데이트한
> 유저를 확인할 수 있으므로 유지보수 관점에서 편리하다. 이렇게 하지 않으면 변경 컬럼이 `null`일 때 
> 등록 컬럼을 또 찾아야 한다. 참고로 저장시점에 저장데이터만 입력하고 싶으면 
> `@EnableJpaAuditing(modifyOnCreate = false)` 옵션을 사용하면 된다.

### 전체 적용 - 권장 X
- `@EntityListeners(AuditingEntityListener.class)`를 생략하고 스프링 데이터 JPA가
제공하는 이벤트를 엔티티 전체에 적용하려면 orm.xml에 다음과 같이 등록하면 된다.
- `META-INF/orm.xml`
```xml
<?xml version="1.0" encoding="UTF-8"?>
<entity-mappings xmlns="http://xmlns.jcp.org/xml/ns/persistence/orm"
        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
        xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/persistence/orm 
            http://xmlns.jcp.org/xml/ns/persistence/orm_2_2.xsd"
        version="2.2">
<persistence-unit-metadata>
<persistence-unit-defaults>
    <entity-listeners>
        <entity-listener
                class="org.springframework.data.jpa.domain.support.AuditingEntityListener"/>
 </entity-listeners>
 </persistence-unit-defaults>
 </persistence-unit-metadata>

</entity-mappings>
```
