# 동적 쿼리 - Where 다중 파라미터 사용
```java
@Test
public void 동적쿼리_WhereParam() throws Exception {
    String usernameParam = "member1";
    Integer ageParam = 10;
    List<Member> result = searchMember2(usernameParam, ageParam);
    Assertions.assertThat(result.size()).isEqualTo(1);
}
private List<Member> searchMember2(String usernameCond, Integer ageCond) {
    return queryFactory
        .selectFrom(member)
        .where(usernameEq(usernameCond), ageEq(ageCond))
        .fetch();
}
private BooleanExpression usernameEq(String usernameCond) {
    return usernameCond != null ? member.username.eq(usernameCond) : null;
}

private BooleanExpression ageEq(Integer ageCond) {
    return ageCond != null ? member.age.eq(ageCond) : null;
}
```
- `where` 조건에 `null` 값은 무시된다.
- 메서드를 다른 쿼리에서도 재활용할 수 있다.
- 쿼리 자체의 가독성이 높아진다.

### 조합 가능
```java
private BooleanExpression allEq(String usernameCond, Integer ageCond) {
    return usernameEq(usernameCond).and(ageEq(ageCond));
}
```
- `null` 체크는 주의해서 처리해야 함
