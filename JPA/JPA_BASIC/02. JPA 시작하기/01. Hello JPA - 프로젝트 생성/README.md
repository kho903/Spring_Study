# Hello JPA
## 데이터베이스 방언
- JPA는 특정 데이터베이스에 종속 X
- 각각의 데이터베이스가 제공하는 SQL 문법과 함수는 조금씩 다름
    - 가변 문자 : MySQL은 VARCHAR, Oracle은 VARCHAR2
    - 문자열을 자르는 함수 : SQL 표준은 SUBSTRING(), ORACLE은 SUBSTR()
    - 페이징 : MySQL은 LIMIT, Oracle ROWNUM
- 방언 : SQL 표준을 지키지 않는 특정 데이터베이스만의 고유한 기능

### hibernate.dialect 속성에 지정
- H2 : org.hibernate.dialect.H2Dialect 
- Oracle 10g : org.hibernate.dialect.Oracle10gDialect 
- MySQL : org.hibernate.dialect.MySQL5InnoDBDialect 
- 하이버네이트는 40가지 이상의 데이터베이스 방언 지원
