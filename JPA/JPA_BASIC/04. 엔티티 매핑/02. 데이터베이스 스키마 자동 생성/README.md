# 데이터베이스 스키마 자동 생성
- DDL을 애플리케이션 실행 시점에 자동 생성
- 테이블 중심 -> 객체 중심
- 데이터베이스 방언을 활용해서 데이터베이스에 맞는 적절한 DDL 생성
- 이렇게 생성된 DDL은 개발 장비에서만 사용
- 생성된 DDL은 운영서버에서는 사용하지 않거나, 적절히 다듬은 후에 사용

## 데이터베이스 스키마 자동 생성 - 속성
hibernate.hbm2ddl.auto
- create : 기존 테이블 삭제 후 다시 생성 (DROP + CREATE)
- create-drop : create와 같으나 종료 시점에 테이블 DROP
- update : 변경분만 반영 (운영DB에는 사용하면 안됨)
- validate : 엔티티와 테이블이 정상 매핑되었는지만 확인
- none : 사용하지 않음

## 데이터베이스 스키마 자동 생성 - 실습
- 스키마 자동 생성하기 설정
- 스키마 자동 생성하기 실행, 옵션별 확인
- 데이터베이스 방언 별로 달라지는 것 확인 (varchar)

## 데이터베이스 스키마 자동 생성 - 주의
- 운영 장비에는 절대 create, create-drop, update 사용하면 안된다.
- 개발 초기 단계는 create 또는 update
- 테스트 서버는 update 또는 validate
- 스테이징과 운영 서버는 validate 또는 none

## DDL 생성 기능
- 제약조건 추가 : 회원 이름은 필수, 10자 초과 X
    - @Column(nullable = false, length = 10)
- 유니크 제약조건 추가
    - @Table(uniqueConstraints = {
        name = "NAME_AGE_UNIQUE",
        columnNames = {"NAME","AGE"}
    })
- DDL 생성 기능은 DDL을 자동 생성할 때만 사용되고 JPA의
실행 로직에는 영향을 주지 않는다.
