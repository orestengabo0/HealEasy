CREATE TABLE consultations (
                               id SERIAL PRIMARY KEY,
                               appointment_id INT REFERENCES appointments(id) ON DELETE CASCADE,  -- linking consultation to appointment
                               notes TEXT,
                               prescription_id INT REFERENCES prescriptions(id) ON DELETE SET NULL,  -- a consultation can have a prescription
                               created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
