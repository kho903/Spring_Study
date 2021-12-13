# 간단한 주문 조회 V4 : JPA에서 DTO로 바로 조회
## OrderSimpleApiController - 추가
```java
private final OrderSimpleQueryRepository orderSimpleQueryRepository; //의존 관계 주입
/**
 * V4. JPA에서 DTO로 바로 조회
 * - 쿼리 1번 호출
 * - select 절에서 원하는 데이터만 선택해서 조회
 */
@GetMapping("/api/v4/simple-orders")
public List<OrderSimpleQueryDto> ordersV4() {
    return orderSimpleQueryRepository.findOrderDtos();
}
```
## OrderSimpleQueryRepository 조회 전용 리포지토리
```java
package jpabook.jpashop.repository.order.simplequery;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrderSimpleQueryRepository {
    private final EntityManager em;

    public List<OrderSimpleQueryDto> findOrderDtos() {
        return em.createQuery(
                "select new
                jpabook.jpashop.repository.order.simplequery.OrderSimpleQueryDto(o.id, m.name,
                        o.orderDate, o.status, d.address)" +
                " from Order o" +
                        " join o.member m" +
                        " join o.delivery d", OrderSimpleQueryDto.class)
                .getResultList();
    }
}
```
## OrderSimpleQueryDto 리포지토리에서 DTO 직접 조회
```java
package jpabook.jpashop.repository.order.simplequery;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.OrderStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OrderSimpleQueryDto {
    private Long orderId;
    private String name;
    private LocalDateTime orderDate; //주문시간
    private OrderStatus orderStatus;
    private Address address;

    public OrderSimpleQueryDto(Long orderId, String name, LocalDateTime
            orderDate, OrderStatus orderStatus, Address address) {
        this.orderId = orderId;
        this.name = name;
        this.orderDate = orderDate;
        this.orderStatus = orderStatus;
        this.address = address;
    }
}
```
- 일반적인 SQL을 사용할 때 처럼 원하는 값을 선택해서 조회
- `new` 명령어를 사용해서 JPQL의 결과를 DTO로 즉시 변환
- SELECT 절에서 원하는 데이터를 직접 선택하므로 DB -> 애플리케이션 네트웍 용량 최적화 (생각보다 미비)
- 리포지토리 재사용성 떨어짐. API 스펙에 맞춘 코드가 리포지토리에 들어가는 단점

## 정리
- 엔티티를 DTO로 변환하거나, DTO를 바로 조회하는 두 가지 방법은 각각 장단점이 있다.
- 둘 중 상황에 따라서 더 나은 방법을 선택하면 된다.
- 엔티티로 조회하면 리포지토리 재사용성도 좋고, 개발도 단순해진다.
- 따라서 권장하는 방법은 다음과 같다.

### 쿼리 방식 선택 권장 순서
1. 우선 엔티티를 DTO로 변환하는 방법을 선택한다.
2. 필용하면 페치 조인으로 성능을 최적화한다. -> 대부분의 성능 이슈가 해결된다.
3. 그래도 안되면 DTO로 직접 조회하는 방법을 사용한다.
4. 최후의 방법은 JPA가 제공하는 네이티브 SQL이나 스프링 JDBC Template을
사용해서 SQL을 직접 사용한다.
