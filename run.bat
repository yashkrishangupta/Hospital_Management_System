@echo off
REM ============================================================
REM Hospital Management System — Build & Run (Windows)
REM Requirements: Java 11+, MySQL JDBC driver in .\lib\
REM ============================================================

if not exist out mkdir out

set JDBC_JAR=lib\mysql-connector-j-9.6.0.jar

if not exist %JDBC_JAR% (
    echo ERROR: MySQL JDBC driver not found at %JDBC_JAR%
    echo Download from: https://dev.mysql.com/downloads/connector/j/
    echo Place the jar in the lib\ folder and rename it to mysql-connector-j-8.0.33.jar
    pause
    exit /b 1
)

echo Compiling all sources...
javac -cp "src;%JDBC_JAR%" ^
  src\util\DBConnection.java ^
  src\util\UITheme.java ^
  src\app\Main.java ^
  src\app\Login.java ^
  src\admin\AdminDashboard.java ^
  src\admin\AddDoctor.java ^
  src\admin\ViewDoctors.java ^
  src\admin\AddPatient.java ^
  src\admin\ViewPatients.java ^
  src\admin\ViewAllAppointments.java ^
  src\admin\SearchPanel.java ^
  src\admin\DoctorHeatmap.java ^
  src\admin\PrescriptionWriter.java ^
  src\doctor\DoctorDashboard.java ^
  src\doctor\MyAppointments.java ^
  src\doctor\UpdateAppointmentStatus.java ^
  src\doctor\DoctorPrescriptionWriter.java ^
  src\doctor\MyPatients.java ^
  src\doctor\DoctorScheduleChart.java ^
  src\doctor\DoctorProfile.java ^
  src\patient\Register.java ^
  src\patient\PatientDashboard.java ^
  src\patient\BookAppointment.java ^
  src\patient\ViewAppointments.java ^
  src\patient\ViewPrescription.java ^
  src\patient\PatientProfile.java ^
  -d out

if %ERRORLEVEL%==0 (
    echo Compilation successful! Starting application...
    java -cp "out;%JDBC_JAR%" app.Main
) else (
    echo Compilation failed. Fix the errors above.
    pause
)
