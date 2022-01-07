
INSERT INTO USER (ID, EMAIL, PASSWORD, PHONE, REG_DATE, UPDATE_DATE, USER_NAME) VALUES (1, 'test@naver.com', '1111', '010-1111-2222', '2022-01-01 00:00:00.000000', null, '박규태');
INSERT INTO USER (ID, EMAIL, PASSWORD, PHONE, REG_DATE, UPDATE_DATE, USER_NAME) VALUES (2, 'test1@naver.com', '2222', '010-3333-4444', '2022-01-01 00:10:00.000000', null, '정혜경');
INSERT INTO USER (ID, EMAIL, PASSWORD, PHONE, REG_DATE, UPDATE_DATE, USER_NAME) VALUES (3, 'test2@naver.com', '3333', '010-5555-6666', '2022-01-01 20:00:01.000000', null, '박하은');
INSERT INTO USER (ID, EMAIL, PASSWORD, PHONE, REG_DATE, UPDATE_DATE, USER_NAME) VALUES (4, 'test3@naver.com', '4444', '010-7777-8888', '2022-01-01 00:30:10.000000', null, '박하영');

INSERT INTO NOTICE (ID, CONTENTS, HITS, LIKES, REG_DATE, TITLE, DELETED, USER_ID) VALUES (1, '내용1', 0, 0, '2022-01-01 00:00:00.000000', '제목1', 0, 1);
INSERT INTO NOTICE (ID, CONTENTS, HITS, LIKES, REG_DATE, TITLE, DELETED, USER_ID) VALUES (2, '내용2', 0, 0, '2022-01-01 00:01:00.000000', '제목2', 0, 1);
INSERT INTO NOTICE (ID, CONTENTS, HITS, LIKES, REG_DATE, TITLE, DELETED, USER_ID) VALUES (3, '내용3', 0, 0, '2022-01-01 00:02:00.000000', '제목3', 0, 2);
INSERT INTO NOTICE (ID, CONTENTS, HITS, LIKES, REG_DATE, TITLE, DELETED, USER_ID) VALUES (4, '내용4', 0, 0, '2022-01-01 00:02:00.000000', '제목4', 0, 2);
INSERT INTO NOTICE (ID, CONTENTS, HITS, LIKES, REG_DATE, TITLE, DELETED, USER_ID) VALUES (5, '내용5', 0, 0, '2022-01-01 00:02:00.000000', '제목5', 0, 2);
INSERT INTO NOTICE (ID, CONTENTS, HITS, LIKES, REG_DATE, TITLE, DELETED, USER_ID) VALUES (6, '내용6', 0, 0, '2022-01-01 00:02:00.000000', '제목6', 0, 1);
INSERT INTO NOTICE (ID, CONTENTS, HITS, LIKES, REG_DATE, TITLE, DELETED, USER_ID) VALUES (7, '내용7', 0, 0, '2022-01-01 00:02:00.000000', '제목7', 0, 3);
INSERT INTO NOTICE (ID, CONTENTS, HITS, LIKES, REG_DATE, TITLE, DELETED, USER_ID) VALUES (8, '내용8', 0, 0, '2022-01-01 00:02:00.000000', '제목8', 0, 3);
INSERT INTO NOTICE (ID, CONTENTS, HITS, LIKES, REG_DATE, TITLE, DELETED, USER_ID) VALUES (9, '내용9', 0, 0, '2022-01-01 00:02:00.000000', '제목9', 0, 1);
INSERT INTO NOTICE (ID, CONTENTS, HITS, LIKES, REG_DATE, TITLE, DELETED, USER_ID) VALUES (10, '내용10', 0, 0, '2022-01-01 00:02:00.000000', '제목10', 0, 1);

INSERT INTO NOTICE_LIKE (ID, NOTICE_ID, USER_ID)
VALUES (1, 3, 1)
     , (2, 4, 1)
     , (3, 1, 1)
     , (4, 3, 2)
     , (5, 1, 4)
     , (6, 2, 4);