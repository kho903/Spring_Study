# 순수 JPA 사용

- 우선 등록일, 수정일 적용

```java
package study.datajpa.entity;

@MappedSuperclass
@Getter
public class JpaBaseEntity {
    @Column(updatable = false)
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        createdDate = now;
        updatedDate = now;
    }

    @PreUpdate
    public void preUpdate() {
        updatedDate = LocalDateTime.now();
    }
}
```

```java
public class Member extends JpaBaseEntity {
}
```

## test 코드
```java
@Test
public void JpaEventBaseEntity() throws Exception {
    //given
    Member member = new Member("member1");
    memberRepository.save(member); //@PrePersist
    Thread.sleep(100);
    member.setUsername("member2");
    em.flush(); //@PreUpdate
    em.clear();
    
    //when
    Member findMember = memberRepository.findById(member.getId()).get();
        
    //then
    System.out.println("findMember.createdDate = " +
            findMember.getCreatedDate());
    System.out.println("findMember.updatedDate = " +
            findMember.getUpdatedDate());
}
```
### JPA 주요 이벤트 어노테이션
- @PrePersist, @PostPersist
- @PreUpdate, @PostUpdate
