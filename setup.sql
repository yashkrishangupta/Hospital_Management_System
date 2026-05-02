-- ============================================================
-- Hospital Management System — Full Database Setup Script
-- Run: mysql -u root -p < setup.sql
-- ============================================================

CREATE DATABASE IF NOT EXISTS hospital_db;
USE hospital_db;

-- Patients
CREATE TABLE IF NOT EXISTS patients (
    id       INT AUTO_INCREMENT PRIMARY KEY,
    name     VARCHAR(100) NOT NULL,
    age      INT          NOT NULL,
    gender   VARCHAR(10)  NOT NULL,
    contact  VARCHAR(20)  NOT NULL,
    username VARCHAR(50)  NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL
);

-- Doctors (with login credentials)
CREATE TABLE IF NOT EXISTS doctors (
    id             INT AUTO_INCREMENT PRIMARY KEY,
    name           VARCHAR(100) NOT NULL,
    specialization VARCHAR(100) NOT NULL,
    contact        VARCHAR(20)  DEFAULT '',
    username       VARCHAR(50)  UNIQUE,
    password       VARCHAR(100) DEFAULT '1234'
);

-- Appointments
CREATE TABLE IF NOT EXISTS appointments (
    id         INT AUTO_INCREMENT PRIMARY KEY,
    patient_id INT         NOT NULL,
    doctor_id  INT         NOT NULL,
    date       DATE        NOT NULL,
    status     VARCHAR(20) NOT NULL DEFAULT 'Pending',
    FOREIGN KEY (patient_id) REFERENCES patients(id)  ON DELETE CASCADE,
    FOREIGN KEY (doctor_id)  REFERENCES doctors(id)   ON DELETE CASCADE
);

-- Prescriptions
CREATE TABLE IF NOT EXISTS prescriptions (
    id             INT AUTO_INCREMENT PRIMARY KEY,
    appointment_id INT          NOT NULL,
    medicine       VARCHAR(150) NOT NULL,
    dosage         VARCHAR(100) NOT NULL,
    instructions   VARCHAR(255) DEFAULT '',
    notes          TEXT,
    created_at     TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (appointment_id) REFERENCES appointments(id) ON DELETE CASCADE
);

-- Sample Doctors with login credentials
INSERT INTO doctors (name, specialization, contact, username, password) VALUES
  ('Rakshit Jha', 'Cardiologist', '8340133564', 'dr.rakshit', '1234'),
  ('Aman Sundriyal', 'Neurologist', '7302775195', 'dr.aman', '1234'),
  ('Vishvadeep Bhatia', 'Gynecologist',  '9389534860', 'dr.vishvadeep', '1234'),
  ('Devansh Garg', 'General Physician', '9119083233', 'dr.devansh', '1234'),
  ('Mohit Suyal', 'Dermatologist', '8077355061', 'dr.mohit', '1234'),
  ('Aryan Mishra', 'Pediatrician', '6299839416', 'dr.aryan', '1234')
ON DUPLICATE KEY UPDATE name=new.name;

-- Sample Patient
INSERT IGNORE INTO patients (name, age, gender, contact, username, password)
VALUES ('Test Patient', 25, 'Male', '7300693972', 'testpatient', '1234');

SELECT 'Setup complete! Doctor logins: dr.aman / 1234 etc.' AS status;
