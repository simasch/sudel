create sequence poll_seq;

create table poll
(
    id            bigint                   not null default nextval('poll_seq') primary key,

    name          varchar(200)             not null,
    location      varchar(200)             not null,
    description   varchar(4000),

    creator_email varchar(200),
    creator_name  varchar(200),

    created_at    timestamp with time zone not null default current_timestamp,

    poll_key      uuid                     not null
);

create sequence poll_date_seq;

create table poll_date
(
    id        bigint                   not null default nextval('poll_date_seq') primary key,

    starts_at timestamp with time zone not null,
    ends_at   timestamp with time zone not null,
    all_day   bool                     not null default false,

    poll_id   bigint                   not null
);

alter table poll_date
    add constraint fk_poll_date_poll foreign key (poll_id) references poll (id);
