package com.example.jpa.board.service;

import com.example.jpa.board.entity.Board;
import com.example.jpa.board.entity.BoardBadReport;
import com.example.jpa.board.entity.BoardComment;
import com.example.jpa.board.entity.BoardType;
import com.example.jpa.board.model.BoardBadReportInput;
import com.example.jpa.board.model.BoardInput;
import com.example.jpa.board.model.BoardPeriod;
import com.example.jpa.board.model.BoardReplyInput;
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

    // 게시글의 게시기간을 설정
    ServiceResult setBoardPeriod(Long id, BoardPeriod boardPeriod);

    // 게시글의 조회수를 증가
    ServiceResult setBoardHits(Long id, String email);

    // 게시글에 좋아요 하기
    ServiceResult setBoardLike(Long id, String email);

    // 게시글의 좋아요를 취소함
    ServiceResult setBoardUnLike(Long id, String email);

    // 게시글을 신고하는 기능
    ServiceResult addBadReport(Long id, String email, BoardBadReportInput boardBadReportInput);

    // 신고된 게시글 정보 목록
    List<BoardBadReport> badReportList();

    // 게시글을 스크랩함
    ServiceResult scrapBoard(Long id, String email);

    // 스크랩 삭제
    ServiceResult removeScrap(Long id, String email);

    // 북마크 추가
    ServiceResult addBookmark(Long id, String email);

    // 북마크 제거
    ServiceResult removeBookmark(Long id, String email);

    // 내가 작성한 게시글 리턴
    List<Board> postList(String email);

    // 내가 작성한 코멘트 목록 리턴
    List<BoardComment> commentList(String email);

    Board detail(Long id);

    // 게시글 목록
    List<Board> list();

    // 게시글 작성
    ServiceResult add(String email, BoardInput boardInput);

    // 문의 게시판의 답변
    ServiceResult replyBoard(Long id, BoardReplyInput boardReplyInput);
}
