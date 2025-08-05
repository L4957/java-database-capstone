package com.project.back_end.models;

import java.beans.Transient;
import java.time.LocalDateTime;
import java.util.List;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;


import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Future;
import java.time.LocalDate;
import java.time.LocalTime;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;

// @Entity annotation:
//    - Marks the class as a JPA entity, meaning it represents a table in the database.
//    - Required for persistence frameworks (e.g., Hibernate) to map the class to a database table.

@Entity
public class Appointment {

 
// 1. 'id' field:
//    - Type: private Long
//    - Description:
//      - Represents the unique identifier for each appointment.
//      - The @Id annotation marks it as the primary key.
//      - The @GeneratedValue(strategy = GenerationType.IDENTITY) annotation auto-generates the ID value when a new record is inserted into the database.
   @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


// 2. 'doctor' field:
//    - Type: private Doctor
//    - Description:
//      - Represents the doctor assigned to this appointment.
//      - The @ManyToOne annotation defines the relationship, indicating many appointments can be linked to one doctor.
//      - The @NotNull annotation ensures that an appointment must be associated with a doctor when created.

    @ManyToOne
    @JoinColumn(name = "doctor_id")
    @JsonManagedReference
    @NotNull(message = "Doctor cannot be null")
    private Doctor doctor;

// 3. 'patient' field:
//    - Type: private Patient
//    - Description:
//      - Represents the patient assigned to this appointment.
//      - The @ManyToOne annotation defines the relationship, indicating many appointments can be linked to one patient.
//      - The @NotNull annotation ensures that an appointment must be associated with a patient when created.

    @ManyToOne
    @JoinColumn(name = "patient_id")
    @JsonManagedReference
    @NotNull
    private Patient patient;

// 4. 'appointmentTime' field:
//    - Type: private LocalDateTime
//    - Description:
//      - Represents the date and time when the appointment is scheduled to occur.
//      - The @Future annotation ensures that the appointment time is always in the future when the appointment is created.
//      - It uses LocalDateTime, which includes both the date and time for the appointment.

    @Future(message = "Appointment time must be in the future")    
    private LocalDateTime appointmentTime;
    private LocalDateTime dateTime;

// 5. 'status' field:
//    - Type: private int
//    - Description:
//      - Represents the current status of the appointment. It is an integer where:
//        - 0 means the appointment is scheduled.
//        - 1 means the appointment has been completed.
//      - The @NotNull annotation ensures that the status field is not null.

    @NotNull(message = "Status cannot be null")
    private Integer status;
    
// 6. 'getEndTime' method:
//    - Type: private LocalDateTime
//    - Description:
//      - This method is a transient field (not persisted in the database).
//      - It calculates the end time of the appointment by adding one hour to the start time (appointmentTime).
//      - It is used to get an estimated appointment end time for display purposes.
    
    @Transient
    private LocalDateTime getEndTime() {
        return appointmentTime.plusHours(1);
    }

// 7. 'getAppointmentDate' method:
//    - Type: private LocalDate
//    - Description:
//      - This method extracts only the date part from the appointmentTime field.
//      - It returns a LocalDate object representing just the date (without the time) of the scheduled appointment.

    @Transient
    private LocalDate getAppointmentDate() {
       return appointmentTime.toLocalDate(); 
    }


// 8. 'getAppointmentTimeOnly' method:
//    - Type: private LocalTime
//    - Description:
//      - This method extracts only the time part from the appointmentTime field.
//      - It returns a LocalTime object representing just the time (without the date) of the scheduled appointment.
    @Transient
    private LocalTime getAppointmentTimeOnly() {
       return appointmentTime.toLocalTime(); 
    }
// 9. Constructor(s):
//    - A no-argument constructor is implicitly provided by JPA for entity creation.
//    - A parameterized constructor can be added as needed to initialize fields.
    public Appointment() {}

    public Appointment(Long id, Doctor doctor, Patient patient, LocalDateTime appointmentTime, int status) {
        this.id = id;
        this.doctor = doctor;
        this.patient = patient;
        this.appointmentTime = appointmentTime;
        this.status = status;
    }
// 10. Getters and Setters:
//    - Standard getter and setter methods are provided for accessing and modifying the fields: id, doctor, patient, appointmentTime, status, etc.
// Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Doctor getDoctor() {
        return doctor;
    }

    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public LocalDateTime getAppointmentTime() {
        return appointmentTime;
    }

    public void setAppointmentTime(LocalDateTime appointmentTime) {
        this.appointmentTime = appointmentTime;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }
}

