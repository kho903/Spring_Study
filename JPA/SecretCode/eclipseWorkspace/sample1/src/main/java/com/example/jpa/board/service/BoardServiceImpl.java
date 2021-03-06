package com.example.jpa.board.service;

import com.example.jpa.board.entity.Board;
import com.example.jpa.board.entity.BoardBadReport;
import com.example.jpa.board.entity.BoardBookmark;
import com.example.jpa.board.entity.BoardComment;
import com.example.jpa.board.entity.BoardHits;
import com.example.jpa.board.entity.BoardLike;
import com.example.jpa.board.entity.BoardScrap;
import com.example.jpa.board.entity.BoardType;
import com.example.jpa.board.model.BoardBadReportInput;
import com.example.jpa.board.model.BoardInput;
import com.example.jpa.board.model.BoardPeriod;
import com.example.jpa.board.model.BoardReplyInput;
import com.example.jpa.board.model.BoardTypeCount;
import com.example.jpa.board.model.BoardTypeCustomRepository;
import com.example.jpa.board.model.BoardTypeInput;
import com.example.jpa.board.model.BoardTypeUsing;
import com.example.jpa.board.model.ServiceResult;
import com.example.jpa.board.repository.BoardBadReportRepository;
import com.example.jpa.board.repository.BoardBookmarkRepository;
import com.example.jpa.board.repository.BoardCommentRepository;
import com.example.jpa.board.repository.BoardHitsRepository;
import com.example.jpa.board.repository.BoardLikeRepository;
import com.example.jpa.board.repository.BoardRepository;
import com.example.jpa.board.repository.BoardScrapRepository;
import com.example.jpa.board.repository.BoardTypeRepository;
import com.example.jpa.common.MailComponent;
import com.example.jpa.common.exception.BizException;
import com.example.jpa.common.model.ResponseResult;
import com.example.jpa.mail.entity.MailTemplate;
import com.example.jpa.mail.repository.MailTemplateRepository;
import com.example.jpa.user.entity.User;
import com.example.jpa.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BoardServiceImpl implements BoardService {

    private final BoardTypeRepository boardTypeRepository;
    private final BoardRepository boardRepository;
    private final BoardTypeCustomRepository boardTypeCustomRepository;
    private final BoardHitsRepository boardHitsRepository;
    private final BoardLikeRepository boardLikeRepository;
    private final BoardBadReportRepository boardBadReportRepository;
    private final BoardScrapRepository boardScrapRepository;
    private final BoardBookmarkRepository boardBookmarkRepository;
    private final BoardCommentRepository boardCommentRepository;

    private final UserRepository userRepository;
    private final MailTemplateRepository mailTemplateRepository;
    private final MailComponent mailComponent;

    @Override
    public ServiceResult addBoard(BoardTypeInput boardTypeInput) {
        BoardType boardType = boardTypeRepository.findByBoardName(boardTypeInput.getName());
        if (boardType != null && boardTypeInput.getName().equals(boardType.getBoardName())) {
            // ????????? ????????? ????????? ??????
            return ServiceResult.fail("?????? ????????? ???????????? ???????????????.");
        }

        BoardType addBoardType = BoardType.builder()
                .boardName(boardTypeInput.getName())
                .regDate(LocalDateTime.now())
                .build();

        boardTypeRepository.save(addBoardType);

        return ServiceResult.success();
    }

    @Override
    public ServiceResult updateBoard(Long id, BoardTypeInput boardTypeInput) {
        Optional<BoardType> optionalBoardType = boardTypeRepository.findById(id);
        if (!optionalBoardType.isPresent()) {
            return ServiceResult.fail("????????? ????????? ????????? ????????????.");
        }

        BoardType boardType = optionalBoardType.get();

        if (boardType.getBoardName().equals(boardTypeInput.getName())) {
            return ServiceResult.fail("????????? ????????? ????????? ???????????? ?????????.");
        }

        boardType.setBoardName(boardTypeInput.getName());
        boardType.setUpdateDate(LocalDateTime.now());
        boardTypeRepository.save(boardType);

        return ServiceResult.success();
    }

    @Override
    public ServiceResult deleteBoard(Long id) {
        Optional<BoardType> optionalBoardType = boardTypeRepository.findById(id);
        if (!optionalBoardType.isPresent()) {
            return ServiceResult.fail("????????? ?????????????????? ????????????.");
        }
        BoardType boardType = optionalBoardType.get();
        if (boardRepository.countByBoardType(boardType) > 0) {
            return ServiceResult.fail("????????? ?????????????????? ???????????? ???????????????.");
        }

        boardTypeRepository.delete(boardType);
        return ServiceResult.success();
    }

    @Override
    public List<BoardType> getAllBoardType() {
        return boardTypeRepository.findAll();
    }

    @Override
    public ServiceResult setBoardTypeUsing(Long id, BoardTypeUsing boardTypeUsing) {
        Optional<BoardType> optionalBoardType = boardTypeRepository.findById(id);
        if (!optionalBoardType.isPresent()) {
            return ServiceResult.fail("?????????????????? ????????????.");
        }
        BoardType boardType = optionalBoardType.get();

        boardType.setUsingYn(boardTypeUsing.isUsingYn());
        boardTypeRepository.save(boardType);

        return ServiceResult.success();
    }

    @Override
    public List<BoardTypeCount> getBoardTypeCount() {

        return boardTypeCustomRepository.getBoardTypeCount();
    }

    @Override
    public ServiceResult setBoardTop(Long id, boolean topYn) {
        Optional<Board> optionalBoard = boardRepository.findById(id);
        if (!optionalBoard.isPresent()) {
            return ServiceResult.fail("???????????? ???????????? ????????????.");
        }

        Board board = optionalBoard.get();
        if (board.isTopYn() == topYn) {
            if (topYn)
                return ServiceResult.fail("?????? ???????????? ???????????? ???????????? ????????????.");
            else
                return ServiceResult.fail("?????? ???????????? ???????????? ???????????? ????????????.");
        }
        board.setTopYn(topYn);
        boardRepository.save(board);

        return ServiceResult.success();
    }

    @Override
    public ServiceResult setBoardPeriod(Long id, BoardPeriod boardPeriod) {
        Optional<Board> optionalBoard = boardRepository.findById(id);
        if (!optionalBoard.isPresent()) {
            return ServiceResult.fail("???????????? ???????????? ????????????.");
        }
        Board board = optionalBoard.get();

        board.setPublishStartDate(boardPeriod.getStartDate());
        board.setPublishEndDate(boardPeriod.getEndDate());
        boardRepository.save(board);

        return ServiceResult.success();
    }

    @Override
    public ServiceResult setBoardHits(Long id, String email) {
        Optional<Board> optionalBoard = boardRepository.findById(id);
        if (!optionalBoard.isPresent()) {
            return ServiceResult.fail("???????????? ???????????? ????????????.");
        }
        Board board = optionalBoard.get();

        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (!optionalUser.isPresent()) {
            return ServiceResult.fail("??????????????? ???????????? ????????????.");
        }
        User user = optionalUser.get();

        if (boardHitsRepository.countByBoardAndUser(board, user) > 0) {
            return ServiceResult.fail("?????? ???????????? ????????????.");
        }

        boardHitsRepository.save(BoardHits.builder()
                .board(board)
                .user(user)
                .regDate(LocalDateTime.now())
                .build());
        return ServiceResult.success();
    }

    @Override
    public ServiceResult setBoardLike(Long id, String email) {
        Optional<Board> optionalBoard = boardRepository.findById(id);
        if (!optionalBoard.isPresent()) {
            return ServiceResult.fail("???????????? ???????????? ????????????.");
        }
        Board board = optionalBoard.get();

        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (!optionalUser.isPresent()) {
            return ServiceResult.fail("??????????????? ???????????? ????????????.");
        }
        User user = optionalUser.get();

        Long boardLikeCount = boardLikeRepository.countByBoardAndUser(board, user);
        if (boardLikeCount > 0) {
            return ServiceResult.fail("?????? ???????????? ????????? ????????????.");
        }

        boardLikeRepository.save(BoardLike.builder()
                .board(board)
                .user(user)
                .regDate(LocalDateTime.now())
                .build());
        return ServiceResult.success();
    }

    @Override
    public ServiceResult setBoardUnLike(Long id, String email) {
        Optional<Board> optionalBoard = boardRepository.findById(id);
        if (!optionalBoard.isPresent()) {
            return ServiceResult.fail("???????????? ???????????? ????????????.");
        }
        Board board = optionalBoard.get();

        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (!optionalUser.isPresent()) {
            return ServiceResult.fail("??????????????? ???????????? ????????????.");
        }
        User user = optionalUser.get();

        Optional<BoardLike> optionalBoardLike = boardLikeRepository.findByBoardAndUser(board, user);
        if (!optionalBoardLike.isPresent()) {
            return ServiceResult.fail("???????????? ????????? ????????????.");
        }
        BoardLike boardLike = optionalBoardLike.get();
        boardLikeRepository.delete(boardLike);
        return ServiceResult.success();
    }

    @Override
    public ServiceResult addBadReport(Long id, String email, BoardBadReportInput boardBadReportInput) {
        Optional<Board> optionalBoard = boardRepository.findById(id);
        if (!optionalBoard.isPresent()) {
            return ServiceResult.fail("???????????? ???????????? ????????????.");
        }
        Board board = optionalBoard.get();

        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (!optionalUser.isPresent()) {
            return ServiceResult.fail("??????????????? ???????????? ????????????.");
        }
        User user = optionalUser.get();

        BoardBadReport boardBadReport = BoardBadReport.builder()
                .userId(user.getId())
                .userName(user.getUserName())
                .userEmail(user.getEmail())

                .boardId(board.getId())
                .boardUserId(board.getUser().getId())
                .boardTitle(board.getTitle())
                .boardContents(board.getContents())
                .boardRegDate(board.getRegDate())
                .comments(boardBadReportInput.getComments())
                .regDate(LocalDateTime.now())
                .build();

        boardBadReportRepository.save(boardBadReport);

        return ServiceResult.success();
    }

    @Override
    public List<BoardBadReport> badReportList() {
        return boardBadReportRepository.findAll();
    }

    @Override
    public ServiceResult scrapBoard(Long id, String email) {
        Optional<Board> optionalBoard = boardRepository.findById(id);
        if (!optionalBoard.isPresent()) {
            return ServiceResult.fail("???????????? ???????????? ????????????.");
        }
        Board board = optionalBoard.get();

        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (!optionalUser.isPresent()) {
            return ServiceResult.fail("??????????????? ???????????? ????????????.");
        }
        User user = optionalUser.get();

        BoardScrap boardScrap = BoardScrap.builder()
                .user(user)
                .boardId(board.getId())
                .boardUserId(board.getUser().getId())
                .boardTypeId(board.getBoardType().getId())
                .boardTitle(board.getTitle())
                .boardContents(board.getContents())
                .boardRegDate(board.getRegDate())
                .regDate(LocalDateTime.now())
                .build();
        boardScrapRepository.save(boardScrap);

        return ServiceResult.success();
    }

    @Override
    public ServiceResult removeScrap(Long id, String email) {

        Optional<BoardScrap> optionalBoardScrap = boardScrapRepository.findById(id);
        if (!optionalBoardScrap.isPresent()) {
            return ServiceResult.fail("????????? ???????????? ????????????.");
        }
        BoardScrap boardScrap = optionalBoardScrap.get();

        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (!optionalUser.isPresent()) {
            return ServiceResult.fail("??????????????? ???????????? ????????????.");
        }
        User user = optionalUser.get();
        // ??? ?????????????????? ?????? ??????
        if (!user.getId().equals(boardScrap.getUser().getId())) {
            return ServiceResult.fail("????????? ???????????? ????????? ??? ????????????.");
        }

        boardScrapRepository.delete(boardScrap);
        return ServiceResult.success();

    }

    private String getBoardUrl(Long boardId) {
        return String.format("/board/%d", boardId);
    }

    @Override
    public ServiceResult addBookmark(Long id, String email) {

        Optional<Board> optionalBoard = boardRepository.findById(id);
        if (!optionalBoard.isPresent()) {
            return ServiceResult.fail("???????????? ???????????? ????????????.");
        }
        Board board = optionalBoard.get();

        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (!optionalUser.isPresent()) {
            return ServiceResult.fail("??????????????? ???????????? ????????????.");
        }
        User user = optionalUser.get();

        BoardBookmark boardBookmark = BoardBookmark.builder()
                .user(user)
                .boardId(board.getId())
                .boardTypeId(board.getBoardType().getId())
                .boardTitle(board.getTitle())
                .boardUrl(getBoardUrl(board.getId()))
                .regDate(LocalDateTime.now())
                .build();
        boardBookmarkRepository.save(boardBookmark);
        return ServiceResult.success();
    }

    @Override
    public ServiceResult removeBookmark(Long id, String email) {
        Optional<BoardBookmark> optionalBoardBookmark = boardBookmarkRepository.findById(id);
        if (!optionalBoardBookmark.isPresent()) {
            return ServiceResult.fail("????????? ???????????? ????????????.");
        }
        BoardBookmark boardBookmark = optionalBoardBookmark.get();

        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (!optionalUser.isPresent()) {
            return ServiceResult.fail("??????????????? ???????????? ????????????.");
        }
        User user = optionalUser.get();
        // ??? ??????????????? ?????? ??????
        if (!user.getId().equals(boardBookmark.getUser().getId())) {
            return ServiceResult.fail("????????? ???????????? ????????? ??? ????????????.");
        }

        boardBookmarkRepository.delete(boardBookmark);
        return ServiceResult.success();
    }

    @Override
    public List<Board> postList(String email) {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (!optionalUser.isPresent()) {
            throw new BizException("?????? ????????? ???????????? ????????????.");
        }
        User user = optionalUser.get();
        return boardRepository.findByUser(user);
    }

    @Override
    public List<BoardComment> commentList(String email) {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (!optionalUser.isPresent()) {
            throw new BizException("?????? ????????? ???????????? ????????????.");
        }
        User user = optionalUser.get();

        return boardCommentRepository.findByUser(user);
    }

    @Override
    public Board detail(Long id) {
        Optional<Board> optionalBoard = boardRepository.findById(id);
        if (!optionalBoard.isPresent()) {
            throw new BizException("???????????? ???????????? ????????????.");
        }
        return optionalBoard.get();
    }

    @Override
    public List<Board> list() {
        return boardRepository.findAll();
    }

    @Override
    public ServiceResult add(String email, BoardInput boardInput) {

        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (!optionalUser.isPresent()) {
            return ServiceResult.fail("??????????????? ???????????? ????????????.");
        }
        User user = optionalUser.get();

        Optional<BoardType> optionalBoardType = boardTypeRepository.findById(boardInput.getBoardType());
        if (!optionalBoardType.isPresent()) {
            return ServiceResult.fail("????????? ????????? ???????????? ????????????.");
        }
        BoardType boardType = optionalBoardType.get();

        Board board = Board.builder()
                .user(user)
                .boardType(boardType)
                .title(boardInput.getTitle())
                .contents(boardInput.getContents())
                .regDate(LocalDateTime.now())
                .build();
        boardRepository.save(board);

        return ServiceResult.success();
    }

    @Override
    public ServiceResult replyBoard(Long id, BoardReplyInput boardReplyInput) {
        Optional<Board> optionalBoard = boardRepository.findById(id);
        if (!optionalBoard.isPresent()) {
            return ServiceResult.fail("???????????? ???????????? ????????????.");
        }
        Board board = optionalBoard.get();
        board.setReplyContents(boardReplyInput.getReplyContents());
        boardRepository.save(board);

        // ?????? ??????
        Optional<MailTemplate> optionalMailTemplate = mailTemplateRepository.findByTemplateId("BOARD_REPLY");
        optionalMailTemplate.ifPresent((e) -> {
            String fromEmail = e.getSendEmail();
            String fromUserName = e.getSendUserName();
            String title = e.getTitle().replaceAll("\\{USER_NAME\\}", board.getUser().getUserName());
            String contents = e.getContents().replaceAll("\\{BOARD_TITLE\\}", board.getTitle())
                    .replaceAll("\\{BOARD_CONTENTS\\}", board.getContents())
                    .replaceAll("\\{BOARD_REPLY_CONTENTS\\}", board.getReplyContents());

            mailComponent.send(fromEmail, fromUserName,
                    board.getUser().getEmail(), board.getUser().getUserName(), title, contents);
        });

        return ServiceResult.success();
    }
}
