package hellojpa;

import org.hibernate.Hibernate;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

public class JpaMain {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        tx.begin();

        // code
        try {
            Member member1 = new Member();
            member1.setUsername("member1");
            em.persist(member1);

            /*Member member2 = new Member();
            member2.setUsername("member2");
            em.persist(member2);*/

            em.flush();
            em.clear();
/*
//            Member findMember = em.find(Member.class, member.getId());
            Member findMember = em.getReference(Member.class, member.getId());
//            System.out.println("findMember = " + findMember.getClass());
//            System.out.println("findMember.id = " + findMember.getId());
            System.out.println("findMember.username = " + findMember.getUsername());

            Member m1 = em.find(Member.class, member1.getId());
            Member m2 = em.getReference(Member.class, member2.getId());
//            System.out.println("m1 == m2 : " + (m1.getClass() == m2.getClass())); // false
            System.out.println("m1 instance of Member : " + (m1 instanceof Member)); // false
            System.out.println("m2 instance of Member : " + (m2 instanceof Member)); // false
*/
            /*Member m1 = em.find(Member.class, member1.getId());
            System.out.println("m1 = " + m1.getClass());
            Member reference = em.getReference(Member.class, member1.getId());
            System.out.println("reference = " + reference.getClass());
            System.out.println("(m1 == reference) = " + (m1 == reference));*/

            /*Member refMember = em.getReference(Member.class, member1.getId());
            System.out.println("refMember = " + refMember);
            Member findMember = em.find(Member.class, member1.getId());
            System.out.println("findMember = " + findMember);
            System.out.println("(refMember == findMember) = " + (refMember == findMember));*/

            Member refMember = em.getReference(Member.class, member1.getId());
            System.out.println("refMember = " + refMember.getClass()); // Proxy
            System.out.println("isLoaded = " + emf.getPersistenceUnitUtil().isLoaded(refMember));
//            em.detach(refMember);
//            em.close();
//            em.clear();
//            refMember.getUsername(); // 강제 초기화 // LazyInitializationException
            Hibernate.initialize(refMember); // 강제 초기화 메소드
            System.out.println("isLoaded = " + emf.getPersistenceUnitUtil().isLoaded(refMember));



            tx.commit(); // DB에 저장되는 때!, [트랜잭션] 커밋!
        } catch (Exception e) {
            tx.rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
        emf.close();
    }
}
