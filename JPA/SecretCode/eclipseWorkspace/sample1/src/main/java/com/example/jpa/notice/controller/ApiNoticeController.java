package com.example.jpa.notice.controller;

import com.example.jpa.notice.model.NoticeModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    @GetMapping("/api/notice3")
    public List<NoticeModel> noticeModelList() {
        List<NoticeModel> noticeList = new ArrayList<>();

        LocalDateTime regDate = LocalDateTime.of(2022, 1, 5, 0, 0);
        LocalDateTime regDate2 = LocalDateTime.of(2022, 1, 6, 0, 0);

        NoticeModel notice1 = new NoticeModel();
        notice1.setId(1);
        notice1.setTitle("공지사항입니다.");
        notice1.setContents("공지사항 내용입니다.");
        notice1.setRegDate(regDate);

        /*
        NoticeModel notice2 = new NoticeModel();
        notice2.setId(2);
        notice2.setTitle("두번째 공지사항입니다.");
        notice2.setContents("두번쨰 공지사항 내용입니다.");
        notice2.setRegDate(regDate2);*/

        // builder 사용
        /*NoticeModel notice2 = NoticeModel.builder()
                .id(2)
                .title("두번째 공지사항입니다.")
                .contents("두번쨰 공지사항 내용입니다.")
                .regDate(regDate2)
                .build();*/

        noticeList.add(notice1);
        noticeList.add(NoticeModel.builder()
                .id(2)
                .title("두번째 공지사항입니다.")
                .contents("두번쨰 공지사항 내용입니다.")
                .regDate(regDate2)
                .build());
        return noticeList;
    }

    @GetMapping("/api/notice4")
    public List<NoticeModel> noticeModelListNull() {
        List<NoticeModel> noticeModels = new ArrayList<>();
        return noticeModels;
    }

    @GetMapping("/api/notice/count")
    public int noticeCount() {
        // int도 가능
        return 10;
    }

    @PostMapping("/api/notice")
    public NoticeModel addNotice(@RequestParam String title, @RequestParam String contents) {
        /*NoticeModel notice = new NoticeModel();
        notice.setTitle(title);
        notice.setContents(contents);
        notice.setId(1);
        notice.setRegDate(LocalDateTime.now());*/

        // builder 페턴
        NoticeModel notice = NoticeModel.builder()
                .id(1)
                .title(title)
                .contents(contents)
                .regDate(LocalDateTime.now())
                .build();
        return notice;
    }

    @PostMapping("/api/notice2")
    public NoticeModel addNotice2(NoticeModel noticeModel) {
        noticeModel.setId(2);
        noticeModel.setRegDate(LocalDateTime.now());

        return noticeModel;
    }

    @PostMapping("/api/notice3")
    public NoticeModel addNotice3(@RequestBody NoticeModel noticeModel) {
        noticeModel.setId(3);
        noticeModel.setRegDate(LocalDateTime.now());
        return noticeModel;
    }
}
