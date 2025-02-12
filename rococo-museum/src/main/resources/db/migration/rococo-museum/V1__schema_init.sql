create table if not exists `museum`
(
    id          binary(16)   not null unique default (uuid_to_bin(uuid(), true)),
    title       varchar(50)  not null check (char_length(title) > 0),
    description text         not null,
    photo       mediumblob   not null,
    city        varchar(50)  not null,
    geo_id      binary(16)   not null,
    primary key (id)
);
