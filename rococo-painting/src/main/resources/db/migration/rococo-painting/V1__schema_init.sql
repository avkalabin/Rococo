create table if not exists `painting`
(
    id              binary(16)   not null unique default (uuid_to_bin(uuid(), true)),
    title           varchar(50)  not null check (char_length(title) > 0),
    description     text         not null,
    content         text         not null,
    museum_id       binary(16)   not null,
    artist_id       binary(16)   not null,
    primary key (id)
);
