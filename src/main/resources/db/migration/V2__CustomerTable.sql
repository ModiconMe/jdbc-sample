CREATE TABLE IF NOT EXISTS customer
(
    id         IDENTITY NOT NULL,
    name       TEXT(50)  NOT NULL,
    email      TEXT(50)  NOT NULL,
    age        INT       NOT NULL,
    created_at TIMESTAMP NOT NULL,
    CONSTRAINT customer_uq UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS payment
(
    id             IDENTITY NOT NULL PRIMARY KEY,
    customer_id    LONG NOT NULL,
    amount         LONG NOT NULL
--     FOREIGN KEY (customer_id) REFERENCES customer(id)
);

ALTER TABLE customer ADD PRIMARY KEY (id);
ALTER TABLE payment ADD FOREIGN KEY (customer_id) REFERENCES customer(id);