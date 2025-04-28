create table medical_records(
    id int primary key,
    patient_id int references patients(id) on delete set null,
    description text not null,
    file_url text not null,
    record_date date default current_date
)