create sequence security_group_seq;

create table security_group
(
    id   bigint       not null default nextval('security_group_seq') primary key,

    name varchar(255) not null
);

alter table security_group
    add constraint uk_security_group_name unique (name);

create sequence security_user_seq;

create table security_user
(
    id              bigint       not null default nextval('security_user_seq') primary key,

    first_name      varchar(255) not null,
    last_name       varchar(255) not null,
    email           varchar(255) not null,
    secret          varchar(255) not null,

    confirmation_id varchar(255),
    confirmed       boolean               default false
);

alter table security_user
    add constraint uk_security_user_email unique (email);

create table user_group
(
    user_id  bigint not null,
    group_id bigint not null
);

alter table user_group
    add primary key (group_id, user_id);
alter table user_group
    add constraint fk_user_group_user foreign key (user_id) references security_user (id);
alter table user_group
    add constraint fk_user_group_group foreign key (group_id) references security_group (id);
