package com.example.jpa.notice.controller;

import com.example.jpa.notice.model.NoticeModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
public class ApiNoticeController {

    @GetMapping("/api/notice")
    public String noticeString() {
        return "공지사항입니다.";
    }

    @GetMapping("/api/notice2")
    public NoticeModel noticeModel() {
        // 게시글ID = 1, 제목 = 공지사항입니다, 내용 = 공지사항 내용입니다, 등록일 = 2022-1-5

        LocalDateTime regDate = LocalDateTime.of(2022, 1, 5, 0, 0);

        NoticeModel notice = new NoticeModel();
        notice.setId(1);
        notice.setTitle("공지사항입니다.");
        notice.setContents("공지사항 내용입니다.");
        notice.setRegDate(regDate);
        return notice;
    }
}
