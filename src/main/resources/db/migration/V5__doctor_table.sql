create table doctors(
    id serial primary key references users(id) on delete cascade,
    specialization varchar(255),
    license_number varchar(255) unique not null ,
    consultation_fees decimal(10,2) not null
)