create type APPOINTMENT_STATUS as enum('PENDING', 'CONFIRMED', 'CANCELLED', 'COMPLETED');

create table appointments(
    id serial primary key,
    patient_id int references patients(id) on delete set null ,
    doctor_id int references doctors(id) on delete set null ,
    schedule_time timestamp not null,
    status APPOINTMENT_STATUS default 'PENDING'
)