# Hospital Management System

> A Java Swing + JDBC desktop application with three roles — Admin, Doctor, and Patient — featuring live statistics, doctor availability heatmap, and full prescription management.

---

## Table of Contents

1. [Project Overview](#project-overview)
2. [Tech Stack](#tech-stack)
3. [Login Credentials](#login-credentials)
4. [Project Structure](#project-structure)
5. [Database Schema](#database-schema)
6. [Features by Role](#features-by-role)
7. [Setup Instructions](#setup-instructions)
8. [How to Run](#how-to-run)
9. [UI Design System](#ui-design-system)
10. [Sample Data](#sample-data)

---

## Project Overview

The Hospital Management System is a college capstone desktop application built entirely in Java. It supports three user roles with dedicated dashboards, all connected to a MySQL database via JDBC using PreparedStatements throughout.

**Unique features included:**
- Live statistics dashboard (Admin)
- Doctor availability heatmap with hover tooltips (Admin + Doctor)
- Full prescription writer with medicine table, notes, right-click delete, and full prescription delete (Admin + Doctor)
- Delete functionality for Doctors, Patients, and Appointments with confirmation dialogs
- Patient and Doctor profile pages with password change

---

## Tech Stack

| Layer        | Technology                          |
|--------------|-------------------------------------|
| Language     | Java 11+                            |
| UI           | Java Swing (JFrame, JTable, JPanel) |
| Database     | MySQL 8.x                           |
| Connectivity | JDBC with PreparedStatement         |
| Driver       | mysql-connector-j-8.x.jar           |
| Build        | Manual javac / run.sh / run.bat     |

---

## Login Credentials

### Admin (hardcoded)
| Username | Password |
|----------|----------|
| `admin`  | `1234`   |

### Doctors (stored in database)
| Username    | Password | Doctor Name    | Specialization    |
|-------------|----------|----------------|-------------------|
| `dr.rakshit`    | `1234`   | Rakshit Jha        | Cardiologist      |
| `dr.aman`       | `1234`   | Aman Sundriyal     | Neurologist       |
| `dr.vishvadeep` | `1234`   | Vishvadeep Bhatia  | Gynecologist      |
| `dr.devansh`    | `1234`   | Devansh Garg       | General Physician |
| `dr.mohit`      | `1234`   | Mohit Suyal        | Dermatologist     |
| `dr.aryan`      | `1234`   | Aryan Mishra       | Pediatrician      |

### Patient (stored in database)
| Username      | Password |
|---------------|----------|
| `testpatient` | `1234`   |

> New patients can self-register from the Login screen using the **Register Here** button.

---

## Project Structure

```
HospitalManagement/
│
├── src/
│   │
│   │   # Core (4 files)
│   ├── app/
│   │   ├── Main.java                        ← Entry point
│   │   └── Login.java                       ← 3-role login screen
│   │
│   ├── util/
│   │   ├── DBConnection.java                ← MySQL JDBC connection helper
│   │   └── UITheme.java                     ← Design system (colors, fonts, factories)
│   │
│   │   # package admin (9 files)
│   ├── admin/
│   │   ├── AdminDashboard.java              ← Live stats + 8 menu buttons
│   │   ├── AddDoctor.java                   ← Add doctor with login credentials
│   │   ├── ViewDoctors.java                 ← JTable + Delete Doctor button
│   │   ├── AddPatient.java                  ← Register a patient
│   │   ├── ViewPatients.java                ← JTable + Delete Patient button
│   │   ├── ViewAllAppointments.java         ← Full log + Delete Appointment button
│   │   ├── SearchPanel.java                 ← Keyword search
│   │   ├── DoctorHeatmap.java               ← Monthly calendar heatmap
│   │   └── PrescriptionWriter.java          ← Write / delete prescriptions
│   │
│   │   # package doctor (7 files)
│   ├── doctor/
│   │   ├── DoctorDashboard.java             ← Personal stats + 6 menu buttons
│   │   ├── MyAppointments.java              ← Filterable appointment table
│   │   ├── UpdateAppointmentStatus.java     ← Confirm / Cancel / Pending
│   │   ├── DoctorPrescriptionWriter.java    ← Write / delete own prescriptions
│   │   ├── MyPatients.java                  ← Unique patients + visit count
│   │   ├── DoctorScheduleChart.java         ← Heatmap filtered to this doctor
│   │   └── DoctorProfile.java               ← Profile view + change password
│   │
│   │   # package patient (6 files)
│   └── patient/
│       ├── Register.java                    ← Self-registration
│       ├── PatientDashboard.java            ← 4 action cards
│       ├── BookAppointment.java             ← Doctor dropdown + date picker
│       ├── ViewAppointments.java            ← Colour-coded appointment status
│       ├── ViewPrescription.java            ← View medicines + doctor notes
│       └── PatientProfile.java              ← Profile view + change password
│
├── lib/
│   └── mysql-connector-j-8.0.33.jar        ← Place JDBC driver here
│
├── out/                                     ← Compiled .class files (auto-created)
│
├── setup.sql                                ← DB schema + seed data
├── run.sh                                   ← Linux / macOS build & run
├── run.bat                                  ← Windows build & run
└── README.md
```

### File count

| Package    | Files | Contents |
|------------|-------|---------|
| `app/`     | 2     | Main, Login |
| `util/`    | 2     | DBConnection, UITheme |
| `admin/`   | 9     | Full admin management suite |
| `doctor/`  | 7     | Doctor portal and tools |
| `patient/` | 6     | Patient portal |
| **Total**  | **26**| Java source files |

---

## Database Schema

```
hospital_db
│
├── patients
│   ├── id          INT   AUTO_INCREMENT  PK
│   ├── name        VARCHAR(100)
│   ├── age         INT
│   ├── gender      VARCHAR(10)
│   ├── contact     VARCHAR(20)
│   ├── username    VARCHAR(50)  UNIQUE
│   └── password    VARCHAR(100)
│
├── doctors
│   ├── id             INT   AUTO_INCREMENT  PK
│   ├── name           VARCHAR(100)
│   ├── specialization VARCHAR(100)
│   ├── contact        VARCHAR(20)
│   ├── username       VARCHAR(50)  UNIQUE
│   └── password       VARCHAR(100)
│
├── appointments
│   ├── id          INT   AUTO_INCREMENT  PK
│   ├── patient_id  INT   FK → patients.id  CASCADE
│   ├── doctor_id   INT   FK → doctors.id   CASCADE
│   ├── date        DATE
│   └── status      VARCHAR(20)  DEFAULT 'Pending'
│                   [ Pending | Confirmed | Cancelled ]
│
└── prescriptions
    ├── id              INT   AUTO_INCREMENT  PK
    ├── appointment_id  INT   FK → appointments.id  CASCADE
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

## Features by Role

### Admin
| Feature | Description |
|---------|-------------|
| Live Stats Dashboard | Real-time count of doctors, patients, total and today's appointments — refreshes on every save |
| Add Doctor | Register a doctor with name, specialization, contact, username, password |
| View Doctors | JTable of all doctors — select a row and click Delete with confirmation |
| Add Patient | Manually register a patient account |
| View Patients | JTable of all patients — select a row and click Delete with confirmation |
| View All Appointments | Full appointment log with colour-coded status — select and delete |
| Search Panel | Keyword search for patients by name/username and doctors by name/specialization |
| Doctor Heatmap | Monthly calendar heatmap with per-doctor filter and hover tooltips |
| Prescription Writer | Write prescriptions — add medicines, right-click to remove rows, clear notes, delete entire prescription |

### Doctor
| Feature | Description |
|---------|-------------|
| Personal Stats Dashboard | Total, today, pending, confirmed appointment counts — scoped to this doctor |
| My Appointments | Filter by All / Pending / Confirmed / Cancelled / Today |
| Update Appointment Status | Confirm, Cancel, or reset to Pending with one click |
| Write Prescription | Add medicines with dosage and instructions — right-click to delete rows, clear notes, or delete full prescription |
| My Patients | All unique patients with total visit count |
| My Schedule Chart | Heatmap pre-filtered to this doctor's appointments |
| My Profile | View name, specialization, contact, username — change password with verification |

### Patient
| Feature | Description |
|---------|-------------|
| Self Register | Name, age, gender, contact, username, password with confirmation |
| Login | Authenticate against the patients table |
| Book Appointment | Select a doctor and date — past dates are rejected |
| My Appointments | Colour-coded status: Pending / Confirmed / Cancelled |
| My Prescriptions | View medicines, dosage, instructions, and doctor notes per appointment |
| My Profile | View personal info — change password with current password verification |

---

## Setup Instructions

### Step 1 — Install Prerequisites
- Java JDK 11+ → https://adoptium.net/
- MySQL Server 8.x → https://dev.mysql.com/downloads/mysql/

### Step 2 — Create the Database
```bash
mysql -u root -p < setup.sql
```
Or open MySQL Workbench, paste `setup.sql`, and execute. Creates all 4 tables and inserts 6 sample doctors and 1 test patient.

### Step 3 — Add the JDBC Driver
1. Download MySQL Connector/J from https://dev.mysql.com/downloads/connector/j/
2. Create a `lib/` folder in the project root
3. Place the jar inside and name it `mysql-connector-j-8.0.33.jar`

### Step 4 — Configure Database Password
Open `src/util/DBConnection.java` and set:
```java
private static final String PASSWORD = "your_mysql_password";
```

---

## How to Run

**Windows:**
```
run.bat
```

**Linux / macOS:**
```bash
chmod +x run.sh
./run.sh
```

Both scripts compile all 26 Java files and launch `app.Main`.

---

## UI Design System

All styling is centralized in `src/util/UITheme.java`.

**Theme: Classic Hospital — Blue & White**

| Token | Color (RGB) | Used For |
|-------|-------------|---------|
| `BG_PAGE` | `(236, 245, 255)` | Main window background |
| `BG_CARD` | `(255, 255, 255)` | Card / panel surfaces |
| `BG_INPUT` | `(250, 253, 255)` | Input field backgrounds |
| `ACCENT` | `(0, 102, 204)` | Primary buttons, borders, highlights |
| `ACCENT_HOVER` | `(0, 76, 153)` | Button hover state |
| `ACCENT_DANGER` | `(204, 0, 0)` | Delete / danger buttons |
| `ACCENT_WARN` | `(230, 130, 0)` | Warning messages |
| `STAT_GREEN` | `(0, 150, 90)` | Confirmed status, patient stats |
| `STAT_BLUE` | Same as ACCENT | Doctor role stats |
| `STAT_ORANGE` | `(220, 120, 0)` | Today count |
| `TEXT_PRIMARY` | `(28, 28, 30)` | All body text |
| `TEXT_MUTED` | `(95, 110, 130)` | Labels, hints, subtitles |
| `BORDER_COLOR` | `(190, 215, 240)` | All borders |
| `BLUE_DARK` | `(0, 60, 130)` | Section headings |
| `SOFT_BLUE` | `(220, 235, 255)` | Hover tint on secondary buttons |

**Font:** Segoe UI Emoji throughout — supports emoji rendering on Windows. Bold for headers and labels, Plain for body text.

---

## Sample Data

After running `setup.sql`:

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

## Notes for Evaluators

- All database queries use `PreparedStatement` — no raw SQL string concatenation
- `DBConnection.java` is the single source of truth for DB connectivity
- Each role's files are organized in their own Java package (`admin`, `doctor`, `patient`, `util`, `app`)
- `UITheme.java` acts as a centralized design system — all 26 files import it
- Appointment status flow: Patient books → `Pending` → Doctor sets → `Confirmed` or `Cancelled`
- Prescriptions are linked to appointments, not patients directly, preserving visit-level detail
- Delete operations on Doctors and Patients cascade to remove all their appointments and prescriptions
- Window size is standardized to 1024 x 700 across all 26 screens

---

*© 2025 Hospital Management System — Built for academic purposes*