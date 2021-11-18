# 주문 기능 테스트
## 테스트 요구사항
- 상품 주문이 성공해야 한다.
- 상품을 주문할 때 재고 수량을 초과하면 안 된다.
- 상품 취소가 성공해야 한다.

## 상품 주문 테스트 코드
```java
package jpabook.jpashop.service;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.exception.NotEnoughStockException;
import jpabook.jpashop.repository.OrderRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class OrderServiceTest {
    @PersistenceContext
    EntityManager em;
    @Autowired
    OrderService orderService;
    @Autowired
    OrderRepository orderRepository;

    @Test
    public void 상품주문() throws Exception {
        //Given
        Member member = createMember();
        Item item = createBook("시골 JPA", 10000, 10); //이름, 가격, 재고
        int orderCount = 2;
        //When
        Long orderId = orderService.order(member.getId(), item.getId(),
                orderCount);
        //Then
        Order getOrder = orderRepository.findOne(orderId);
        assertEquals("상품 주문시 상태는 ORDER", OrderStatus.ORDER,
                getOrder.getStatus());
        assertEquals("주문한 상품 종류 수가 정확해야 한다.", 1,
                getOrder.getOrderItems().size());
        assertEquals("주문 가격은 가격 * 수량이다.", 10000 * 2,
                getOrder.getTotalPrice());
        assertEquals("주문 수량만큼 재고가 줄어야 한다.", 8, item.getStockQuantity());
    }

    @Test(expected = NotEnoughStockException.class)
    public void 상품주문_재고수량초과() throws Exception {
        //...
    }

    @Test
    public void 주문취소() {
        //...
    }

    private Member createMember() {
        Member member = new Member();
        member.setName("회원1");
        member.setAddress(new Address("서울", "강가", "123-123"));
        em.persist(member);
        return member;
    }

    private Book createBook(String name, int price, int stockQuantity) {
        Book book = new Book();
        book.setName(name);
        book.setStockQuantity(stockQuantity);
        book.setPrice(price);
        em.persist(book);
        return book;
    }
}
```
- 상품 주문이 정상 동작하는 지 확인하는 테스트다. Given 절에서 테스트를 위한 회원과 상품을 만들고 
  When 절에서 실제 상품을 주문하고 Then 절에서 주문 가격이 올바른 지, 주문후 제고 수량이 정확히 줄었는 지 검증한다.

## 재고 수량 초과 테스트
- 재고 수량을 초과해서 상품을 주문해보자. 이때는 `NotEnoughStockException` 예외가 발생해야 한다.
```java
@Test(expected = NotEnoughStockException.class)
public void 상품주문_재고수량초과() throws Exception {
        
        //Given
        Member member=createMember();
        Item item=createBook("시골 JPA",10000,10); //이름, 가격, 재고
        int orderCount=11; //재고보다 많은 수량
        
        //When
        orderService.order(member.getId(),item.getId(),orderCount);
        
        //Then
        fail("재고 수량 부족 예외가 발생해야 한다.");
}
```
- 코드를 보면 재고는 10권인데 `orderCount = 11`로 재고보다 1권 더 많은 수량을 주문했다. 주문 초과로 다음 로직에서 예외가 발생한다.
```java
public abstract class Item {
    //...
    public void removeStock(int orderQuantity) {
        int restStock = this.stockQuantity - orderQuantity;
        if (restStock < 0) {
            throw new NotEnoughStockException("need more stock");
        }
        this.stockQuantity = restStock;
    }
}
```

## 주문 취소 테스트
- 주문을 취소하면 그만큼 재고가 증가해야 한다.
```java
@Test
public void 주문취소() {
    //Given
    Member member = createMember();
    Item item = createBook("시골 JPA", 10000, 10); //이름, 가격, 재고
    int orderCount = 2;
    
    Long orderId = orderService.order(member.getId(), item.getId(), orderCount);
        
    //When
    orderService.cancelOrder(orderId);
        
    //Then
    Order getOrder = orderRepository.findOne(orderId);
    assertEquals("주문 취소시 상태는 CANCEL 이다.",OrderStatus.CANCEL, getOrder.getStatus());
    assertEquals("주문이 취소된 상품은 그만큼 재고가 증가해야 한다.", 10, item.getStockQuantity());
}
```
- 주문을 취소하려면 먼저 주문을 해야 한다. Given 절에서 주문하고 When 절에서 해당 주문을 취소했다.
Then 절에서 주문 상태가 주문 취소 상태인지 (`CANCEL`), 취소한 만큼 재고가 증가했는지 검증한다.
