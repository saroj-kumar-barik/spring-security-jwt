CREATE TABLE IF NOT EXISTS TBL_LOGIN_USER
(
ID INT PRIMARY KEY AUTO_INCREMENT,
USERNAME VARCHAR(256) UNIQUE NOT NULL,
PASSWORD VARCHAR(256) NOT NULL
);