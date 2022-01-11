package com.example.jpa.user.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserLogCount {

    private Long id;
    private String email;
    private String userName;

    private Long noticeCount;
    private Long noticeLikeCount;
}
