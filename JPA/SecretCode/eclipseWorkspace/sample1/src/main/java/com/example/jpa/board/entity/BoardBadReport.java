package com.example.jpa.board.entity;

import com.example.jpa.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BoardBadReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 신고자 정보
    @Column
    private Long userId;

    @Column
    private String userName;

    @Column
    private String userEmail;

    // 신고 게시글 정보
    @Column
    private Long boardId;

    @Column
    private Long boardUserId;

    @Column
    private String boardTitle;

    @Column
    private String boardContents;

    @Column
    private LocalDateTime boardRegDate;

    // 신고 내용
    @Column
    private String comments;

    // 신고 날짜
    @Column
    private LocalDateTime regDate;

}
