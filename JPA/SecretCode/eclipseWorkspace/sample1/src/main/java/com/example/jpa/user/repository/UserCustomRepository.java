package com.example.jpa.user.repository;

import com.example.jpa.user.model.UserNoticeCount;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class UserCustomRepository {

    private final EntityManager em;

    public List<UserNoticeCount> findUserNoticeCount() {

        String sql = "select u.id, u.email, u.user_name, (select count(*) from Notice n where n.user_id = u.id) notice_count from User u";

        List<UserNoticeCount> list = em.createNativeQuery(sql).getResultList();
        return list;
    }

    public List<UserNoticeCount> findUserLogCount() {
        String sql = "select u.id, u.email, u.user_name, " +
                "(select count(*) from Notice n where n.user_id = u.id) notice_count, " +
                "(select count(*) from notice_like nl where nl.user_id = u.id) notice_like_count " +
                "from user u";

        List<UserNoticeCount> list = em.createNativeQuery(sql).getResultList();
        return list;
    }
}
