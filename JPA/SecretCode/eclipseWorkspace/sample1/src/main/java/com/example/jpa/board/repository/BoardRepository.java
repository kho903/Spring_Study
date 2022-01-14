package com.example.jpa.board.repository;

import com.example.jpa.board.entity.Board;
import com.example.jpa.board.entity.BoardType;
import com.example.jpa.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BoardRepository extends JpaRepository<Board, Long> {
    Long countByBoardType(BoardType boardType);

    List<Board> findByUser(User user);
}
