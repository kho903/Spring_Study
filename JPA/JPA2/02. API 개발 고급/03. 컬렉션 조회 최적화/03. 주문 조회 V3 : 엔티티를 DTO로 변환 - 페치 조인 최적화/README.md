# 주문 조회 V3 : 엔티티를 DTO로 변환 - 페치 조인 최적화
## OrderApiController에 추가
```java
@GetMapping("/api/v3/orders")
public List<OrderDto> ordersV3() {
    List<Order> orders = orderRepository.findAllWithItem();
    List<OrderDto> result = orders.stream()
        .map(o -> new OrderDto(o))
        .collect(toList());
    return result;
}
```
## OrderRepository에 추가
```java
public List<Order> findAllWithItem() {
    return em.createQuery(
        "select distinct o from Order o" +
            " join fetch o.member m" +
            " join fetch o.delivery d" +
            " join fetch o.orderItems oi" +
            " join fetch oi.item i", Order.class)
        .getResultList();
}
```
- 페치 조인으로 SQL이 1번만 실행됨
- `distinct`를 사용한 이유는 1대 다 조인이 있으므로 데이터베이스 row가 증가한다. 그 결과 같은
order 엔티티의 조회 수도 증가하게 된다. JPA의 distinct는 SQL에 distinct를 추가하고, 더해서
같은 엔티티가 조회되면, 애플리케이션에서 중복을 걸러준다. 이 예에서 order가 컬렉션 페치 조인 때문에
중복 조회 되는 것을 막아준다.
- 단점
    - 페이징 불가능
> 참고 1 : 컬렉션 페치 조인을 사용하면 페이징이 불가능하다. 하이버네이트는 경고 로그를 남기면서 모든
> 데이터를 DB에서 읽어오고, 메모리에서 페이징 해버린다. (매우 위험하다.)

> 참고 2 : 컬렉션 페치 조인은 1개만 사용할 수 있다. 컬렉션 둘 이사에 페치 조인을 사용하면 안된다.
> 데이터가 부정합하게 조회 될 수 있다.
