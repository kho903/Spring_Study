# 실전 예제 3 - 다양한 연관관계 매핑
## 배송, 카테고리 추가 - 엔티티
- 주문과 배송은 1:1 (@OneToOne)
- 상품과 카테고리는 N:M (@ManyToMany)

## N:M 관계는 1:N, N:1 로
- 테이블의 N:M 관계는 중간 테이블을 이용해서 1:N, N:1
- 실전에서는 중간 테이블이 단순하지 않다.
- @ManyToMany는 제약 : 필드 추가X, 엔티티 테이블 불일치
- 실전에서는 @ManyToMany 사용 X

## @JoinColumn
외래 키를 매핑할 때 사용
- name : 매핑할 외래 키 이름
    - 기본 값 : 필드명 + _ + 참조하는 테이블의 기본 키 컬럼명
- referencedColumnName : 외래 키가 참조하는 대상 테이블의 컬럼명
    - 참조하는 테이블의 기본키 컬럼명
- foreignKey(DDL) : 외래 키 제약조건을 직접 지정할 수 있다.
이 속성은 테이블을 생성할 떄만 사용한다.
- unique, nullable, insertable, updatable, columnDefinition, table
    - @Column의 속성과 같다.

## @ManyToOne - 주요 속성
다대일 관계 매핑
- optional : false로 설정하면 연관된 엔티티가 항상 있어야 한다.
    - 기본값 : TRUE
- fetch : 글로벌 페치 전략을 설정한다.
    - 기본 값 : 
        - @ManyToOne=FetchType.EAGER
        - @OneToMany=FetchType.LAZY
- cascade : 영속성 전이 기능을 사용한다.
- targetEntity : 연관된 엔티티의 타입 정보를 설정한다. 이 기능은 거의 사용하지 않는다.
컬렉션을 사용해도 제네릭으로 타입 정보를 알 수 있다.

## @OneToMany - 주요 속성
다대일 관계 매핑
- mappedBy : 연관관계의 주인 필드를 선택한다.
- fetch : 글로벌 페치 전략을 설정한다.
    - 기본 값 :
        - @ManyToOne=FetchType.EAGER
        - @OneToMany=FetchType.LAZY
- cascade : 영속성 전이 기능을 사용한다.
- targetEntity : 연관된 엔티티의 타입 정보를 설정한다. 이 기능은 거의 사용하지 않는다.
  컬렉션을 사용해도 제네릭으로 타입 정보를 알 수 있다.
