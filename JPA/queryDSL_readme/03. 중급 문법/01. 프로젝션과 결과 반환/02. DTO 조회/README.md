# DTO 조회
## 순수 JPA에서 DTO 조회
### MemberDto
```java
package study.querydsl.dto;

import lombok.Data;

@Data
public class MemberDto {
    private String username;
    private int age;

    public MemberDto() {
    }

    public MemberDto(String username, int age) {
        this.username = username;
        this.age = age;
    }
}
```
### 순수 JPA에서 DTO 조회 코드
```java
List<MemberDto> result = em.createQuery(
    "select new study.querydsl.dto.MemberDto(m.username, m.age) " +
        "from Member m", MemberDto.class)
    .getResultList();
```
- 순수 JPA에서 DTO를 조회할 때는 new 명령어를 사용해야 함
- DTO의 package 이름을 다 적어줘야 해서 지저분함
- 생성자 방식만 지원함

## Querydsl 빈 생성 (Bean population)
결과를 DTO 반환할 때 사용한다. 다음 3가지 방법 지원
- 프로퍼티 접근
- 필드 직접 접근
- 생성자 사용
### 프로퍼티 접근 - Setter
```java
List<MemberDto> result = queryFactory
        .select(Projections.bean(MemberDto.class,
            member.username,
            member.age))
        .from(member)
        .fetch();
```
### 필드 직접 접근
```java
List<MemberDto> result = queryFactory
    .select(Projections.fields(MemberDto.class,
        member.username,
        member.age))
    .from(member)
    .fetch();
```

### 별칭이 다를 때
```java
package study.querydsl.dto;

import lombok.Data;

@Data
public class UserDto {
    private String name;
    private int age;
}
```
```java
List<UserDto> fetch = queryFactory
    .select(Projections.fields(UserDto.class,
            member.username.as("name"),
                ExpressionUtils.as(
                    JPAExpressions
                        .select(memberSub.age.max())
                        .from(memberSub), "age")
        )
    ).from(member)
    .fetch();
```
- 프로퍼티나 필드 접근 생성 방식에서 이름이 다를 때 해결 방안
- `ExpressinUtils.as(source, alias)` : 필드나 서브쿼리에 별칭 적용
- `username.as("memberName")` : 필드에 별칭 적용

### 생성자 사용
```java
List<MemberDto> result = queryFactory
    .select(Projections.constructor(MemberDto.class,
        member.username,
        member.age))
    .from(member)
    .fetch();
}
```