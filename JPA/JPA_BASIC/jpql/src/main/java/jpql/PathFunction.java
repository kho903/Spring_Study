package jpql;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.util.Collection;
import java.util.List;

public class PathFunction {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        tx.begin();

        // code
        try {
            Team team = new Team();
            em.persist(team);

            Member member = new Member();
            member.setUsername("관리자");
            member.setTeam(team);
            em.persist(member);

            Member member2 = new Member();
            member2.setUsername("관리자2");
            member2.setTeam(team);
            em.persist(member2);



            em.flush();
            em.clear();

            // 상태 필드
//            String query = "select m.username from Member m";
            // 단일 값 연관 경로 - 묵시적 내부 조인 발생, 탐색 O
//            String query = "select m.team from Member m";
            // 컬렉션 값 연관 경로 - 묵시적 내부 조인 발생, 탐색 X
            /*String query = "select t.members From Team t";
            Collection resultList = em.createQuery(query, Collection.class)
                    .getResultList();
            System.out.println("resultList = " + resultList);*/
//            for (Object o : resultList) {
//                System.out.println("o = " + o);
//            }

            /*String query = "select t.members.size From Team t";
            Integer result = em.createQuery(query, Integer.class)
                    .getSingleResult();
            System.out.println("result = " + result);*/

            // 컬렉션 값 연관 경로 - 묵시적 내부 조인 발생, 탐색 X -> 별칭으로 탐색
            String query = "select m.username From Team t join t.members m";
            List<String> resultList = em.createQuery(query, String.class)
                    .getResultList();
//            System.out.println("resultList = " + resultList);
            for (String s : resultList) {
                System.out.println("s = " + s);
            }


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
