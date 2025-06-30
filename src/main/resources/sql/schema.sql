
CREATE TABLE customers (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    firstname VARCHAR(255),
    lastname VARCHAR(255),
    tckn VARCHAR(11) UNIQUE NOT NULL,
    created_date TIMESTAMP NOT NULL,
    created_by VARCHAR(255) NOT NULL,
    updated_date TIMESTAMP,
    updated_by VARCHAR(255)
);