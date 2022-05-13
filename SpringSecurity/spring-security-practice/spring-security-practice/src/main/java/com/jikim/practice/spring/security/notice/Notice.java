package com.jikim.practice.spring.security.notice;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Notice {

	@Id
	@GeneratedValue
	private Long id;

	/**
	 * 공지사항 제목
	 */
	private String title;

	/**
	 * 공지사항 내용
	 */
	@Lob
	private String content;

	/**
	 * 등록 일시
	 */
	@CreatedDate
	private LocalDateTime createdAt;

	@LastModifiedDate
	private LocalDateTime updatedAt;

	public Notice(String title, String content) {
		this.title = title;
		this.content = content;
	}
}
