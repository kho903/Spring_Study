# 스프링 데이터 JPA 페이징과 정렬
## 페이징과 정렬 파라미터
- `org.springframework.data.domain.Sort` : 정렬 기능
- `org.springframework.data.domain.Pageable` : 페이징 기능 (내부에 `Sort` 포함)

## 특별한 반환 타입
- `org.springframework.data.domain.Page` : 추가 count 쿼리 결과를 포함하는 페이징
- `org.springframework.data.domain.Slice` : 추가 count 쿼리 없이 다음 페이지만 확인 가능
  (내부적으로 limit + 1 조회)
- `List` (자바 컬렉션) : 추가 count 쿼리 없이 결과만 반환

## 페이징과 정렬 사용 예제
```java
Page<Member> findByUsername(String name, Pageable pageable); //count 쿼리 사용
Slice<Member> findByUsername(String name, Pageable pageable); //count 쿼리 사용안함
List<Member> findByUsername(String name, Pageable pageable); //count 쿼리 사용안함
List<Member> findByUsername(String name, Sort sort);
```

다음 조건으로 페이징과 정렬을 사용하는 예제 코드를 보자
- 검색 조건 : 나이가 10살
- 정렬 조건 : 이름으로 내림차순
- 페이징 조건 : 첫 번째 페이지, 페이지당 보여줄 데이터는 3건

## Page 사용 예제 정의 코드
```java
public interface MemberRepository extends Repository<Member, Long> {
    Page<Member> findByAge(int age, Pageable pageable);
}
```

## Page 사용 예제 실행 코드
```java
//페이징 조건과 정렬 조건 설정
@Test
public void page() throws Exception {
    //given
    memberRepository.save(new Member("member1", 10));
    memberRepository.save(new Member("member2", 10));
    memberRepository.save(new Member("member3", 10));
    memberRepository.save(new Member("member4", 10));
    memberRepository.save(new Member("member5", 10));
        
    //when
    PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username"));
    Page<Member> page = memberRepository.findByAge(10, pageRequest);
        
    //then
    List<Member> content = page.getContent(); //조회된 데이터
    assertThat(content.size()).isEqualTo(3); //조회된 데이터 수
    assertThat(page.getTotalElements()).isEqualTo(5); //전체 데이터 수
    assertThat(page.getNumber()).isEqualTo(0); //페이지 번호
    assertThat(page.getTotalPages()).isEqualTo(2); //전체 페이지 번호
    assertThat(page.isFirst()).isTrue(); //첫번째 항목인가?
    assertThat(page.hasNext()).isTrue(); //다음 페이지가 있는가?
}
```
- 두 번째 파라미터로 받은 `Pageable`은 인터페이스다. 따라서 실제 사용할 때는 해당 인터페이스를
구현한 `org.springframework.data.domain.PageRequest` 객체를 사용한다.
- `PageRequest` 생성자의 첫 번째 파라미터에는 현재 페이지를, 두 번째 파라미터에는 조회할 데이터 수를
입력한다. 여기에 추가로 정렬 정보도 파라미터로 사용할 수 있다. 참고로 페이지는 0부터 시작한다.

## Page 인터페이스
```java
public interface Page<T> extends Slice<T> {
    int getTotalPages(); //전체 페이지 수

    long getTotalElements(); //전체 데이터 수

    <U> Page<U> map(Function<? super T, ? extends U> converter); //변환기
}
```
## Slice 인터페이스
```java
public interface Slice<T> extends Streamable<T> {
    int getNumber(); //현재 페이지

    int getSize(); //페이지 크기

    int getNumberOfElements(); //현재 페이지에 나올 데이터 수

    List<T> getContent(); //조회된 데이터

    boolean hasContent(); //조회된 데이터 존재 여부

    Sort getSort(); //정렬 정보

    boolean isFirst(); //현재 페이지가 첫 페이지 인지 여부

    boolean isLast(); //현재 페이지가 마지막 페이지 인지 여부

    boolean hasNext(); //다음 페이지 여부

    boolean hasPrevious(); //이전 페이지 여부

    Pageable getPageable(); //페이지 요청 정보

    Pageable nextPageable(); //다음 페이지 객체

    Pageable previousPageable();//이전 페이지 객체

    <U> Slice<U> map(Function<? super T, ? extends U> converter); //변환기
}
```
### 참고 : count 쿼리를 다음과 같이 분리할 수 있음
```java
@Query(value = "select m from Member m",
 countQuery = "select count(m.username) from Member m")
Page<Member> findMemberAllCountBy(Pageable pageable);
```
### Top, First 사용 참고
- https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories.limit-query-result

### 실습
- Page
- Slice (count X) 추가로 limit + 1을 조회한다. 그래서 다음 페이지 여부 확인 (최근 모바일 리스트 생각해보면 됨)
- List (count X)
- 카운트 쿼리 분리 (이건 복잡한 sql에서 사용, 데이터는 left join, 카운트는 left join 안해도 됨)
    - 실무에서 매우 중요
> 참고 : 전체 count 쿼리는 매우 무겁다.

