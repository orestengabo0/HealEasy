create type NOTIFICATION_TYPE as enum('SYSTEM','REMINDER','APPOINTMENT');

create table notifications (
    id int primary key,
    user_id int references users(id) on delete cascade,
    message text not null,
    message_type NOTIFICATION_TYPE not null,
    seen boolean default false,
    created_at timestamp default current_timestamp
)