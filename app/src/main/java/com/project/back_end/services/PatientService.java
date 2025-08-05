package com.project.back_end.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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
    @Transactional
    public List<AppointmentDTO> getPatientAppointment(Long patientId, String token) {
        // Validate token (assuming you have a token validation method)
        if (!tokenService.validateToken(token, "patient")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid token");
        }

        // Fetch appointments from repository
        List<Appointment> appointments = appointmentRepository.findByPatientId(patientId);

        // Convert Appointment entities to AppointmentDTOs
        List<AppointmentDTO> appointmentDTOs = appointments.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());

        return appointmentDTOs;
    }

     
    // Helper method to convert Appointment to AppointmentDTO
    private AppointmentDTO convertToDTO(Appointment appointment) {
        return new AppointmentDTO(
            appointment.getId(),
            appointment.getDoctor().getId(),
            appointment.getDoctor().getName(),
            appointment.getPatient().getId(),
            appointment.getPatient().getName(),
            appointment.getPatient().getEmail(),
            appointment.getPatient().getPhone(),
            appointment.getPatient().getAddress(),
            appointment.getAppointmentTime(),  
            appointment.getStatus(),
            appointment.getAppointmentTime().toLocalDate(),
            appointment.getAppointmentTime().toLocalTime(),
            appointment.getAppointmentTime().plusHours(1)
        );
    }

// 5. **filterByCondition Method**:
//    - Filters appointments for a patient based on the condition (e.g., "past" or "future").
//    - Retrieves appointments with a specific status (0 for future, 1 for past) for the patient.
//    - Converts the appointments into `AppointmentDTO` and returns them in the response.
//    - Instruction: Ensure the method correctly handles "past" and "future" conditions, and that invalid conditions are caught and returned as errors.
    public ResponseEntity<Map<String, Object>> filterByCondition(String condition, Long patientId) {
        Map<String, Object> response = new HashMap<>();

        // Validate input parameters
        if (condition == null || patientId == null) {
            response.put("message", "Condition and patient ID must not be null.");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        int status;
        if ("past".equalsIgnoreCase(condition)) {
            status = 1;  // Past appointments
        } else if ("future".equalsIgnoreCase(condition)) {
            status = 0;  // Future appointments
        } else {
            response.put("message", "Invalid condition. Use 'past' or 'future'.");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        // Fetch appointments from repository by patient ID and status
        List<Appointment> appointments = appointmentRepository.findByPatientIdAndStatus(patientId, status);

        // Convert Appointment entities to AppointmentDTOs
        List<AppointmentDTO> appointmentDTOs = appointments.stream()
            .map(appointment -> new AppointmentDTO(
                appointment.getId(),
                appointment.getDoctor().getId(),
                appointment.getDoctor().getName(),
                appointment.getPatient().getId(),
                appointment.getPatient().getName(),
                appointment.getPatient().getEmail(),
                appointment.getPatient().getPhone(),
                appointment.getPatient().getAddress(),
                appointment.getAppointmentTime(),  
                appointment.getStatus(),
                appointment.getAppointmentTime().toLocalDate(),
                appointment.getAppointmentTime().toLocalTime(),
                appointment.getAppointmentTime().plusHours(1)
            ))
            .collect(Collectors.toList());

        response.put("appointments", appointmentDTOs);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // 6. **filterByDoctor Method**:
    //    - Filters appointments for a patient based on the doctor's name.
    //    - It retrieves appointments where the doctor’s name matches the given value, and the patient ID matches the provided ID.
    //    - Instruction: Ensure that the method correctly filters by doctor's name and patient ID and handles any errors or invalid cases.
    public ResponseEntity<Map<String, Object>> filterByDoctor(String doctorName, Long patientId) {
    Map<String, Object> response = new HashMap<>();

    // Validate input parameters
    if (doctorName == null || doctorName.trim().isEmpty() || patientId == null) {
        response.put("message", "Doctor name and patient ID must not be null or empty.");
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    // Fetch appointments matching doctor name and patient ID
    List<Appointment> appointments = appointmentRepository.filterByDoctorNameAndPatientId(doctorName, patientId);

    if (appointments.isEmpty()) {
        response.put("message", "No appointments found for the given doctor and patient.");
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    // Convert Appointment entities to AppointmentDTOs
    List<AppointmentDTO> appointmentDTOs = appointments.stream()
        .map(appointment -> new AppointmentDTO(
            appointment.getId(),
            appointment.getDoctor().getId(),
            appointment.getDoctor().getName(),
            appointment.getPatient().getId(),
            appointment.getPatient().getName(),
            appointment.getPatient().getEmail(),
            appointment.getPatient().getPhone(),
            appointment.getPatient().getAddress(),
            appointment.getAppointmentTime(),  
            appointment.getStatus(),
            appointment.getAppointmentTime().toLocalDate(),
            appointment.getAppointmentTime().toLocalTime(),
            appointment.getAppointmentTime().plusHours(1)
        ))
        .collect(Collectors.toList());

    response.put("appointments", appointmentDTOs);
    return new ResponseEntity<>(response, HttpStatus.OK);
}


// 7. **filterByDoctorAndCondition Method**:
//    - Filters appointments based on both the doctor's name and the condition (past or future) for a specific patient.
//    - This method combines filtering by doctor name and appointment status (past or future).
//    - Converts the appointments into `AppointmentDTO` objects and returns them in the response.
//    - Instruction: Ensure that the filter handles both doctor name and condition properly, and catches errors for invalid input.
    // Method to filter appointments by doctor's name and appointment condition (past/future)
    public ResponseEntity<Map<String, Object>> filterByDoctorAndCondition(String condition, String doctorName, Long patientId) {
    Map<String, Object> response = new HashMap<>();

    // Validate inputs
    if (condition == null || doctorName == null || doctorName.trim().isEmpty() || patientId == null) {
        response.put("message", "Condition, doctor name, and patient ID must not be null or empty.");
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    int status;
    if ("past".equalsIgnoreCase(condition)) {
        status = 1;  // Past appointments
    } else if ("future".equalsIgnoreCase(condition)) {
        status = 0;  // Future appointments
    } else {
        response.put("message", "Invalid condition. Use 'past' or 'future'.");
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    // Fetch appointments filtered by doctorName, patientId, and status
    List<Appointment> appointments = appointmentRepository.findByPatientIdAndDoctorNameContainingIgnoreCaseAndStatus(patientId, doctorName, status);

    if (appointments.isEmpty()) {
        response.put("message", "No appointments found matching the criteria.");
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    // Convert to AppointmentDTO list
    List<AppointmentDTO> appointmentDTOs = appointments.stream()
        .map(appointment -> new AppointmentDTO(
            appointment.getId(),
            appointment.getDoctor().getId(),
            appointment.getDoctor().getName(),
            appointment.getPatient().getId(),
            appointment.getPatient().getName(),
            appointment.getPatient().getEmail(),
            appointment.getPatient().getPhone(),
            appointment.getPatient().getAddress(),
            appointment.getAppointmentTime(),  
            appointment.getStatus(),
            appointment.getAppointmentTime().toLocalDate(),
            appointment.getAppointmentTime().toLocalTime(),
            appointment.getAppointmentTime().plusHours(1)
        ))
        .collect(Collectors.toList());

    response.put("appointments", appointmentDTOs);
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
        String email = tokenService.extractIdentifier(token);
        if (email == null || email.isEmpty()) {
            response.put("message", "Invalid token");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }

        Patient patient = patientRepository.findByEmail(email);
        if (patient == null) {
            response.put("message", "Patient not found");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
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
