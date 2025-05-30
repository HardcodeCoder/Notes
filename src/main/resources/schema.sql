CREATE TABLE IF NOT EXISTS ACCOUNT(
    ID INTEGER PRIMARY KEY AUTO_INCREMENT,
    NAME VARCHAR(50),
    EMAIL VARCHAR(100) NOT NULL UNIQUE,
    PASSWORD VARCHAR(255) NOT NULL,
    CREATED_ON TIMESTAMP(3) WITH TIME ZONE NOT NULL,
    MODIFIED_ON TIMESTAMP(3) WITH TIME ZONE NOT NULL
);

CREATE TABLE IF NOT EXISTS TOKEN(
    ID INTEGER PRIMARY KEY AUTO_INCREMENT,
    ACCOUNT_ID INTEGER,
    ACCESS_TOKEN VARCHAR(255) NOT NULL,
    REFRESH_TOKEN VARCHAR(255) NOT NULL,
    GENERATED_ON TIMESTAMP(0) WITH TIME ZONE NOT NULL,
    FOREIGN KEY(ACCOUNT_ID) REFERENCES ACCOUNT(ID) ON UPDATE CASCADE ON DELETE CASCADE
);