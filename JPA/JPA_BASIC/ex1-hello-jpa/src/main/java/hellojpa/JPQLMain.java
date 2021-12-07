package hellojpa;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

public class JPQLMain {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        tx.begin();

        // code
        try {
            /*List<Member> result = em.createQuery(
                    "select m from Member m where m.username like '%kim%'",
                    Member.class
            ).getResultList();

            for (Member member : result) {
                System.out.println("member = " + member);
            }*/

            // Criteria - 실무에서 짜기 힘듦
            // Criteria 사용 준비
            /*CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Member> query = cb.createQuery(Member.class);

            Root<Member> m = query.from(Member.class);
            CriteriaQuery<Member> cq = query.select(m).where(cb.equal(m.get("username"), "kim"));
            List<Member> resultList = em.createQuery(cq)
                    .getResultList();*/

            // 네이티브 SQL
            Member member = new Member();
            member.setUsername("member1");
            em.persist(member);

            // flush -> commit, query 날릴 때

            // jdbc, mybatis 등을 직접 사용하게 될 때 사용 시점에 강제로 플러시 필요
//            em.flush();

            List<Member> resultList = em.createNativeQuery("select Member_ID, city, street, zipcode, USERNAME from Member", Member.class)
                    .getResultList();

            for (Member member1 : resultList) {
                System.out.println("member1 = " + member1);
            }

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
