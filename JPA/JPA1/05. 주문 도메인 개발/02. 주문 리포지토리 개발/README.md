# 주문 리포지토리 개발
## 주문 리포지토리 코드
```java
package jpabook.jpashop.repository;

import jpabook.jpashop.domain.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;

@Repository
@RequiredArgsConstructor
public class OrderRepository {
    private final EntityManager em;

    public void save(Order order) {
        em.persist(order);
    }

    public Order findOne(Long id) {
        return em.find(Order.class, id);
    }
    // public List<Order> findAll(OrderSearch orderSearch) { ... }
}
```
- 주문 리포지토리에는 주문 엔티티를 저장하고 검색하는 기능이 있다.
