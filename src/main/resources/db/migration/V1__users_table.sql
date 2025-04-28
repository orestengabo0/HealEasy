create type user_roles as enum('ADMIN','DOCTOR','PATIENT');

create table users
(
    id              serial,
    name            varchar(255)                             not null,
    email           varchar(255)                             not null,
    password        varchar(255)                             not null,
    phone_number    varchar(15)                              not null,
    profileimageurl text,
    type            user_roles default 'PATIENT'::user_roles not null,
    created_at      timestamp  default CURRENT_TIMESTAMP,
    updated_at      timestamp  default CURRENT_TIMESTAMP
);

alter table users
    owner to postgres;

