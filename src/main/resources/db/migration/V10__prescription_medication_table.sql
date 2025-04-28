CREATE TABLE prescription_medications (
                                          prescription_id INT REFERENCES prescriptions(id) ON DELETE CASCADE,
                                          medication_id INT REFERENCES medications(id) ON DELETE CASCADE,
                                          PRIMARY KEY (prescription_id, medication_id)
);
