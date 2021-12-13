# 간단한 주문 조회 V1 : 엔티티를 직접 노출
## OrderSimpleApiController
```java
package jpabook.jpashop.api;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.repository.*;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 *
 * xToOne(ManyToOne, OneToOne) 관계 최적화
 * Order
 * Order -> Member
 * Order -> Delivery
 *
 */
@RestController
@RequiredArgsConstructor
public class OrderSimpleApiController {
    private final OrderRepository orderRepository;

    /**
     * V1. 엔티티 직접 노출
     * - Hibernate5Module 모듈 등록, LAZY=null 처리
     * - 양방향 관계 문제 발생 -> @JsonIgnore
     */
    @GetMapping("/api/v1/simple-orders")
    public List<Order> ordersV1() {
        List<Order> all = orderRepository.findAllByString(new OrderSearch());
        for (Order order : all) {
            order.getMember().getName(); //Lazy 강제 초기화
            order.getDelivery().getAddress(); //Lazy 강제 초기환
        }
        return all;
    }
}
```
- 엔티티를 직접 노출하는 것은 좋지 않다.
- `order` -> `member` 와 `order` -> `address`는 지연 로딩이다.
따라서 실제 엔티티 대신에 프록시 존재
- jackson 라이브러리는 기본적으로 이 프록시 객체를 json으로 어떻게 생성해야 하는 지 모름 -> 예외 발셍
- `Hibernate5Module`을 스프링 빈으로 등록하면 해결 (스프링 부트 사용중)

## Hibernate5Module 등록
- `JpashopApplication`에 다음 코드를 추가하자.
```java
@Bean
Hibernate5Module hibernate5Module() {
    return new Hibernate5Module();
}
```
- 다음과 같이 설정하면 강제로 지연 로딩 가능
```java
@Bean
Hibernate5Module hibernate5Module() {
    Hibernate5Module hibernate5Module = new Hibernate5Module();
    //강제 지연 로딩 설정
    hibernate5Module.configure(Hibernate5Module.Feature.FORCE_LAZY_LOADING, true);
    return hibernate5Module;
}
```
- 이 옵션을 키면 `order -> member`, `member -> orders` 양방향 연관관계를 계속 로딩하게 된다.
따라서 `@JsonIgnore` 옵션을 한 곳에 주어야 한다.
> 주의 : 엔티티를 직접 노출할 때는 양방향 연관관계가 걸린 곳은 꼭! 한곳을
> `@JsonIgnore` 처리해야 한다. 안그러면 양쪽을 서로 호출하면서 무한 루프가 걸린다.

> 참고 : 앞에서 계속 강조했듯이 정말 간단한 애플리케이션이 아니면 엔티티를 API 응답으로
> 외부로 노출하는 것은 좋지 않다. 따라서 `Hibernate5Module`를 사용하기 보다는 
> DTO로 변환해서 반환하는 것이 더 좋은 방법이다.

> 주의 : 지연 로딩(LAZY)을 피하기 위해 즉시 로딩(EAGER)으로 설정하면 안된다! 즉시 로딩
> 때문에 연관관계가 필요없는 경우에도 데이터를 항상 조회해서 성능 문제가 발생할 수 있따.
> 즉시 로딩으로 설정하면 성능 튜닝이 매우 어려워진다.<br>
> 항상 지연 로딩을 기본으로 하고, 성능 최적화가 필요한 경우에는 페치 조인(fetch join)을
> 사용해라~!