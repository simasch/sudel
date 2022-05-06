CREATE SEQUENCE security_group_seq;

CREATE TABLE security_group
(
    id   bigint       NOT NULL DEFAULT NEXTVAL('security_group_seq') PRIMARY KEY,

    name varchar(255) NOT NULL
);

ALTER TABLE security_group
    ADD CONSTRAINT uk_security_group_name UNIQUE (name);

CREATE SEQUENCE security_user_seq;

CREATE TABLE security_user
(
    id              bigint       NOT NULL DEFAULT NEXTVAL('security_user_seq') PRIMARY KEY,

    first_name      varchar(255) NOT NULL,
    last_name       varchar(255) NOT NULL,
    email           varchar(255) NOT NULL,
    secret          varchar(255) NOT NULL,

    confirmation_id varchar(255),
    confirmed       boolean               DEFAULT false
);

ALTER TABLE security_user
    ADD CONSTRAINT uk_security_user_email UNIQUE (email);

CREATE TABLE user_group
(
    user_id  bigint NOT NULL,
    group_id bigint NOT NULL
);

ALTER TABLE user_group
    ADD PRIMARY KEY (group_id, user_id);
ALTER TABLE user_group
    ADD CONSTRAINT fk_user_group_user FOREIGN KEY (user_id) REFERENCES security_user (id);
ALTER TABLE user_group
    ADD CONSTRAINT fk_user_group_group FOREIGN KEY (group_id) REFERENCES security_group (id);
