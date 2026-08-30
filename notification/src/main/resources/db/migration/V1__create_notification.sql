create sequence if not exists notification_id_sequence start with 1 increment by 50;

create table if not exists notification (
    notification_id integer primary key,
    to_customer_id integer,
    to_customer_email varchar(255),
    sender varchar(255),
    message varchar(255),
    sent_at timestamp
);
