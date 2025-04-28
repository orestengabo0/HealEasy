CREATE TABLE patients (
                          id SERIAL PRIMARY KEY REFERENCES users(id) ON DELETE CASCADE,  -- inherit from users
                          medical_history TEXT,
                          active_status BOOLEAN DEFAULT TRUE
);
