package com.example.jpa.notice.controller;

import com.example.jpa.notice.entity.Notice;
import com.example.jpa.notice.exception.AlreadyDeletedException;
import com.example.jpa.notice.exception.NoticeNotFoundException;
import com.example.jpa.notice.model.NoticeDeleteInput;
import com.example.jpa.notice.model.NoticeInput;
import com.example.jpa.notice.model.NoticeModel;
import com.example.jpa.notice.model.ResponseError;
import com.example.jpa.notice.repository.NoticeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class ApiNoticeController {

    private final NoticeRepository noticeRepository;

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

    @PostMapping("/api/notice4")
    public Notice addNotice4(@RequestBody NoticeInput noticeInput) {
        Notice notice = Notice.builder()
                .title(noticeInput.getTitle())
                .contents(noticeInput.getContents())
                .regDate(LocalDateTime.now())
                .build();

        noticeRepository.save(notice);

        return notice;
    }

    @PostMapping("/api/notice5")
    public Notice addNotice5(@RequestBody NoticeInput noticeInput) {
        Notice notice = Notice.builder()
                .title(noticeInput.getTitle())
                .contents(noticeInput.getContents())
                .regDate(LocalDateTime.now())
                .hits(0)
                .likes(0)
                .build();

        noticeRepository.save(notice);

        return notice;
    }

    @GetMapping("/api/notice/{id}")
    public Notice notice(@PathVariable Long id) {
        Optional<Notice> notice = noticeRepository.findById(id);
        if (notice.isPresent()) {
            return notice.get();
        }
        return null;
    }

    /*@PutMapping("/api/notice/{id}")
    public void updateNotice(@PathVariable Long id, @RequestBody NoticeInput noticeInput) {
        Optional<Notice> notice = noticeRepository.findById(id);
        if (notice.isPresent()) {
            notice.get().setTitle(noticeInput.getTitle());
            notice.get().setContents(noticeInput.getContents());
            notice.get().setUpdateDate(LocalDateTime.now());
            noticeRepository.save(notice.get());
        }
    }*/

    @ExceptionHandler(NoticeNotFoundException.class)
    public ResponseEntity<String> handlerNoticeNotFoundException(NoticeNotFoundException exception) {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @PutMapping("/api/notice/{id}")
    public void updateNotice(@PathVariable Long id, @RequestBody NoticeInput noticeInput) {
        /*Optional<Notice> notice = noticeRepository.findById(id);
        if (!notice.isPresent()) {
            // 예외 발생
            throw new NoticeNotFoundException("공지사항에 글이 존재하지 않습니다.");
        }

        // 공지사항 글이 있을 때
        notice.get().setTitle(noticeInput.getTitle());
        notice.get().setContents(noticeInput.getContents());
        notice.get().setUpdateDate(LocalDateTime.now());
        noticeRepository.save(notice.get());
        */
        Notice notice = noticeRepository.findById(id)
                .orElseThrow(() -> new NoticeNotFoundException("공지사항에 글이 존재하지 않습니다."));
        // 공지사항 글이 있을 때
        notice.setTitle(noticeInput.getTitle());
        notice.setContents(noticeInput.getContents());
        notice.setUpdateDate(LocalDateTime.now());
        noticeRepository.save(notice);

    }

    @PatchMapping("/api/notice/{id}/hits")
    public void noticeHits(@PathVariable Long id) {
        Notice notice = noticeRepository.findById(id)
                .orElseThrow(() -> new NoticeNotFoundException("공지사항에 글이 존재하지 않습니다."));

        notice.setHits(notice.getHits() + 1);
        noticeRepository.save(notice);
    }

    // 21번
    /*@DeleteMapping("/api/notice/{id}")
    public void deleteNotice(@PathVariable Long id) {
        Optional<Notice> notice = noticeRepository.findById(id);
        if (notice.isPresent()) {
            noticeRepository.delete(notice.get());
        }
    }*/

    // 22번
    /*@DeleteMapping("/api/notice/{id}")
    public void deleteNotice(@PathVariable Long id) {
        Notice notice = noticeRepository.findById(id)
                .orElseThrow(() -> new NoticeNotFoundException("공지사항에 글이 존재하지 않습니다."));

        noticeRepository.delete(notice);
    }*/

    @ExceptionHandler(AlreadyDeletedException.class)
    public ResponseEntity<String> handlerAlreadyDeletedException(NoticeNotFoundException exception) {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.OK);
    }

    @DeleteMapping("/api/notice/{id}")
    public void deleteNotice(@PathVariable Long id) {
        Notice notice = noticeRepository.findById(id)
                .orElseThrow(() -> new NoticeNotFoundException("공지사항에 글이 존재하지 않습니다."));

        if (notice.isDeleted()) {
            throw new AlreadyDeletedException("이미 삭제된 글입니다.");
        }

        notice.setDeleted(true);
        notice.setDeletedDate(LocalDateTime.now());

        noticeRepository.save(notice);
    }

    @DeleteMapping("/api/notice")
    public void deleteNoticeList(@RequestBody NoticeDeleteInput noticeDeleteInput) {

        List<Notice> noticeList = noticeRepository.findByIdIn(noticeDeleteInput.getIdList())
                .orElseThrow(() -> new NoticeNotFoundException("공지사항에 글이 존재하지 않습니다."));

        noticeList.stream().forEach(e -> {
            e.setDeleted(true);
            e.setDeletedDate(LocalDateTime.now());
        });

        noticeRepository.saveAll(noticeList);
    }

    @DeleteMapping("/api/notice/all")
    public void deleteAll() {
        noticeRepository.deleteAll();
    }

    @PostMapping("/api/notice6")
    public void addNotice6(@RequestBody NoticeInput noticeInput) {
        Notice notice = Notice.builder()
                .title(noticeInput.getTitle())
                .contents(noticeInput.getContents())
                .hits(0)
                .likes(0)
                .regDate(LocalDateTime.now())
                .build();
        noticeRepository.save(notice);
    }

    @PostMapping("/api/notice7")
    public ResponseEntity<Object> addNotice7(@RequestBody @Valid NoticeInput noticeInput, Errors errors) {
        /*if (noticeInput.getTitle() == null
            || noticeInput.getTitle().length() < 1
            || noticeInput.getContents() == null
            || noticeInput.getContents().length() < 1)  {

            return new ResponseEntity<>("입력값이 정확하지 않습니다.", HttpStatus.BAD_REQUEST);
        }*/

        if (errors.hasErrors()) {
//            return new ResponseEntity<>(errors.getAllErrors(), HttpStatus.BAD_REQUEST);
            List<ResponseError> responseErrors = new ArrayList<>();

            /*errors.getAllErrors().stream().forEach(e -> {

                ResponseError responseError = new ResponseError();
                responseError.setField(((FieldError) e).getField());
                responseError.setMessage(e.getDefaultMessage());
                responseErrors.add(responseError);
            });*/
            errors.getAllErrors().stream().forEach(e -> {
                responseErrors.add(ResponseError.of( (FieldError) e));
            });


            return new ResponseEntity<>(responseErrors, HttpStatus.BAD_REQUEST);
        }

        // 정상적인 저장..
        noticeRepository.save(Notice.builder()
                .title(noticeInput.getTitle())
                .contents(noticeInput.getContents())
                .hits(0)
                .likes(0)
                .regDate(LocalDateTime.now())
                .build());

        return ResponseEntity.ok().build();
    }
}






