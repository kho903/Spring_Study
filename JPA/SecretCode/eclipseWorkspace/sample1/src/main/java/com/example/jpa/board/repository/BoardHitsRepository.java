package com.example.jpa.board.repository;

import com.example.jpa.board.entity.Board;
import com.example.jpa.board.entity.BoardHits;
import com.example.jpa.board.entity.BoardType;
import com.example.jpa.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardHitsRepository extends JpaRepository<BoardHits, Long> {

    long countByBoardAndUser(Board board, User user);
}
