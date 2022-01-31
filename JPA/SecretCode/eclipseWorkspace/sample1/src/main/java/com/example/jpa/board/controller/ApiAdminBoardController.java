package com.example.jpa.board.controller;

import com.example.jpa.board.entity.BoardBadReport;
import com.example.jpa.board.model.BoardReplyInput;
import com.example.jpa.board.model.ServiceResult;
import com.example.jpa.board.service.BoardService;
import com.example.jpa.common.model.ResponseResult;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class ApiAdminBoardController {

    private final BoardService boardService;

    @GetMapping("/board/badreport")
    public ResponseEntity<?> badReport() {
        List<BoardBadReport> list = boardService.badReportList();
        return ResponseResult.success(list);
    }

    @PostMapping("/board/{id}/reply")
    public ResponseEntity<?> reply(@PathVariable Long id,
                      @RequestBody BoardReplyInput boardReplyInput) {

        ServiceResult result = boardService.replyBoard(id, boardReplyInput);
        return ResponseResult.result(result);
    }
}






