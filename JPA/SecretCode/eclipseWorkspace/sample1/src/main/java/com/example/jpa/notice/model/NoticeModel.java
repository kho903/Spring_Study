package com.example.jpa.notice.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class NoticeModel {

    // id, 제목, 내용, 등록일(작성일)
    private int id;
    private String title;
    private String contents;
    private LocalDateTime regDate;
}
