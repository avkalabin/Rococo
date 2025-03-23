create table if not exists `logs`
(
    id              binary(16)   not null unique default (uuid_to_bin(uuid(), true)),
    event_type      varchar(25) not null,
    entity_id       binary(16)   not null,
    description     VARCHAR(255),
    event_date      datetime(6) not null,
    created_at      datetime(6) not null default current_timestamp(6),
    primary key (id)
);