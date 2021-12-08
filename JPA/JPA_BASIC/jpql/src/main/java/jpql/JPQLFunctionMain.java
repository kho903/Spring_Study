package jpql;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.OrderColumn;
import javax.persistence.Persistence;
import java.util.List;

public class JPQLFunctionMain {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        tx.begin();

        // code
        try {
//            Team team = new Team();
//            team.setName("teamA");
//            em.persist(team);

            Member member = new Member();
            member.setUsername("관리자");
            em.persist(member);

            Member member2 = new Member();
            member2.setUsername("관리자2");
            em.persist(member2);

            em.flush();
            em.clear();

//            String query = "select 'a' || 'b' from Member m";
//            String query = "select concat('a', 'b') from Member m";
//            String query = "select substring(m.username, 2, 3) from Member m";

//            String query = "select function('group_concat', m.username) from Member m";
            String query = "select group_concat(m.username) from Member m";

            List<String> resultList = em.createQuery(query, String.class)
                    .getResultList();

            for (String s : resultList) {
                System.out.println("s = " + s);
            }

//            String query = "select locate('cd', 'abcdefg') from Member m";
//            String query = "select size(t.members) from Team t";
//            String query = "select index(t.members) from Team t";
//            List<Integer> resultList = em.createQuery(query, Integer.class)
//                    .getResultList();
//            for (Integer s : resultList) {
//                System.out.println("s = " + s);
//            }

            tx.commit();
        } catch (Exception e) {
            tx.rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
        emf.close();
    }
}
