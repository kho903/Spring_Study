package com.example.jpa.board.repository;

import com.example.jpa.board.entity.BoardComment;
import com.example.jpa.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BoardCommentRepository extends JpaRepository<BoardComment, Long> {

    List<BoardComment> findByUser(User user);
}
