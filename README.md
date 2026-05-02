# 🏥 Hospital Management System

> A Java Swing + JDBC desktop application with three roles — Admin, Doctor, and Patient — featuring a modern dark-themed UI, live statistics, appointment heatmap, and prescription management.

---

## 📌 Table of Contents

1. [Project Overview](#-project-overview)
2. [Tech Stack](#-tech-stack)
3. [Login Credentials](#-login-credentials)
4. [Project Structure](#-project-structure)
5. [Database Schema](#-database-schema)
6. [Features by Role](#-features-by-role)
7. [Setup Instructions](#-setup-instructions)
8. [How to Run](#-how-to-run)
9. [UI Design System](#-ui-design-system)
10. [Sample Data](#-sample-data)

---

## 📖 Project Overview

The Hospital Management System is a college capstone desktop application built entirely in Java. It supports three user roles with dedicated dashboards, all connected to a MySQL database via JDBC using PreparedStatements throughout.

**Unique features included:**
- 📊 Live statistics dashboard (Admin)
- 🗓️ Doctor availability heatmap with hover tooltips (Admin + Doctor)
- 💊 Full prescription writer with medicine table and doctor notes (Admin + Doctor)

---

## 🛠️ Tech Stack

| Layer       | Technology                          |
|-------------|-------------------------------------|
| Language    | Java 11+                            |
| UI          | Java Swing (JFrame, JTable, JPanel) |
| Database    | MySQL 8.x                           |
| Connectivity| JDBC with PreparedStatement         |
| Driver      | mysql-connector-j-8.x.jar           |
| Build       | Manual javac / run.sh / run.bat     |

---

## 🔐 Login Credentials

### Admin (hardcoded)
| Username | Password |
|----------|----------|
| `admin`  | `1234`   |

### Doctors (stored in database)
| Username    | Password | Doctor Name   | Specialization   |
|-------------|----------|---------------|------------------|
| `dr.priya`  | `1234`   | Priya Sharma  | Cardiologist     |
| `dr.rahul`  | `1234`   | Rahul Mehta   | Neurologist      |
| `dr.anjali` | `1234`   | Anjali Gupta  | Orthopedics      |
| `dr.vikram` | `1234`   | Vikram Nair   | General Physician|
| `dr.sneha`  | `1234`   | Sneha Patel   | Dermatologist    |
| `dr.arjun`  | `1234`   | Arjun Reddy   | Pediatrician     |

### Patient (stored in database)
| Username      | Password |
|---------------|----------|
| `testpatient` | `1234`   |

> New patients can self-register from the Login screen using the **Register Here** button.

---

## 📁 Project Structure

```
HospitalManagement/                          ← Project root
│
├── src/ 
│   ├── app\
│   │   ├── Main.java                       ← Entry point — sets L&F, launches Login      
│   │   └── Login.java                      ← 3-role login screen (Admin / Doctor / Patient)
│   ├── util\
│   │     ├── DBConnection.java             ← MySQL JDBC connection helper
│   │     └── UITheme.java                 ← Design system (colors, fonts, component factories)                    
│   │                       
│   │
│   ├── admin/                               ← package admin  (9 files)
│   │   ├── AdminDashboard.java              ← Dashboard — live stats + 8 menu buttons
│   │   ├── AddDoctor.java                   ← Form: add doctor + set login credentials
│   │   ├── ViewDoctors.java                 ← JTable of all registered doctors
│   │   ├── AddPatient.java                  ← Form: manually register a patient
│   │   ├── ViewPatients.java                ← JTable of all patient records
│   │   ├── ViewAllAppointments.java         ← Full appointment log (all patients & doctors)
│   │   ├── SearchPanel.java                 ← Keyword search for patients & doctors
│   │   ├── DoctorHeatmap.java               ← ⭐ Monthly calendar heatmap with hover tooltips
│   │   └── PrescriptionWriter.java          ← ⭐ Write prescriptions for any appointment
│   │
│   ├── doctor/                              ← package doctor  (7 files)
│   │   ├── DoctorDashboard.java             ← Dashboard — personal stats + 6 menu buttons
│   │   ├── MyAppointments.java              ← Own appointments with status filter
│   │   ├── UpdateAppointmentStatus.java     ← Confirm / Cancel / Reset appointment status
│   │   ├── DoctorPrescriptionWriter.java    ← Write prescriptions (own appointments only)
│   │   ├── MyPatients.java                  ← All unique patients sorted by visit count
│   │   ├── DoctorScheduleChart.java         ← Heatmap pre-filtered to this doctor
│   │   └── DoctorProfile.java               ← View profile info + change password
│   │
│   └── patient/                             ← package patient  (5 files)
│       ├── Register.java                    ← Self-registration with password confirmation
│       ├── PatientDashboard.java            ← Dashboard — 4 action cards
│       ├── BookAppointment.java             ← Book via doctor dropdown + date picker
│       ├── ViewAppointments.java            ← Own appointments with colour-coded status
│       └── ViewPrescription.java            ← View medicines prescribed by doctor
│
├── lib/                                     ← JDBC driver goes here (create manually)
│   └── mysql-connector-j-9.6.0.jar         ← Download from dev.mysql.com
│
├── out/                                     ← Compiled .class files (auto-created on build)
│
├── setup.sql                                ← DB schema + 6 sample doctors + 1 patient
├── run.bat                                  ← Build & run script (Windows)
└── README.md                                ← This file
```

### File count summary

| Package       | Files | Description                        |
|---------------|-------|------------------------------------|
| root `src/`   | 4     | Main, Login, DBConnection, UITheme |
| `admin/`      | 9     | Full admin management suite        |
| `doctor/`     | 7     | Doctor portal and tools            |
| `patient/`    | 5     | Patient portal                     |
| **Total**     | **25**| Java source files                  |

---

## 🗄️ Database Schema

```
hospital_db
│
├── patients
│   ├── id          INT  AUTO_INCREMENT  PK
│   ├── name        VARCHAR(100)
│   ├── age         INT
│   ├── gender      VARCHAR(10)
│   ├── contact     VARCHAR(20)
│   ├── username    VARCHAR(50)   UNIQUE
│   └── password    VARCHAR(100)
│
├── doctors
│   ├── id             INT  AUTO_INCREMENT  PK
│   ├── name           VARCHAR(100)
│   ├── specialization VARCHAR(100)
│   ├── contact        VARCHAR(20)
│   ├── username       VARCHAR(50)   UNIQUE
│   └── password       VARCHAR(100)
│
├── appointments
│   ├── id          INT  AUTO_INCREMENT  PK
│   ├── patient_id  INT  FK → patients.id
│   ├── doctor_id   INT  FK → doctors.id
│   ├── date        DATE
│   └── status      VARCHAR(20)   DEFAULT 'Pending'
│                   [ Pending | Confirmed | Cancelled ]
│
└── prescriptions
    ├── id              INT  AUTO_INCREMENT  PK
    ├── appointment_id  INT  FK → appointments.id
    ├── medicine        VARCHAR(150)
    ├── dosage          VARCHAR(100)
    ├── instructions    VARCHAR(255)
    ├── notes           TEXT
    └── created_at      TIMESTAMP  DEFAULT CURRENT_TIMESTAMP
```

**Relationships:**
- One patient → many appointments
- One doctor → many appointments
- One appointment → many prescription rows (one per medicine)
- All foreign keys use `ON DELETE CASCADE`

---

## ✨ Features by Role

### 🛡️ Admin
| Feature | Description |
|---------|-------------|
| Live Stats Dashboard | Real-time count of doctors, patients, total appointments, and today's appointments — auto-refreshes on every save action |
| Add Doctor | Register a doctor with name, specialization, contact, and login credentials |
| View Doctors | Searchable JTable of all registered doctors |
| Add Patient | Manually register a patient account |
| View Patients | JTable showing all patient records |
| View All Appointments | Complete appointment log across all patients and doctors with status column |
| Search Panel | Keyword search for patients (by name/username) and doctors (by name/specialization) |
| Doctor Heatmap ⭐ | Monthly calendar view with color-coded appointment load per day. Supports per-doctor filtering and hover tooltips showing date + count |
| Prescription Writer ⭐ | Select any appointment, add medicine rows (name + dosage + instructions), write diagnosis notes, and save — overwrites on re-save |

---

### 👨‍⚕️ Doctor
| Feature | Description |
|---------|-------------|
| Personal Stats Dashboard | Shows total appointments, today's count, pending count, and confirmed count — all scoped to this doctor only |
| My Appointments | Filterable table: All / Pending / Confirmed / Cancelled / Today |
| Update Appointment Status | Select a row and click Confirm ✔, Cancel ✖, or Reset to Pending — updates DB immediately |
| Write Prescription | Same prescription writer as admin but shows only this doctor's appointments |
| My Patients | All unique patients who have visited, sorted by visit count |
| My Schedule Chart | Opens the heatmap pre-filtered to this doctor's bookings |
| My Profile | Read-only view of name, specialization, contact, username — plus change password with current password verification |

---

### 🧑 Patient
| Feature | Description |
|---------|-------------|
| Self Register | Create account with name, age, gender, contact, username, and password (with confirmation) |
| Login | Authenticate against the patients table |
| Book Appointment | Choose a doctor from a live dropdown and enter a date — past dates are rejected |
| My Appointments | Personal appointment list with color-coded status: 🟠 Pending · 🟢 Confirmed · 🔴 Cancelled |
| My Prescriptions | Select an appointment to view all medicines (name, dosage, instructions) and doctor's notes |

---

## ⚙️ Setup Instructions

### Step 1 — Install Prerequisites
- **Java JDK 11+** → https://adoptium.net/
- **MySQL Server 8.x** → https://dev.mysql.com/downloads/mysql/

### Step 2 — Create the Database
Open a terminal and run:
```bash
mysql -u root -p < setup.sql
```
Or open **MySQL Workbench**, paste the contents of `setup.sql`, and execute. This creates the database, all 4 tables, and inserts 6 sample doctors + 1 test patient.

### Step 3 — Add the JDBC Driver
1. Download **MySQL Connector/J** from https://dev.mysql.com/downloads/connector/j/
2. Create a folder named `lib/` in the project root
3. Place the downloaded `.jar` inside it
4. Rename the file (or update the build script) to: `mysql-connector-j-8.0.33.jar`

### Step 4 — Configure Database Password
Open `src/DBConnection.java` and update line 5:
```java
private static final String PASSWORD = "your_mysql_root_password";
```
If your MySQL user is not `root`, also update the `USER` field.

---

## ▶️ How to Run

### Linux / macOS

```
run.bat
```

Script compile all 25 Java files and launch `Main.java` in one step.

---

## 🎨 UI Design System

All styling lives in `UITheme.java`. It provides factory methods so every screen uses the same colors, fonts, and components.

**Color Palette:**

| Token | Hex | Used For |
|-------|-----|----------|
| `BG_DARK` | `#0A192F` | Main window background |
| `BG_CARD` | `#112848` | Card / panel surfaces |
| `BG_INPUT` | `#163250` | Text field backgrounds |
| `ACCENT` | `#00D2BE` | Teal — primary buttons, borders |
| `STAT_GREEN` | `#00DC82` | Patient role, confirmed status |
| `STAT_BLUE` | `#50A0FF` | Doctor role, info elements |
| `STAT_ORANGE` | `#FF8C3C` | Today's count, warnings |
| `ACCENT_DANGER` | `#FF505A` | Danger buttons, cancelled status |

**Font:** Segoe UI throughout — Bold for headers and labels, Plain for body text.

---

## 📋 Sample Data

After running `setup.sql`, the database contains:

**6 Doctors** (all password: `1234`):
- Dr. Rakshit Jha — Cardiologist (`dr.rakshit`)
- Dr. Aman Sundriyal — Neurologist (`dr.aman`)
- Dr. Vishvadeep Bhatia — Gynecologist (`dr.vishvadeep`)
- Dr. Devansh Garg — General Physician (`dr.devansh`)
- Dr. Mohit Suyal — Dermatologist (`dr.mohit`)
- Dr. Aryan Mishra — Pediatrician (`dr.aryan`)

**1 Patient:**
- Test Patient — username: `testpatient`, password: `1234`

---

## 📝 Notes for Evaluators

- All database queries use `PreparedStatement` — no raw SQL string concatenation
- `DBConnection.java` is the single source of truth for DB connectivity
- Each role's files are in their own Java package (`admin`, `doctor`, `patient`)
- `UITheme.java` acts as a design system — removing the need to repeat colors/fonts across 25 files
- Appointment status flows: Patient books → `Pending` → Doctor sets → `Confirmed` or `Cancelled`
- Prescriptions are linked to appointments, not patients directly, preserving visit-level detail

---

*© 2025 Hospital Management System — Built for academic purposes*
