# 기본 키 매핑
## 기본 키 매핑 어노테이션
- @Id
- @GeneratedValue
```java
@Id @GeneratedValue(stategy = GenerationType.AUTO)
private Long id;
```
## 기본 키 매핑 방법
- 직접 할당 : @Id만 사용
- 자동 생성 (@GeneratedValue)
    - IDENTITY : 데이터베이스에 위임, MYSQL
    - SEQUENCE : 데이터베이스 시퀀스 오브젝트 사용, ORACLE
        - @SequenceGenerator 필요
    - TABLE : 키 생성용 테이블 사용, 모든 DB에서 사용
        - @TableGenerator 필요
    - AUTO : 방언에 따라 자동 지정, 기본값
## 직접 할당
- @Id 사용

## IDENTITY 전략 - 특징
- 기본 키 생성을 데이터베이스에 위임
- 주로 MySQL, PostgreSQL, SQL Server, DB2에서 사용
    - 예 : MySQL의 AUTO_INCREMENT
- JPA는 보통 트랜잭션 커밋 시점에 INSERT SQL 실행
- AUTO_INCREMENT는 데이터베이스에 INSERT SQL을 실행한 이후에 ID 값을 알 수 있음
- IDENTITY 전략은 em.persist() 시점에 즉시 INSERT SQL 실행하고 DB에서 식별자를 조회

## IDENTITY 전략 - 매핑
```java

@Entity
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
}
```
## SEQUENCE 전략 - 특징
- 데이터베이스 시퀀스는 유일한 값을 순서대로 생성하는 특별한 오브젝트 (예 : 오라클 시퀀스 )
- 오라클, PostgreSQL, DB2, H2 데이터베이스에서 사용

## ## SEQUENCE 전략 - 매핑
```java
@Entity
@SequenceGenerator(
        name = "MEMBER_SEQ_GENERATOR",
        sequenceName = "MEMBER_SEQ", // 매핑할 데이터베이스 시퀀스 이름
        initialValue = 1, allocationSize = 1)
public class Member {
    
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
            generator = "MEMBER_SEQ_GENERATOR")
    private Long id;
}
```

## SEQUENCE - @SequenceGenerator
주의 : allocationSize 기본값 = 50
- name : 식별자 생성기 이름
    - 기본값 : 필수
- sequenceName : 데이터베이스에 등록되어 있는 시퀀스 이름
    - 기본값 : hibernate_sequence
- initialValue : DDL 생성 시에만 사용됨, 시퀀스 DDL을 생성할 때 처음 1 시작하는 수를 지정한다.
    - 기본값 : 1
- allocationSize : 시퀀스 한 번 호출에 증가하는 수 (성능 최적화에 사용됨),
  데이터베이스 시퀀스 값이 하나씩 증가하도록 설정되어 있으면 이 값을 반드시 1로 설정해야 한다.
    - 기본값 : 50
- catalog, schema : 데이터베이스 catalog, schema 이름

## TABLE 전략
- 키 생성 전용 테이블을 하나 만들어서 데이터베이스 시퀀스를 흉내내는 전략
- 장점 : 모든 데이터베이스에 적용 가능
- 단점 : 성능

## TABLE 전략 - 매핑
```sql
create table MY_SEQUENCES (
    sequence_name varchar(255) not null,
    next_val bigint,
    primary key ( sequence_name )
)
```
```java
@Entity
@TableGenerator(
        name = "MEMBER_SEQ_GENERATOR",
        table = "MY_SEQUENCES",
        pkColumnValue = "MEMBER_SEQ", allocationSize = 1)
public class Member {
    @Id
    @GeneratedValue(stategy = GenerationType.TABLE,
                    generator = "MEMBER_SEQ_GENERATOR")
    private Long id;
}
```

## @TableGenerator - 속성
- name : 식별자 생성기 이름
    - 기본값 : 필수
- table : 키생성 테이블명
    - 기본값 : hibernate_sequences
- pkColumnName : 시퀀스 컬럼명
    - 기본값 : sequence_name
- valueColumnNa : 시퀀스 값 컬럼명
    - 기본값 : next_val
- pkColumnValue : 키로 사용할 값 이름
    - 기본값 : 엔티티 이름
- initialValue : 초기값, 마지막으로 생성된 값이 기준이다.
    - 기본값: 0
- allocationSize : 시퀀스 한 번 호출에 증가하는 수 (성능 최적화에 사용됨)
    - 기본값 : 50
- catalog, schema : 데이터베이스 catalog, schema 이름
- uniqueConstraints(DDL) : 유니크 제약 조건을 지정할 수 있다.

## 권장하는 식별자 전략
- 기본 키 제약 조건 : null 아님, 유일, 변하면 안된다.
- 미래까지 이 조건을 만족하는 자연키를 찾기 어렵다. 대리키 (대체키)를 사용하자.
- 예를 들어 주민등록번호도 기본키로 적절하지 않다.
- 권장 : Long 형 + 대체키 + 키 생성전략 사용