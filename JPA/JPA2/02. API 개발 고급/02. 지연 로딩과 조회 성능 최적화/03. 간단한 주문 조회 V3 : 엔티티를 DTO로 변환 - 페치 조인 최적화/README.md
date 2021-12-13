# 간단한 주문 조회 V3 : 엔티티를 DTO로 변환 - 페치 조인 최적화
## OrderSimpleApiController - 추가
```java
/**
 * V3. 엔티티를 조회해서 DTO로 변환(fetch join 사용O)
 * - fetch join으로 쿼리 1번 호출
 * 참고: fetch join에 대한 자세한 내용은 JPA 기본편 참고(정말 중요함)
 */
@GetMapping("/api/v3/simple-orders")
public List<SimpleOrderDto> ordersV3() {
    List<Order> orders = orderRepository.findAllWithMemberDelivery();
    List<SimpleOrderDto> result = orders.stream()
        .map(o -> new SimpleOrderDto(o))
        .collect(toList());
    return result;
}
```
## OrderRepository - 추가 코드
```java
public List<Order> findAllWithMemberDelivery() {
    return em.createQuery(
            "select o from Order o" +
                    " join fetch o.member m" +
                    " join fetch o.delivery d", Order.class)
            .getResultList();
}
```
- 엔티티를 페치 조인 (fetch join)을 사용해서 쿼리 1번에 조회
- 페치 조인으로 `order -> member`, `order -> delivery`는
이미 조회 된 상태이므로 지연로딩 X
