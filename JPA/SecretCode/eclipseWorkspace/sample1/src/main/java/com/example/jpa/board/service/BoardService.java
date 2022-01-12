package com.example.jpa.board.service;

import com.example.jpa.board.entity.BoardType;
import com.example.jpa.board.model.BoardTypeCount;
import com.example.jpa.board.model.BoardTypeInput;
import com.example.jpa.board.model.BoardTypeUsing;
import com.example.jpa.board.model.ServiceResult;

import java.util.List;

public interface BoardService {

    ServiceResult addBoard(BoardTypeInput boardTypeInput);

    ServiceResult updateBoard(Long id, BoardTypeInput boardTypeInput);

    ServiceResult deleteBoard(Long id);

    List<BoardType> getAllBoardType();

    // 게시판 타입의 사용여부를 설정
    ServiceResult setBoardTypeUsing(Long id, BoardTypeUsing boardTypeUsing);

    // 게시판 타입의 게시글 수를 리턴
    List<BoardTypeCount> getBoardTypeCount();

    // 게시글을 최상단에 배치함
    ServiceResult setBoardTop(Long id, boolean topYn);
}
