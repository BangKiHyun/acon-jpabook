CREATE TABLE MEMBER (
    ID VARCHAR(255) NOT NULL, --아이디(기본 키)
    NAME VARCHAR(255),        --이름
    AGE INTEGER NOT NULL,     --나이
    PRIMARY KEY (ID)
)

CREATE TABLE BOARD (
    ID INT NOT NULL AUTO_INCREMENT PRIMARY  KEY,
    DATA VARCHAR (255)
);

INSERT INTO BOARD(DATA) VALUES('A');
INSERT INTO BOARD(DATA) VALUES('B');
