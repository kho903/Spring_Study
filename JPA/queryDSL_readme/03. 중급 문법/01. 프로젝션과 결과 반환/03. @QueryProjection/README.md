# @QueryProjection
## 생성자 + @QueryProjection
```java
package study.querydsl.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;

@Data
public class MemberDto {
    private String username;
    private int age;

    public MemberDto() {
    }

    @QueryProjection
    public MemberDto(String username, int age) {
        this.username = username;
        this.age = age;
    }
}
```
- `./gradlew compileQuerydsl`
- `QMemberDto` 생성 확인

## @QueryProjection 활용
```java
List<MemberDto> result = queryFactory
    .select(new QMemberDto(member.username, member.age))
        .from(member)
        .fetch();
```
- 이 방법은 컴파일러로 타입을 체크할 수 있으므로 가장 안전한 방법이다. 다만 DTO에 QueryDSL
어노테이션을 유지해야 하는 점과 DTO까지 Q파일을 생성해야 하는 단점이 있다.
  
## distinct
```java
List<String> result = queryFactory
    .select(member.username).distinct()
    .from(member)
    .fetch()
```
- distinct는 JPQL의 distinct와 같다.
