# 필드와 컬럼 매핑
## 요구사항 추가
1. 회원은 일반 회원과 관리자로 구분해야 한다.
2. 회원 가입일과 수정일이 있어야 한다.
3. 회원을 설명할 수 있는 필드가 있어야 한다. 이 필드는 길이 제한이 없다.
```java
package hellojpa;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
public class Member {
    @Id
    private Long id;
    
    @Column(name = "name")
    private String username;
    
    private Integer age;
    
    @Enumerated(EnumType.STRING)
    private RoleType roleType;
    
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;
    
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastModifiedDate;
    
    @Lob
    private String description;
    //Getter, Setter…
}
```

## 매핑 어노테이션 정리
hibernate.hbm2ddl.auto
- @Column : 컬럼 매핑
- @Temporal : 날짜 타입 매핑
- @Enumerated : enum 타입 매핑
- @Lob : BLOB, CLOB 매핑
- @Transient : 특정 필드를 컬럼에 매핑하지 않음 (매핑 무시)

### @Column
- name : 필드와 매핑할 테이블의 컬럼 이름
    - 기본값 : 객체의 필드 이름
- insertable, updatable : 등록, 병경 가능 여부
    - 기본값 : TRUE
- nullable(DDL) : null 값의 허용 여부를 설정한다. false로 설정하면 DDL 생성 시에
not null 제약조건이 붙는다.
- unique(DDL) : @Table의 uniqueConstraints와 같지만 한 컬럼에 간단히 
  유니크 제약 조건을 걸 때 사용한다.
- columnDefinition(DDL) : 데이터베이스 컬럼 정보를 직접 줄 수 있다.
    - ex) varchar(100) default 'EMPTY'
    - 기본값 : 필드의 자바 타입과 방언 정보를 사용해서 적절한 컬럼 타입
- length(DDL) : 문자길이 제약 조건, String 타입에만 사용한다.
    - 기본값 : 255
- precision, scale(DDL) : BigDecimal 타입에서 사용한다. (BigInteger도 사용할 수 있다.)
    - precision은 소수점을 포함한 전체 자릿수를, scale은 소수의 자릿수다.
    - 참고로 double, float 타입에는 적용되지 않는다. 아주 큰 숫자나 정밀한 소수를 다루어야 할 때만 사용한다.
    - 기본값 : precision=19, scale=2

## @Enumerated
자바 enum 타입을 매핑할 때 사용, 주의 : ORDINAL 사용 X
- value
    - EnumType.ORDINAL : enum 순서를 데이터베이스에 저장
    - EnumType.String : enum 이름을 데이터베이스에 저장
    - 기본값 : EnumType.ORDINAL    

## @Temporal
날짜 타입 (java.util.Date, java.util.Calendar)을 매핑할 때 사용<br>
참고 : LocalDate, LocalDateTime을 사용할 때는 생략 가능 (최신 하이버네이트 지원)
- value
    - TemporalType.DATE : 날짜, 데이터베이스 date 타입과 매핑 (예: 2013-10-11)
    - TemporalType.TIME : 시간, 데이터베이스 time 타입과 매핑 (예: 11:11:11)
    - TemporalType.TIMESTAMP : 날짜와 시간, 데이터베이스 timestamp 타입과 매핑
      (예 : 2013-10-11 11:11:11)

## @Lob
데이터베이스 BLOB, CLOB 타입과 매핑
- @Lob에는 지정할 수 있는 속성이 없다.
- 매핑하는 필드 타입이 문자면 CLOB 매핑, 나머지는 BLOB 매핑
    - CLOB : String, char[], java.sql.CLOB
    - BLOB : byte[], java.sql.BLOB

## @Transient
- 필드 매핑 X
- 데이터베이스에 저장 X, 조회 X
- 주로 메모리상에서만 임시로 어떤 값을 보관하고 싶을 때 사용
```java
@Transient
private Integer temp;
```
