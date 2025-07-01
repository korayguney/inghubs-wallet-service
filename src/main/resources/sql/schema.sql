CREATE TABLE customer
(
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    username     VARCHAR(255) UNIQUE NOT NULL,
    password     VARCHAR(255)        NOT NULL,
    firstname    VARCHAR(255),
    lastname     VARCHAR(255),
    tckn         VARCHAR(11) UNIQUE  NOT NULL,
    created_date TIMESTAMP           NOT NULL,
    created_by   VARCHAR(255)        NOT NULL,
    updated_date TIMESTAMP,
    updated_by   VARCHAR(255)
);

CREATE TABLE employee
(
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    username     VARCHAR(255) UNIQUE NOT NULL,
    password     VARCHAR(255)        NOT NULL,
    firstname    VARCHAR(255),
    lastname     VARCHAR(255),
    department   VARCHAR(255),
    created_date TIMESTAMP           NOT NULL,
    created_by   VARCHAR(255)        NOT NULL,
    updated_date TIMESTAMP,
    updated_by   VARCHAR(255)
);
