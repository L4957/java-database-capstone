# Designing the Database

## MySQL Database Design

Tables and columns:
1. patients (id, name, email, mobile, date_of_birth, address)
2. doctors (id, name, email, mobile, date_of_birth, address)
3. appointments (id, Doctor, Patient, Appointment_time, Room)
4. admin (id, name, email, mobile, date of birth)

### Table: patients
- id: INT, Primary Key, Auto Increment
- name: varchar(255), Not Null
- email: varchar(255), Not Null
- mobile: varchar(15), Not Null
- date_of_birth: DATETIME, Not Null
- address: varchar(255), Not Null

### Table: doctors
- id: INT, Primary Key, Auto Increment
- name: varchar(255), Not Null
- email: varchar(255), Not Null
- mobile: varchar(15), Not Null
- date_of_birth: DATETIME, Not Null
- address: varchar(255), Not Null

### Table: appointments
- id: INT, Primary Key, Auto Increment
- doctor_id: INT, Foreign Key -> doctors(id)
- patient_id: INT, FOreign Key -> patients(id)
- appointment_time: DATETIME, Not Null
- status: INT (0 = Scheduled, 1 = Completed, 2 = Cancelled)

### Table: doctors
- id: INT, Primary Key, Auto Increment
- name: varchar(255), Not Null
- email: varchar(255), Not Null
- mobile: varchar(15), Not Null
- date_of_birth: DATETIME, Not Null


## MongoDB Collection Design

Tables and columns:
1. Prescriptions (id, Patient, Doctor, Prescription, Comments, Date&Time)
2. Feedback (id, Patient, Doctor, Feedback, Date&Time)

### Collection: prescriptions

```json
{
  "_id": "ObjectId('32dcba654321')",
  "patientName": "Max Sax",
  "appointmentId": 43,
  "medication": "Brufen",
  "dosage": "500mg",
  "doctorNotes": "Take 1 if you feel pain.",
  "refillCount": 2,
  "pharmacy": {
  "name": "Farmacia della Pera",
  "location": "Piazza della Pera"
  }
}
