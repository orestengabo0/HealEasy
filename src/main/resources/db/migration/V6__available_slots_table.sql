create table available_slots (
    id serial primary key,
    doctor_id int references doctors(id) on delete cascade,
    start_time timestamp not null,
    end_time timestamp not null
)