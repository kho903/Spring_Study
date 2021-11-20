# 주문 검색 기능 개발
## 검색 조건 파라미터 OrderSearch
```java
package jpabook.jpashop.domain;

public class OrderSearch {
    private String memberName; //회원 이름
    private OrderStatus orderStatus;//주문 상태[ORDER, CANCEL]
    //Getter, Setter
}
```

## 검색을 추가한 주문 리포지토리 코드
```java
package jpabook.jpashop.repository;

@Repository
public class OrderRepository {
    @PersistenceContext
    EntityManager em;

    public void save(Order order) {
        em.persist(order);
    }

    public Order findOne(Long id) {
        return em.find(Order.class, id);
    }

    public List<Order> findAll(OrderSearch orderSearch) {
        //... 검색 로직
    }
}
```
- findAll(OrderSearch orderSearch) 메서드는 검색조건에 동적으로 쿼리를 생성해서
주문 엔티티를 조회한다.
  
## JPQL로 처리
```java
public List<Order> findAllByString(OrderSearch orderSearch) {
     //language=JPAQL
     String jpql = "select o From Order o join o.member m";
     boolean isFirstCondition = true;
     //주문 상태 검색
     if (orderSearch.getOrderStatus() != null) {
         if (isFirstCondition) {
             jpql += " where";
             isFirstCondition = false;
         } else {
             jpql += " and";
         }
         jpql += " o.status = :status";
     }
     //회원 이름 검색
     if (StringUtils.hasText(orderSearch.getMemberName())) {
         if (isFirstCondition) {
             jpql += " where";
             isFirstCondition = false;
         } else {
             jpql += " and";
         }
         jpql += " m.name like :name";
     }
     TypedQuery<Order> query = em.createQuery(jpql, Order.class)
     .setMaxResults(1000); //최대 1000건
     if (orderSearch.getOrderStatus() != null) {
         query = query.setParameter("status", orderSearch.getOrderStatus());
     }
     if (StringUtils.hasText(orderSearch.getMemberName())) {
         query = query.setParameter("name", orderSearch.getMemberName());
     }
     return query.getResultList();
}
```
- JPQL 쿼리를 문자로 생성하기는 번거롭고, 실수로 인한 버그가 충분히 발생할 수 있다.

## JPA Criteria로 처리
```java
public List<Order> findAllByCriteria(OrderSearch orderSearch) {
    CriteriaBuilder cb = em.getCriteriaBuilder();
    CriteriaQuery<Order> cq = cb.createQuery(Order.class);
    Root<Order> o = cq.from(Order.class);
    Join<Order, Member> m = o.join("member", JoinType.INNER); //회원과 조인
    List<Predicate> criteria = new ArrayList<>();
        
    //주문 상태 검색
    if (orderSearch.getOrderStatus() != null) {
        Predicate status = cb.equal(o.get("status"),
        orderSearch.getOrderStatus());
        criteria.add(status);
    }
        
    //회원 이름 검색
    if (StringUtils.hasText(orderSearch.getMemberName())) {
        Predicate name =
        cb.like(m.<String>get("name"), "%" +
        orderSearch.getMemberName() + "%");
        criteria.add(name);
    }
    cq.where(cb.and(criteria.toArray(new Predicate[criteria.size()])));
    TypedQuery<Order> query = em.createQuery(cq).setMaxResults(1000); //최대1000건
        
    return query.getResultList();
}
```
- JPA Criteria는 JPA 표준 스펙이지만 실무에서 사용하기에 너무 복잡하다. 결국 다른 대안이 필요하다. 
- 많은 개발자가 비슷한 고민을 했지만, 가장 멋진 해결책은 Querydsl이 제시했다. 
