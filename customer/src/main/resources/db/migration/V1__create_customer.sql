create sequence if not exists customer_id_sequence start with 1 increment by 50;

create table if not exists customer (
    id integer primary key,
    first_name varchar(255),
    last_name varchar(255),
    email varchar(255)
);
