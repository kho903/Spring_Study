# 주문 조회 V5 : JPA에서 DTO 직접 조회 - 컬렉션 조회 최적화
## OrderApiController에 추가
```java
@GetMapping("/api/v5/orders")
public List<OrderQueryDto> ordersV5() {
    return orderQueryRepository.findAllByDto_optimization();
}
```
## OrderQueryRepository에 추가
```java
/**
 * 최적화
 * Query: 루트 1번, 컬렉션 1번
 * 데이터를 한꺼번에 처리할 때 많이 사용하는 방식
 *
 */
public List<OrderQueryDto> findAllByDto_optimization() {
    //루트 조회(toOne 코드를 모두 한번에 조회)
    List<OrderQueryDto> result = findOrders();
        
    //orderItem 컬렉션을 MAP 한방에 조회
    Map<Long, List<OrderItemQueryDto>> orderItemMap =
            findOrderItemMap(toOrderIds(result));
        
    //루프를 돌면서 컬렉션 추가(추가 쿼리 실행X)
    result.forEach(o -> o.setOrderItems(orderItemMap.get(o.getOrderId())));
    return result;
}
private List<Long> toOrderIds(List<OrderQueryDto> result) {
    return result.stream()
                .map(o -> o.getOrderId())
                .collect(Collectors.toList());
}
private Map<Long, List<OrderItemQueryDto>> findOrderItemMap(List<Long> orderIds) {
 List<OrderItemQueryDto> orderItems = em.createQuery(
              "select new jpabook.jpashop.repository.order.query.OrderItemQueryDto(oi.order.id, i.name, oi.orderPrice, oi.count)" +
                " from OrderItem oi" +
                " join oi.item i" +
              " where oi.order.id in :orderIds", OrderItemQueryDto.class)
        .setParameter("orderIds", orderIds)
        .getResultList();
 
    return orderItems.stream()
        .collect(Collectors.groupingBy(OrderItemQueryDto::getOrderId));
}
```
- Query : 루트 1번, 컬렉션 1번
- ToOne 관계들을 먼저 조회하고, 여기서 얻은 식별자 orderId로 ToMany 관계인 `OrderItem`을 한꺼번에 조회
- MAP을 사용해서 매칭 성능 향상 (O(1))
