package hellojpa;

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
            Member member = new Member();
            member.setUsername("member1");
            member.setHomeAddress(new Address("homeCity", "street", "10000"));

            member.getFavoriteFoods().add("치킨");
            member.getFavoriteFoods().add("피자");
            member.getFavoriteFoods().add("족발");

            member.getAddressHistory().add(new Address("old1", "street", "10000"));
            member.getAddressHistory().add(new Address("old2", "street", "10000"));

            em.persist(member);

            em.flush();
            em.clear();

//            System.out.println("============== START =================="); // 조회
//            Member findMember = em.find(Member.class, member.getId());
//
//            List<Address> addressHistory = findMember.getAddressHistory();
//            for (Address address : addressHistory) {
//                System.out.println("address = " + address.getCity());
//            }
//
//            Set<String> favoriteFoods = findMember.getFavoriteFoods();
//            for (String favoriteFood : favoriteFoods) {
//                System.out.println("favoriteFood = " + favoriteFood);
//            }

            System.out.println("============== START =================="); // 수정
            Member findMember = em.find(Member.class, member.getId());

////            findMember.getHomeAddress().setCity("new City"); // side effect 우려
//            Address a = findMember.getHomeAddress();
//            findMember.setHomeAddress(new Address("new City", a.getStreet(), a.getZipcode())); // 새로 넣을 것!

//            // 치킨 -> 한식 (String은 update X -> remove 후 add)
//            findMember.getFavoriteFoods().remove("치킨");
//            findMember.getFavoriteFoods().add("한식");

            // remove 후 add (equals method로 같은 Address를 new 하여 그것과 같은 address를 지워주는 방식)
            findMember.getAddressHistory().remove(new Address("old1", "street", "10000"));
            findMember.getAddressHistory().add(new Address("newCity1", "street", "10000"));

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
