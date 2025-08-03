package com.project.back_end.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import java.util.HashMap;
import java.util.Map;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;


import com.project.back_end.models.Appointment;
import com.project.back_end.repo.AppointmentRepository;
import com.project.back_end.repo.DoctorRepository;
import com.project.back_end.repo.PatientRepository;

@Service
public class AppointmentService {
// 1. **Add @Service Annotation**:
//    - To indicate that this class is a service layer class for handling business logic.
//    - The `@Service` annotation should be added before the class declaration to mark it as a Spring service component.
//    - Instruction: Add `@Service` above the class definition.

// 2. **Constructor Injection for Dependencies**:
//    - The `AppointmentService` class requires several dependencies like `AppointmentRepository`, `Service`, `TokenService`, `PatientRepository`, and `DoctorRepository`.
//    - These dependencies should be injected through the constructor.
//    - Instruction: Ensure constructor injection is used for proper dependency management in Spring.
    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final TokenService tokenService;

    // Constructor for dependency injection
    public AppointmentService(AppointmentRepository appointmentRepository,
                              PatientRepository patientRepository,
                              DoctorRepository doctorRepository,
                              TokenService tokenService) {
        this.appointmentRepository = appointmentRepository;
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
        this.tokenService = tokenService;
    }

// 3. **Add @Transactional Annotation for Methods that Modify Database**:
//    - The methods that modify or update the database should be annotated with `@Transactional` to ensure atomicity and consistency of the operations.
//    - Instruction: Add the `@Transactional` annotation above methods that interact with the database, especially those modifying data.

// 4. **Book Appointment Method**:
//    - Responsible for saving the new appointment to the database.
//    - If the save operation fails, it returns `0`; otherwise, it returns `1`.
//    - Instruction: Ensure that the method handles any exceptions and returns an appropriate result code.
    public int bookAppointment(Appointment appointment) {
        try {
            appointmentRepository.save(appointment);
            return 1; // Success
        } catch (Exception e) {
            // Log the error if needed
            return 0; // Error occurred
        }
    }

// 5. **Update Appointment Method**:
//    - This method is used to update an existing appointment based on its ID.
//    - It validates whether the patient ID matches, checks if the appointment is available for updating, and ensures that the doctor is available at the specified time.
//    - If the update is successful, it saves the appointment; otherwise, it returns an appropriate error message.
//    - Instruction: Ensure proper validation and error handling is included for appointment updates.
    @Transactional
        public void updateAppointment(Appointment appointment) {
        // 5.1. Check if the appointment exists by ID
        Optional<Appointment> existingAppointmentOpt = appointmentRepository.findById(appointment.getId());
        if (!existingAppointmentOpt.isPresent()) {
            throw new IllegalArgumentException("Appointment not found");
        }
        Appointment existingAppointment = existingAppointmentOpt.get();

        // 5.2. Validate patient ID matches the existing appointment
        if (!existingAppointment.getPatient().getId().equals(appointment.getPatient().getId())) {
            throw new IllegalArgumentException("Patient ID does not match the existing appointment");
        }

        // 5.3. Check if the doctor exists
        if (!doctorRepository.existsById(appointment.getDoctor().getId())) {
            throw new IllegalArgumentException("Doctor does not exist");
        }

        // 5.4. Validate doctor availability at the appointment time
        boolean isDoctorAvailable = checkDoctorAvailability(appointment.getDoctor().getId(), appointment.getDateTime(), appointment.getId());
        if (!isDoctorAvailable) {
            throw new IllegalArgumentException("Doctor is not available at the specified time");
        }

        // 5.5. If all validations pass, update the appointment details
        existingAppointment.setDateTime(appointment.getDateTime());
        // existingAppointment.setReason(appointment.getReason());
        existingAppointment.setDoctor(appointment.getDoctor());
        existingAppointment.setPatient(appointment.getPatient());
        existingAppointment.setStatus(appointment.getStatus()); // e.g., confirmed, canceled, etc.
        // existingAppointment.setLocation(appointment.getLocation()); // if applicable
        // existingAppointment.setNotes(appointment.getNotes()); // any additional notes
        // Add other fields as needed

        // 5.6. Save the updated appointment
        appointmentRepository.save(existingAppointment);
    }
// 6. **Cancel Appointment Method**:
//    - This method cancels an appointment by deleting it from the database.
//    - It ensures the patient who owns the appointment is trying to cancel it and handles possible errors.
//    - Instruction: Make sure that the method checks for the patient ID match before deleting the appointment.
    @Transactional
    public ResponseEntity<Map<String, String>> cancelAppointment(long id, String token) {
        Map<String, String> response = new HashMap<>();

        // Validate token for patient role
        if (!tokenService.validateToken(token, "patient")) {
            response.put("message", "Invalid or unauthorized token");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }

        // Find appointment by ID
        Optional<Appointment> appointmentOpt = appointmentRepository.findById(id);
        if (!appointmentOpt.isPresent()) {
            response.put("message", "Appointment not found");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        // Delete the appointment
        appointmentRepository.delete(appointmentOpt.get());
        response.put("message", "Appointment canceled successfully");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

// 7. **Get Appointments Method**:
//    - This method retrieves a list of appointments for a specific doctor on a particular day, optionally filtered by the patient's name.
//    - It uses `@Transactional` to ensure that database operations are consistent and handled in a single transaction.
//    - Instruction: Ensure the correct use of transaction boundaries, especially when querying the database for appointments.

    @Transactional(readOnly = true)
    public Map<String, Object> getAppointment(String pname, LocalDate date, String token) {
        Map<String, Object> response = new HashMap<>();

        // Validate token for doctor role
        if (!tokenService.validateToken(token, "doctor")) {
            response.put("error", "Invalid or unauthorized token");
            return response;
        }

        // Define start and end of the day for the date
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);

        // Fetch appointments for the doctor between start and end of the day
        List<Appointment> appointments = appointmentRepository.findByDoctorIdAndAppointmentTimeBetween(
                getDoctorIdFromToken(token), startOfDay, endOfDay);

        // If patient name filter is provided, filter the list
        if (pname != null && !pname.isEmpty()) {
            appointments = appointments.stream()
                    .filter(app -> app.getPatient() != null &&
                            app.getPatient().getName() != null &&
                            app.getPatient().getName().toLowerCase().contains(pname.toLowerCase()))
                    .toList();
        }

        response.put("appointments", appointments);
        return response;
    }

// 8. **Change Status Method**:
//    - This method updates the status of an appointment by changing its value in the database.
//    - It should be annotated with `@Transactional` to ensure the operation is executed in a single transaction.
//    - Instruction: Add `@Transactional` before this method to ensure atomicity when updating appointment status.
    @Transactional
    public void changeStatus(Long appointmentId, String newStatus) {
        // Find the appointment by ID
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new IllegalArgumentException("Appointment not found"));

        // Update the status
        appointment.setStatus(newStatus);

        // Save the updated appointment
        appointmentRepository.save(appointment);
    }

 
// LM added - not explicitly mentioned in the instructions
//  Helper method to check doctor's availability excluding current appointment
    private boolean checkDoctorAvailability(Long doctorId, LocalDateTime dateTime, Long appointmentId) {
            List<Appointment> conflicts = appointmentRepository.findConflictingAppointments(doctorId, dateTime, appointmentId);
        return conflicts.isEmpty();  // true if no conflicts, false if doctor is booked
    }

// Helper method to extract doctor ID from token (implementation depends on your tokenService)
    private Long getDoctorIdFromToken(String token) {
        // Example: parse token to get doctor ID
        return tokenService.getUserIdFromToken(token);
    }
// end LM added

}
