package com.project.back_end.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import com.project.back_end.DTO.AppointmentDTO;
import com.project.back_end.DTO.Login;
import com.project.back_end.models.Appointment;
import com.project.back_end.models.Doctor;
import com.project.back_end.models.Patient;
import com.project.back_end.repo.AppointmentRepository;
import com.project.back_end.repo.DoctorRepository;
import com.project.back_end.repo.PatientRepository;
import com.project.back_end.services.TokenService;



@Service
public class PatientService {
// 1. **Add @Service Annotation**:
//    - The `@Service` annotation is used to mark this class as a Spring service component. 
//    - It will be managed by Spring's container and used for business logic related to patients and appointments.
//    - Instruction: Ensure that the `@Service` annotation is applied above the class declaration.

// 2. **Constructor Injection for Dependencies**:
//    - The `PatientService` class has dependencies on `PatientRepository`, `AppointmentRepository`, and `TokenService`.
//    - These dependencies are injected via the constructor to maintain good practices of dependency injection and testing.
//    - Instruction: Ensure constructor injection is used for all the required dependencies.
    private final PatientRepository patientRepository;
    private final AppointmentRepository appointmentRepository;
    private final TokenService tokenService;

    // Constructor injection for dependencies
    public PatientService(PatientRepository patientRepository,
                          AppointmentRepository appointmentRepository,
                          TokenService tokenService) {
        this.patientRepository = patientRepository;
        this.appointmentRepository = appointmentRepository;
        this.tokenService = tokenService;
    }

// 3. **createPatient Method**:
//    - Creates a new patient in the database. It saves the patient object using the `PatientRepository`.
//    - If the patient is successfully saved, the method returns `1`; otherwise, it logs the error and returns `0`.
//    - Instruction: Ensure that error handling is done properly and exceptions are caught and logged appropriately.
    public int createPatient(Patient patient) {
        try {
            patientRepository.save(patient);
            return 1;  // Success
        } catch (Exception e) {
            // Optionally log the exception here
            return 0;  // Failure
        }
    }

// 4. **getPatientAppointment Method**:
//    - Retrieves a list of appointments for a specific patient, based on their ID.
//    - The appointments are then converted into `AppointmentDTO` objects for easier consumption by the API client.
//    - This method is marked as `@Transactional` to ensure database consistency during the transaction.
//    - Instruction: Ensure that appointment data is properly converted into DTOs and the method handles errors gracefully.
    public ResponseEntity<Map<String, Object>> getPatientAppointment(Long id, String token) {
        Map<String, Object> response = new HashMap<>();

        // Extract email from token
        String emailFromToken = tokenService.extractEmail(token);

        // Validate patient ID matches email from token
        Patient patient = patientRepository.findById(id).orElse(null);
        if (patient == null || !patient.getEmail().equals(emailFromToken)) {
            response.put("message", "Unauthorized access");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }

        // Retrieve patient's appointments (convert to AppointmentDTO if needed)
        List<AppointmentDTO> appointments = appointmentRepository.findByPatientId(id);

        response.put("appointments", appointments);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

// 5. **filterByCondition Method**:
//    - Filters appointments for a patient based on the condition (e.g., "past" or "future").
//    - Retrieves appointments with a specific status (0 for future, 1 for past) for the patient.
//    - Converts the appointments into `AppointmentDTO` and returns them in the response.
//    - Instruction: Ensure the method correctly handles "past" and "future" conditions, and that invalid conditions are caught and returned as errors.
    public ResponseEntity<Map<String, Object>> filterByCondition(String condition, Long id) {
        Map<String, Object> response = new HashMap<>();

        int status;
        if ("past".equalsIgnoreCase(condition)) {
            status = 1;  // Past appointments
        } else if ("future".equalsIgnoreCase(condition)) {
            status = 0;  // Future appointments
        } else {
            response.put("message", "Invalid condition. Use 'past' or 'future'.");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        // Fetch appointments by patient ID and status
        List<AppointmentDTO> filteredAppointments = appointmentRepository.findByPatientIdAndStatus(id, status);

        response.put("appointments", filteredAppointments);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
// 6. **filterByDoctor Method**:
//    - Filters appointments for a patient based on the doctor's name.
//    - It retrieves appointments where the doctor’s name matches the given value, and the patient ID matches the provided ID.
//    - Instruction: Ensure that the method correctly filters by doctor's name and patient ID and handles any errors or invalid cases.
    public ResponseEntity<Map<String, Object>> filterByDoctor(String name, Long patientId) {
        Map<String, Object> response = new HashMap<>();

        if (name == null || name.isEmpty()) {
            response.put("message", "Doctor name must be provided");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        // Fetch appointments by patient ID and doctor's name (partial match)
        List<AppointmentDTO> filteredAppointments = appointmentRepository.findByPatientIdAndDoctorNameContainingIgnoreCase(patientId, name);

        response.put("appointments", filteredAppointments);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


// 7. **filterByDoctorAndCondition Method**:
//    - Filters appointments based on both the doctor's name and the condition (past or future) for a specific patient.
//    - This method combines filtering by doctor name and appointment status (past or future).
//    - Converts the appointments into `AppointmentDTO` objects and returns them in the response.
//    - Instruction: Ensure that the filter handles both doctor name and condition properly, and catches errors for invalid input.
    // Method to filter appointments by doctor's name and appointment condition (past/future)
    public ResponseEntity<Map<String, Object>> filterByDoctorAndCondition(String condition, String name, long patientId) {
        Map<String, Object> response = new HashMap<>();

        int status;
        if ("past".equalsIgnoreCase(condition)) {
            status = 1;  // Past appointments
        } else if ("future".equalsIgnoreCase(condition)) {
            status = 0;  // Future appointments
        } else {
            response.put("message", "Invalid condition. Use 'past' or 'future'.");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        if (name == null || name.isEmpty()) {
            response.put("message", "Doctor name must be provided");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        // Fetch filtered appointments by patient ID, doctor name, and status
        List<AppointmentDTO> filteredAppointments = appointmentRepository
                .findByPatientIdAndDoctorNameContainingIgnoreCaseAndStatus(patientId, name, status);

        response.put("appointments", filteredAppointments);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

// 8. **getPatientDetails Method**:
//    - Retrieves patient details using the `tokenService` to extract the patient's email from the provided token.
//    - Once the email is extracted, it fetches the corresponding patient from the `patientRepository`.
//    - It returns the patient's information in the response body.
    //    - Instruction: Make sure that the token extraction process works correctly and patient details are fetched properly based on the extracted email.
    // Method to fetch patient details based on JWT token
    public ResponseEntity<Map<String, Object>> getPatientDetails(String token) {
        Map<String, Object> response = new HashMap<>();

        // Extract email from token
        String email = tokenService.extractEmail(token);
        if (email == null || email.isEmpty()) {
            response.put("message", "Invalid token");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }

        // Retrieve patient by email
        Optional<Patient> patientOpt = patientRepository.findByEmail(email);
        if (!patientOpt.isPresent()) {
            response.put("message", "Patient not found");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        Patient patient = patientOpt.get();
        response.put("patient", patient);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


// 9. **Handling Exceptions and Errors**:
//    - The service methods handle exceptions using try-catch blocks and log any issues that occur. If an error occurs during database operations, the service responds with appropriate HTTP status codes (e.g., `500 Internal Server Error`).
//    - Instruction: Ensure that error handling is consistent across the service, with proper logging and meaningful error messages returned to the client.

// 10. **Use of DTOs (Data Transfer Objects)**:
//    - The service uses `AppointmentDTO` to transfer appointment-related data between layers. This ensures that sensitive or unnecessary data (e.g., password or private patient information) is not exposed in the response.
//    - Instruction: Ensure that DTOs are used appropriately to limit the exposure of internal data and only send the relevant fields to the client.



}
