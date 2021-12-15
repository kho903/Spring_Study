# 공통 인터페이스 분석
- JpaRepository 인터페이스 : 공통 CRUD 제공
- 제네릭은 <엔티티 타입, 식별자 타입> 설정

## `JpaRepository` 공통 기능 인터페이스
```java
public interface JpaRepository<T, ID extends Serializable>
        extends PagingAndSortingRepository<T, ID> {
 ...
}
```
## `JpaRepository`를 사용하는 인터페이스
```java
public interface MemberRepository extends JpaRepository<Member, Long> {
}
```
## 공통 인터페이스 구성
- 스프링 데이터
```text
<<Interface>> Repository
^
|
<<Interface>> CrudRepository
    - save(S) : S
    - findOne(ID) : T
    - exists(ID) : boolean
    - count() : long
    - delete(T)
    ...
^
|
<<Interface>> PagingAndSortingRepository
    - findAll(Sort) : Iterable<T>
    - findAll(Pageable) : Page<T>
```
- 스프링 데이터 JPA
```text
^
|
<<Interface>> JpaRepository
    - findAll() : List<T>
    - findAll(Sort) : List<T>
    - findAll(Iterable<ID>) : List<T>
    - save(Iterable<S>) : List<S>
    - flush()
    - saveAndFlush(T) : T
    - deleteInBatch(Iterable<T>)
    - deleteAllInBatch()
    - getOne(ID) : T 
```
### 주의
- `T findOne(ID)` -> `Optional<T> findById(ID)` 변경
### 제네릭 타입
- T : 엔티티
- ID : 엔티티의 식별자 타입
- S : 엔티티와 그 자식 타입

### 주요 메서드
- save(S) : 새로운 엔티티는 저장하고 이미 있는 엔티티는 병합한다.
- delete(T) : 엔티티 하나를 삭제한다. 내부에서 `EntityManager.remove()` 호출
- findById(ID) : 엔티티 하나를 조회한다. 내부에서 `EntityManager.find()` 호출
- getOne(ID) : 엔티티를 프록시로 조회한다.  내부에서 `EntityManager.getReference()` 호출
- findAll(_) : 모든 엔티티를 조회한다. 정렬(`Sort`)나 페이징(`Pageable`) 조건을 파라미터로 제공할 수 있다.

> 참고 : `JpaRepository`는 대부분의 공통 메서드를 제공한다.
