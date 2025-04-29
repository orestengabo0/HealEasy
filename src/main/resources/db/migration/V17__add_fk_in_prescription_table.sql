alter table prescriptions
add column consultation_id int references consultations(id) on delete cascade