# 주문 조회 V5 : JPA에서 DTO 직접 조회 - 플랫 데이터 최적화
## OrderApiController에 추가
```java
@GetMapping("/api/v6/orders")
public List<OrderQueryDto> ordersV6() {
    List<OrderFlatDto> flats = orderQueryRepository.findAllByDto_flat();
        
    return flats.stream()
        .collect(groupingBy(o -> new OrderQueryDto(o.getOrderId(), o.getName(), o.getOrderDate(), o.getOrderStatus(), o.getAddress()),
            mapping(o -> new OrderItemQueryDto(o.getOrderId(), o.getItemName(), o.getOrderPrice(), o.getCount()), toList())
            )).entrySet().stream()
        .map(e -> new OrderQueryDto(e.getKey().getOrderId(), e.getKey().getName(), e.getKey().getOrderDate(), e.getKey().getOrderStatus(), e.getKey().getAddress(), e.getValue()))
        .collect(toList());
}
```
## OrderQueryDto에 생성자 추가
```java
public OrderQueryDto(Long orderId, String name, LocalDateTime orderDate, OrderStatus orderStatus, Address address, List<OrderItemQueryDto> orderItems) {
    this.orderId = orderId;
    this.name = name;
    this.orderDate = orderDate;
    this.orderStatus = orderStatus;
    this.address = address;
    this.orderItems = orderItems;
}
```
## OrderQueryRepository에 추가
```java
public List<OrderFlatDto> findAllByDto_flat() {
    return em.createQuery(
        "select new
            jpabook.jpashop.repository.order.query.OrderFlatDto(o.id, m.name, o.orderDate,
            o.status, d.address, i.name, oi.orderPrice, oi.count)" +
            " from Order o" +
            " join o.member m" +
            " join o.delivery d" +
            " join o.orderItems oi" +
            " join oi.item i", OrderFlatDto.class)
    .getResultList();
}
```
## OrderFlatDto
```java
package jpabook.jpashop.repository.order.query;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.OrderStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OrderFlatDto {
    private Long orderId;
    private String name;
    private LocalDateTime orderDate; //주문시간
    private Address address;
    private OrderStatus orderStatus;
    private String itemName;//상품 명
    private int orderPrice; //주문 가격
    private int count; //주문 수량

    public OrderFlatDto(Long orderId, String name, LocalDateTime orderDate,
                        OrderStatus orderStatus, Address address, String itemName, int orderPrice, int
                                count) {
        this.orderId = orderId;
        this.name = name;
        this.orderDate = orderDate;
        this.orderStatus = orderStatus;
        this.address = address;
        this.itemName = itemName;
        this.orderPrice = orderPrice;
        this.count = count;
    }
}
```
- Query : 1번
- 단점
    - 쿼리는 한번이지만 조인으로 인해 DB에서 애플리케이션에 전달하는 데이터에 중복 데이터가
    추가되므로 상황에 따라 V5보다 더 느릴 수도 있다.
    - 애플리케이션에서 추가 작업이 크다.
    - 페이징 불가능
