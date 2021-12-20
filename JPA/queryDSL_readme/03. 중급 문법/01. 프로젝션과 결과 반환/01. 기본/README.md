# 프로젝션과 결과 반환 - 기본
- 프로젝션 : select 대상 지정

## 프로젝션 대상이 하나
```java
List<String> result = queryFactory
        .select(member.username)
        .from(member)
        .fetch();
```
- 프로젝션 대상이 하나면 타입을 명확하게 지정할 수 있음
- 프로젝션 대상이 둘 이상이면 튜플이나 DTO로 조회

## 튜플 조회
- 프로젝션 대상이 둘 이상일 때 사용
- com.querydsl.core.Tuple
```java
List<Tuple> result = queryFactory
    .select(member.username, member.age)
    .from(member)
    .fetch();
for (Tuple tuple : result) {
    String username = tuple.get(member.username);
    Integer age = tuple.get(member.age);
    System.out.println("username=" + username);
    System.out.println("age=" + age);
}
```

