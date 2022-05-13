package com.jikim.practice.spring.security.note;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jikim.practice.spring.security.user.User;

public interface NoteRepository extends JpaRepository<Note, Long> {

	List<Note> findByUserOrderByIdDesc(User user);

	Note findByIdAndUser(Long noteId, User user);
}
