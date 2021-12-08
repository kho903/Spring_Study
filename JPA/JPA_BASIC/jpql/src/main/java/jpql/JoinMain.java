package jpql;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.util.List;

public class JoinMain {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        tx.begin();

        // code
        try {
            Team team = new Team();
            team.setName("teamA");
            em.persist(team);

            Member member = new Member();
            member.setUsername("member");
            member.setAge(10);
            member.setTeam(team);
            em.persist(member);


            em.flush();
            em.clear();

//            String query = "select m from Member m inner join m.team t"; // 내부 조인
//            String query = "select m from Member m left outer join m.team t"; // 외부 조인
//            String query = "select m from Member m, Team t where m.username = t.name"; // 세타 조인

            // ON 절
            // 1. 조인 대상 필터링
//            String query = "select m from Member m left join m.team t on t.name = 'teamA'";
            // 2. 연관관계 없는 엔티티 외부 조인
            String query = "SELECT m FROM Member m LEFT JOIN Team t on m.username = t.name";

            List<Member> resultList = em.createQuery(query, Member.class)
                    .getResultList();
            for (Member member1 : resultList) {
                System.out.println("member1 = " + member1.getUsername());
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
