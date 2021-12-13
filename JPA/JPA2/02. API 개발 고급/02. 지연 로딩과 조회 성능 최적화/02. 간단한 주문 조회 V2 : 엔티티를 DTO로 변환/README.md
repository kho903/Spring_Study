# 간단한 주문 조회 V2 : 엔티티를 DTO로 변환
## OrderSimpleApiController - 추가
```java
/**
 * V2. 엔티티를 조회해서 DTO로 변환(fetch join 사용X)
 * - 단점: 지연로딩으로 쿼리 N번 호출
 */
@GetMapping("/api/v2/simple-orders")
public List<SimpleOrderDto> ordersV2() {
    List<Order> orders=orderRepository.findAll();
    
    List<SimpleOrderDto> result = orders.stream()
            .map(o->new SimpleOrderDto(o))
            .collect(toList());
        
    return result;
}

@Data
static class SimpleOrderDto {
    private Long orderId;
    private String name;
    private LocalDateTime orderDate; //주문시간
    private OrderStatus orderStatus;
    private Address address;

    public SimpleOrderDto(Order order) {
        orderId = order.getId();
        name = order.getMember().getName();
        orderDate = order.getOrderDate();
        orderStatus = order.getStatus();
        address = order.getDelivery().getAddress();
    }
}
```
- 엔티티를 DTO로 변환하는 일반적인 방법이다.
- 쿼리가 총 1 + N + N 번 실행된다. (v1과 쿼리수 결과는 같다.)
    - `order` 조회 1번 (order 조회 결과 수가 N이 된다.)
    - `order -> member` 지연 로딩 조회 N번
    - `order -> delivery` 지연 로딩 조회 N번
    - 에) order의 결과가 4개면 최악의 경우 1 + 4 + 4 번 실행된다.
        - 지연로딩은 영속성 컨텍스트에서 조회하므로, 이미 조회된 경우 쿼리를 생략한다.
