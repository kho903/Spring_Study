
DROP TABLE IF EXISTS USER;
DROP TABLE IF EXISTS NOTICE;
-- auto-generated definition
create table USER
(
    ID          BIGINT auto_increment primary key,
    EMAIL       VARCHAR(255),
    USER_NAME   VARCHAR(255),
    PASSWORD    VARCHAR(255),
    PHONE       VARCHAR(255),
    REG_DATE    TIMESTAMP,
    UPDATE_DATE TIMESTAMP
);

-- auto-generated definition
create table NOTICE
(
    ID       BIGINT auto_increment primary key,
    TITLE    VARCHAR(255),
    CONTENTS VARCHAR(255),

    HITS     INTEGER,
    LIKES    INTEGER,

    REG_DATE        TIMESTAMP,
    UPDATE_DATE     TIMESTAMP,
    DELETED_DATE    TIMESTAMP,
    DELETED         BOOLEAN,

    USER_ID         BIGINT,
    constraint FK_NOTICE_USER_ID foreign key(USER_ID) references USER(ID)
);

-- auto-generated definition
create table NOTICE_LIKE
(
    ID          BIGINT auto_increment primary key,
    NOTICE_ID   BIGINT,
    USER_ID     BIGINT not null,
    constraint  FK_NOTICE_LIKE_NOTICE_ID foreign key (NOTICE_ID) references NOTICE (ID),
    constraint  FK_NOTICE_LIKE_USER_ID foreign key (USER_ID) references USER (ID)
);