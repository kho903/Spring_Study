package jpql;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import java.util.Collection;
import java.util.List;

public class FetchJoinMain {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        tx.begin();

        // code
        try {
            Team teamA = new Team();
            teamA.setName("팀A");
            em.persist(teamA);

            Team teamB = new Team();
            teamB.setName("팀B");
            em.persist(teamB);

            Member member1 = new Member();
            member1.setUsername("회원1");
            member1.setTeam(teamA);
            em.persist(member1);

            Member member2 = new Member();
            member2.setUsername("회원2");
            member2.setTeam(teamA);
            em.persist(member2);

            Member member3 = new Member();
            member3.setUsername("회원3");
            member3.setTeam(teamB);
            em.persist(member3);

            em.flush();
            em.clear();

            /*String query = "select m from Member m";
            List<Member> result = em.createQuery(query, Member.class)
                    .getResultList();
            for (Member member : result) {
                System.out.println("member = " + member.getUsername() + ", " + member.getTeam().getName());
                // 회원1, 팀A(SQL)
                // 회원2, 팀A(1차 캐시)
                // 회원3, 팀B(SQL)
                // 회원 100명 -> N + 1
            }*/
            /*String query = "select m from Member m join fetch m.team";
            List<Member> result = em.createQuery(query, Member.class)
                    .getResultList();
            for (Member member : result) {
                System.out.println("member = " + member.getUsername() + ", " + member.getTeam().getName());
            }*/

            // 컬렉션 페치 조인
            /*String query = "select t from Team t join fetch t.members";
            List<Team> result = em.createQuery(query, Team.class)
                    .getResultList();
            for (Team team : result) {
                System.out.println("team = " + team.getName() + " | members = "+ team.getMembers().size());
                for (Member member : team.getMembers()) {
                    System.out.println(" ==> member = " + member);
                }
            }*/

            // 페치 조인 and DISTINCT
            /*String query = "select distinct t from Team t join fetch t.members m";
            List<Team> result = em.createQuery(query, Team.class)
                    .getResultList();
            for (Team team : result) {
                System.out.println("team = " + team.getName() + " | members = "+ team.getMembers().size());
                for (Member member : team.getMembers()) {
                    System.out.println(" ==> member = " + member);
                }
            }*/

            // 그냥 조인 - 쿼리가 출력을 찍을 때 마다 나옴
            /*String query = "select t from Team t join t.members m";
            List<Team> result = em.createQuery(query, Team.class)
                    .getResultList();
            for (Team team : result) {
                System.out.println("team = " + team.getName() + " | members = "+ team.getMembers().size());
                for (Member member : team.getMembers()) {
                    System.out.println(" ==> member = " + member);
                }
            }*/

            // @BatchSize(size = 100) 추가 후
            /*String query = "select t from Team t";
            List<Team> result = em.createQuery(query, Team.class)
                    .setFirstResult(0)
                    .setMaxResults(2)
                    .getResultList();
            for (Team team : result) {
                System.out.println("team = " + team.getName() + " | members = "+ team.getMembers().size());
                for (Member member : team.getMembers()) {
                    System.out.println(" ==> member = " + member);
                }
            }*/

//            String query = "select m from Member m where m = :member";
//            Member findMember = em.createQuery(query, Member.class)
//                    .setParameter("member", member1)
//                    .getSingleResult();
            /*String query = "select m from Member m where m.id = :memberId";
            Member findMember = em.createQuery(query, Member.class)
                    .setParameter("memberId", member1.getId())
                    .getSingleResult();
            System.out.println("findMember = " + findMember);*/

            String query = "select m from Member m where m.team = :team";
            List<Member> members = em.createQuery(query, Member.class)
                    .setParameter("team", teamA)
                    .getResultList();

            for (Member member : members) {
                System.out.println("member = " + member);
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
