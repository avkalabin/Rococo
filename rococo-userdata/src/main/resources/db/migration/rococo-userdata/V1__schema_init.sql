create table if not exists `user`
(
    id              binary(16)   not null unique default (uuid_to_bin(uuid(), true)),
    username        varchar(50)  not null check (char_length(username) > 0),
    firstname       varchar(30),
    lastname        varchar(50),
    avatar          mediumblob,
    primary key (id)
);

