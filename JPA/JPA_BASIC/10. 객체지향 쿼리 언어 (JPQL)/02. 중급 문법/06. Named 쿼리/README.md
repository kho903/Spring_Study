# Named 쿼리 - 정적 쿼리
- 미리 정의해서 이름을 부여해두고 사용하는 JPQL
- 정적 쿼리
- 어노테이션, XML에 정의
- 애플리케이션 로딩 시점에 초기화 후 재사용
- 애플리케이션 로딩 시점에 쿼리를 검증

## Named 쿼리 - 어노테이션
```java
@Entity
@NamedQuery(
        name = "Member.findByUsername",
        query = "select m from Member m where m.username = :username"
)
public class Member {
    ...
}
```
```java
List<Member> resultList =
    em.createNamedQuery("Member.findByUsername", Member.class)
      .setParameter("username", "회원1")
      .getResultList();
```

## Named 쿼리 - XML에 정의
- META-INF / persistence.xml
```xml
<persistence-unit name="jpabook" >
 <mapping-file>META-INF/ormMember.xml</mapping-file>
```
- META-INF / ormMember.xml
```xml
<?xml version="1.0" encoding="UTF-8"?>
<entity-mappings xmlns="http://xmlns.jcp.org/xml/ns/persistence/orm" version="2.1">
    <named-query name="Member.findByUsername">
        <query><![CDATA[
                 select m
                 from Member m
                 where m.username = :username
                 ]]>
        </query>
    </named-query>
    <named-query name="Member.count">
        <query>select count(m) from Member m</query>
    </named-query>
</entity-mappings>
```

## Named 쿼리 환경에 따른 설정
- XML이 항상 우선권을 가진다.
- 애플리케이션 운영 환경에 따라 다른 XML을 배포할 수 있다.
