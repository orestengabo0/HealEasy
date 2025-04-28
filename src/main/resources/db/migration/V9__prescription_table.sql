create table prescriptions(
    id serial primary key,
    patient_id int references patients(id) on delete cascade,
    doctor_id int references doctors(id) on delete cascade,
    issued_date date not null
)