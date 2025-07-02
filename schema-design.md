# Designing the Database

## MySQL database

Tables and columns:
1. Patients (id, name, email, mobile, date of birth, address)
2. Doctors (id, name, email, mobile, date of birth, address)
3. Appointments (id, Doctor, Patient, Time-slot, Room)
4. Admin (id, name, email, mobile, date of birth)

## MongoDB database

Tables and columns:
1. Prescriptions (id, Patient, Doctor, Prescription, Comments, Date&Time)
2. Feedback (id, Patient, Doctor, Feedback, Date&Time)
