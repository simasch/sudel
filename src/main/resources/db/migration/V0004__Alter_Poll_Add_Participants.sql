CREATE SEQUENCE poll_participant_seq;

CREATE TABLE poll_participant
(
    id      bigint  NOT NULL DEFAULT NEXTVAL('poll_participant_seq') PRIMARY KEY,

    email   varchar NOT NULL,

    poll_id bigint  NOT NULL
);

ALTER TABLE poll_participant
    ADD CONSTRAINT fk_poll_participant_poll FOREIGN KEY (poll_id) REFERENCES poll (id);
