create table medications(
    id serial primary key,
    name varchar(255) not null ,
    dosage varchar(255) not null,
    duration_in_days int not null
)