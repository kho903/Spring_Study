# 기본 Q-Type 활용
## Q클래스 인스턴스를 사용하는 2가지 방법
```java
QMember qMember = new QMember("m"); //별칭 직접 지정
QMember qMember = QMember.member; //기본 인스턴스 사용
```
## 기본 인스턴스를 static import와 함께 사용
```java
import static study.querydsl.entity.QMember.*;
@Test
public void startQuerydsl3() {
    //member1을 찾아라.
    Member findMember = queryFactory
                .select(member)
                .from(member)
                .where(member.username.eq("member1"))
                .fetchOne();
    assertThat(findMember.getUsername()).isEqualTo("member1");
}
```
- 다음 설정을 추가하면 실행되는 JPQL을 볼 수 있다.
```groovy
spring.jpa.properties.hibernate.use_sql_comments : true
```
> 참고: 같은 테이블을 조인해야 하는 경우가 아니면 기본 인스턴스를 사용하자.
