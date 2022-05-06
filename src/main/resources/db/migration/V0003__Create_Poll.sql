CREATE SEQUENCE poll_seq;

CREATE TABLE poll
(
    id          bigint       NOT NULL DEFAULT NEXTVAL('poll_seq') PRIMARY KEY,

    name        varchar(200) NOT NULL,
    location    varchar(200) NOT NULL,
    description varchar(4000)
);

CREATE SEQUENCE poll_date_seq;

CREATE TABLE poll_date
(
    id        bigint    NOT NULL DEFAULT NEXTVAL('poll_date_seq') PRIMARY KEY,

    starts_at timestamp NOT NULL,
    ends_at   timestamp NOT NULL,
    all_day   bool,
    poll_id   bigint    NOT NULL
);

ALTER TABLE poll_date
    ADD CONSTRAINT fk_poll_date_poll FOREIGN KEY (poll_id) REFERENCES poll (id);
