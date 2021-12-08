# 다형성 쿼리
## TYPE
- 조회 대상을 특정 자식으로 한정
- 예) Item 중에 Book, Movie를 조회해라
- [JPQL]                            <br>
  select i from Item i              <br>
  where type(i) IN (BOOK, Movie)    
- [SQL]                             <br>
  select i from i                   <br>
  where i.DTYPE in ('B', 'M')
  
## TREAT (JPA 2.1)
- 자바의 타입 캐스팅과 유사
- 상속 구조에서 부모 타입을 특정 자식 타입으로 다룰 때 사용
- FROM, WHERE, SELECT (하이버네이트 지원) 사용
- 예) 부모인 Item과 자식 Book이 있다.
- [JPQL]                            <br>
  select i from Item i              <br>
  where treat(i as Book).auther = 'kim'
- [SQL]                             <br>
  select i.* from Item i            <br>
  where i.DTYPE = 'B' and i.auther = 'kim'