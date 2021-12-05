package hellojpa;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.util.List;

public class JpaMain {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        tx.begin();

        // code
        try {
            // 삽입
            /*Member member = new Member();
            member.setId(2L);
            member.setName("HelloB");
            em.persist(member);*/
            // 조회
            /*Member findMember = em.find(Member.class, 1L);
            System.out.println("findMember.getId() = " + findMember.getId());
            System.out.println("findMember.getName() = " + findMember.getName());*/
            // 삭제
            /*Member findMember = em.find(Member.class, 1L);
            em.remove(findMember);*/
            // 수정
            /*Member findMember = em.find(Member.class, 1L);
            findMember.setName("HelloA_Update");*/
            // JPQL 소개
            // 가장 단순한 조회 방법
//            List<Member> result = em.createQuery("select m from Member m", Member.class)
//                    .setFirstResult(1)
//                    .setMaxResults(10)
//                    .getResultList();
//            for (Member member : result) {
//                System.out.println("member.getName() = " + member.getName());
//            }


            // 비영속
            /*Member member = new Member();
            member.setId(101L);
            member.setName("Hello JPA");

            // 영속
            System.out.println("======BEFORE======");
            em.persist(member); // DB에 저장되는 때가 아님
            System.out.println("======AFTER======");

            Member findMember = em.find(Member.class, 101L);

            System.out.println("findMember.getId() = " + findMember.getId());
            System.out.println("findMember.getName() = " + findMember.getName());
*/
            /*Member findMember1 = em.find(Member.class, 101L);
            Member findMember2 = em.find(Member.class, 101L);

            // 영속 엔티티의 동일성 보장
            System.out.println("result = " + (findMember1 == findMember2));
*/
            /*Member member1 = new Member(150L, "A");
            Member member2 = new Member(160L, "B");

            em.persist(member1);
            em.persist(member2); // 여기까지 INSERT SQL을 데이터베이스에 보내지 않는다.*/


            // 엔티티 수정 - 변경 감지
            /*Member member = em.find(Member.class, 150L);
            member.setName("ZZZZZ");

//            em.persist(member);*/

            // 플러시
            /*Member member = new Member(200L, "member200");
            em.persist(member);

            em.flush(); // INSERT Query가 즉시 호출
            System.out.println("=============");*/

            // 준영속
            /*Member member = em.find(Member.class, 150L);
            member.setName("AAAAA");
//            em.detach(member); // 특정 엔티티만 준영속 상태로 전환
            em.clear(); // 영속성 컨텍스트를 완전히 초기화

            Member member2 = em.find(Member.class, 150L);
*/

            /*Member member = new Member();
            member.setId(3L);
            member.setUsername("C");
            member.setRoleType(RoleType.GUEST);

            em.persist(member);*/

            /*Member member = new Member();
//            member.setId("ID_A");
            member.setUsername("C");

            System.out.println("================");
            em.persist(member);
            System.out.println("member.getId() = " + member.getId());
            System.out.println("================");*/

            Member member1 = new Member();
            member1.setUsername("A");
            Member member2 = new Member();
            member2.setUsername("B");
            Member member3 = new Member();
            member3.setUsername("C");

            System.out.println("============");
            em.persist(member1);
            em.persist(member1);
            em.persist(member1);

            System.out.println("member1.getId() = " + member1.getId());
            System.out.println("member2.getId() = " + member2.getId());
            System.out.println("member3.getId() = " + member3.getId());
            System.out.println("============");

            tx.commit(); // DB에 저장되는 때!, [트랜잭션] 커밋!
        } catch (Exception e) {
            tx.rollback();
        } finally {
            em.close();
        }
        emf.close();
    }
}
