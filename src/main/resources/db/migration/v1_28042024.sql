create table User(
    id serial primary key ,
    username varchar(64) not null unique ,
    password varchar(2048)not null ,
    role varchar(32) not null ,
    first_name varchar(64) not null ,
    last_name varchar(64) not null ,
    enabled boolean not null default false,
    created_at timestamp,
    updated_at timestamp
);