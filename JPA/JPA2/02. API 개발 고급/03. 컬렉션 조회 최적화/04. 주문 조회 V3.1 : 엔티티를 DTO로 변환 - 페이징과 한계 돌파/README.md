# 주문 조회 V3.1 : 엔티티를 DTO로 변환 - 페이징과 한계 돌파
## 페이징과 한계 돌파
- 컬렉션을 페치 조인하면 페이징이 불가능하다.
    - 컬렉션을 페치 조인하면 일대다 조인이 발생하므로 데이터가 예측할 수 없이 증가한다.
    - 일대다에서 일(1)을 기준으로 페이징을 하는 것이 목적이다.
    그런데 데이터는 다(N)를 기준으로 row가 생성된다.
    - Order를 기준으로 페이징 하고 싶은데, 다(N)인 OrderItem을 조인하면
    OrderItem이 기준이 되어버린다.
- 이 경우 하이버네이트는 경고 로그를 남기고 모든 DB 데이터를 읽어서 메모리에서 페이징을 시도한다.
최악의 경우 장애로 이어질 수 있다.

### 한계 돌파
- 그러면 페이징 + 컬렉션 엔티티를 함께 조회하려면 어떻게 해야 할까?
- 먼저 ToOne(OneToOne, ManyToOne) 관계를 모두 페치조인 한다.
ToOne 관계는 row 수를 증가시키지 않으므로 페이징 쿼리에 영향을 주지 않는다.
- 컬렉션은 지연 로딩으로 조회한다.
- 지연 로딩 성능 최적화를 위해 `hibernate.default_batch_fetch_size`, `@BatchSize`를 적용한다.
    - hibernate.default_batch_fetch_size: 글로벌 설정
    - @BatchSize : 개별 최적화
    - 이 옵션을 사용하면 컬렉션이나, 프록시 객체를 한꺼번에 설정한 size만큼 IN 쿼리로 조회한다.

## OrderRepository에 추가
```java
public List<Order> findAllWithMemberDelivery(int offset, int limit) {
    return em.createQuery(
        "select o from Order o" +
            " join fetch o.member m" +
            " join fetch o.delivery d", Order.class)
        .setFirstResult(offset)
        .setMaxResults(limit)
        .getResultList();
}
```
## OrderApiController에 추가
```java
/**
 * V3.1 엔티티를 조회해서 DTO로 변환 페이징 고려
 * - ToOne 관계만 우선 모두 페치 조인으로 최적화
 * - 컬렉션 관계는 hibernate.default_batch_fetch_size, @BatchSize로 최적화
 */
@GetMapping("/api/v3.1/orders")
public List<OrderDto> ordersV3_page(@RequestParam(value = "offset", defaultValue = "0") int offset,
                                    @RequestParam(value = "limit", defaultValue = "100") int limit) {
        List<Order> orders = orderRepository.findAllWithMemberDelivery(offset, limit);
        List<OrderDto> result = orders.stream()
            .map(o -> new OrderDto(o))
            .collect(toList());
        return result;
}
```
## 최적화 옵션
```yaml
spring:
  jpa:
    properties:
      hibernate:
        default_batch_fetch_size: 1000
```
- 개별로 설정하려면 `@BatchSize`를 적용하면 된다. (컬렉션은 컬렉션 빌드에,
  엔티티는 엔티티 클래스에 적용)
- 장점
    - 쿼리 호출 수가 `1 + N` -> `1 + 1`로 최적화 된다.
    - 조인보다 DB 데이터 전송량이 최적화된다. (Order와 OrderItem을 조인하면 Order가
      OrderItem 만큼 중복해서 조회된다. 이 방법은 각각 조회하므로 전송해야할 중복 데이터가 없다.)
    - 페치 조인 방식과 비교해서 쿼리 호출 수가 약간 증가하지만, DB 데이터 전송량이 감소한다.
    - 컬렉션 페치 조인은 페이징이 불가능하지만 이 방법은 페이징이 가능하다.
- 결론
    - ToOne 관계는 페치 조인해도 페이징에 영향을 주지 않는다. 따라서 ToOne 관계는
    페치 조인으로 쿼리 수를 줄이고 해결하고, 나머지는 `hibernate.default_batch_fetch_size`로
    최적화 하자.

```text
참고 : default_batch_fetch_size의 크기는 적당한 사이즈를 골라야 하는데, 100 ~ 100 사이를 
선택하는 것을 권장한다. 이 전략은 SQL IN 절을 사용하는데, 데이터베이스에 따라 IN절 파라미터를
1000으로 제한하기도 한다. 1000으로 잡으면 한번에 1000개를 DB에서 애플리케이션에 불러오므로 DB에
순간 부하가 증가할 수 있다. 하지만 애플리케이션은 100이든 1000이든 결국 전체 데이터를 로딩해야
하므로 메모리 사용량이 같다. 1000으로 설정하는 것이 성능상 가장 좋지만, 결국 DB든 애플리케이션이든
순간 부하를 어디까지 견딜 수 있는 지를 결정하면 된다.
```
