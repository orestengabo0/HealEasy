CREATE TABLE insurance (
                           id SERIAL PRIMARY KEY,
                           provider VARCHAR(255) NOT NULL,
                           policy_number VARCHAR(100) NOT NULL UNIQUE,
                           patient_id INT UNIQUE REFERENCES patients(id) ON DELETE CASCADE,
                           valid_until DATE NOT NULL
);
