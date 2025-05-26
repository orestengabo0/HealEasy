ALTER TABLE appointments
ADD COLUMN zoom_meeting_id VARCHAR(255),
ADD COLUMN zoom_join_url VARCHAR(1024),
ADD COLUMN zoom_start_url VARCHAR(1024),
ADD COLUMN zoom_password VARCHAR(255),
ADD COLUMN duration_minutes INTEGER DEFAULT 30,
ADD COLUMN description TEXT;