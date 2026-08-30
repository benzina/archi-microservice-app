create sequence if not exists fraud_id_sequence start with 1 increment by 50;

create table if not exists fraud_check_history (
    id integer primary key,
    customer_id integer,
    is_fraudster boolean not null,
    created_at timestamp
);
