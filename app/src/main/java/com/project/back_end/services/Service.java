package com.project.back_end.services;

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
import com.project.back_end.models.Admin;
import com.project.back_end.models.Appointment;
import com.project.back_end.models.Doctor;
import com.project.back_end.models.Patient;
import com.project.back_end.repo.AdminRepository;
import com.project.back_end.repo.AppointmentRepository;
import com.project.back_end.repo.DoctorRepository;
import com.project.back_end.repo.PatientRepository;
import com.project.back_end.services.TokenService;
import com.project.back_end.services.DoctorService;


@org.springframework.stereotype.Service
public class Service {
// 1. **@Service Annotation**
// The @Service annotation marks this class as a service component in Spring. This allows Spring to automatically detect it through component scanning
// and manage its lifecycle, enabling it to be injected into controllers or other services using @Autowired or constructor injection.

// 2. **Constructor Injection for Dependencies**
// The constructor injects all required dependencies (TokenService, Repositories, and other Services). This approach promotes loose coupling, improves testability,
// and ensures that all required dependencies are provided at object creation time.
    private final TokenService tokenService;
    private final AdminRepository adminRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final DoctorService doctorService;
    private final PatientService patientService;

    public Service(TokenService tokenService, AdminRepository adminRepository, DoctorRepository doctorRepository,
                   PatientRepository patientRepository, DoctorService doctorService, PatientService patientService) {
        this.tokenService = tokenService;
        this.adminRepository = adminRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
        this.doctorService = doctorService;
        this.patientService = patientService;
    }
// 3. **validateToken Method**
// This method checks if the provided JWT token is valid for a specific user. It uses the TokenService to perform the validation.
// If the token is invalid or expired, it returns a 401 Unauthorized response with an appropriate error message. This ensures security by preventing
// unauthorized access to protected resources.
    public ResponseEntity<Map<String, String>> validateToken(String token, String user) {
        Map<String, String> response = new HashMap<>();

        boolean isValid = tokenService.validateToken(token, user);

        if (!isValid) {
            response.put("message", "Invalid or expired token");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);  // 401 Unauthorized
        }

        // If valid, return null or an empty response (depending on your usage)
        return null;
    }

// 4. **validateAdmin Method**
// This method validates the login credentials for an admin user.
// - It first searches the admin repository using the provided username.
// - If an admin is found, it checks if the password matches.
// - If the password is correct, it generates and returns a JWT token (using the admin’s username) with a 200 OK status.
// - If the password is incorrect, it returns a 401 Unauthorized status with an error message.
// - If no admin is found, it also returns a 401 Unauthorized.
// - If any unexpected error occurs during the process, a 500 Internal Server Error response is returned.
// This method ensures that only valid admin users can access secured parts of the system.
    public ResponseEntity<Map<String, String>> validateAdmin(Admin receivedAdmin) {
        Map<String, String> response = new HashMap<>();

        // Find admin by username
        Admin existingAdmin = adminRepository.findByUsername(receivedAdmin.getUsername());
        if (existingAdmin == null) {
            response.put("message", "Admin not found");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }

        // Compare passwords (assuming plain text; replace with hashed password check if needed)
        if (!existingAdmin.getPassword().equals(receivedAdmin.getPassword())) {
            response.put("message", "Invalid password");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }

        // Generate token
        String token = tokenService.generateToken(existingAdmin.getUsername());

        response.put("token", token);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
// 5. **filterDoctor Method**
// This method provides filtering functionality for doctors based on name, specialty, and available time slots.
// - It supports various combinations of the three filters.
// - If none of the filters are provided, it returns all available doctors.
// This flexible filtering mechanism allows the frontend or consumers of the API to search and narrow down doctors based on user criteria.
    public Map<String, Object> filterDoctor(String name, String specialty, String time) {
        // Delegate filtering to doctorService method that handles all three criteria
        return doctorService.filterDoctorsByNameSpecialtyandTime(name, specialty, time);
    }
// 6. **validateAppointment Method**
// This method validates if the requested appointment time for a doctor is available.
// - It first checks if the doctor exists in the repository.
// - Then, it retrieves the list of available time slots for the doctor on the specified date.
// - It compares the requested appointment time with the start times of these slots.
// - If a match is found, it returns 1 (valid appointment time).
// - If no matching time slot is found, it returns 0 (invalid).
// - If the doctor doesn’t exist, it returns -1.
// This logic prevents overlapping or invalid appointment bookings.
    public int validateAppointment(Appointment appointment) {
        // Find the doctor by ID
        Optional<Doctor> doctorOpt = doctorRepository.findById(appointment.getDoctor().getId());
        if (!doctorOpt.isPresent()) {
            return -1;  // Doctor doesn't exist
        }

        Doctor doctor = doctorOpt.get();

        // Get available time slots for the doctor on the appointment date
        List<String> availableSlots = doctorService.getDoctorAvailability(doctor.getId(), appointment.getDateTime().toLocalDate());

        // Check if the appointment time is in the available slots
        String appointmentTime = appointment.getDateTime().toLocalTime().toString().substring(0, 5); // e.g., "09:00"
        if (availableSlots.contains(appointmentTime)) {
            return 1;  // Appointment time is valid
        } else {
            return 0;  // Time is unavailable
        }
    }
// 7. **validatePatient Method**
// This method checks whether a patient with the same email or phone number already exists in the system.
// - If a match is found, it returns false (indicating the patient is not valid for new registration).
// - If no match is found, it returns true.
// This helps enforce uniqueness constraints on patient records and prevent duplicate entries.
    public boolean validatePatient(Patient patient) {
        // Use repository method to find patient by email or phone
        Patient existingPatient = patientRepository.findByEmailOrPhone(patient.getEmail(), patient.getPhone());

        // Return false if patient exists, true otherwise
        return existingPatient == null;
    }
// 8. **validatePatientLogin Method**
// This method handles login validation for patient users.
// - It looks up the patient by email.
// - If found, it checks whether the provided password matches the stored one.
// - On successful validation, it generates a JWT token and returns it with a 200 OK status.
// - If the password is incorrect or the patient doesn't exist, it returns a 401 Unauthorized with a relevant error.
// - If an exception occurs, it returns a 500 Internal Server Error.
// This method ensures only legitimate patients can log in and access their data securely.
    public ResponseEntity<Map<String, String>> validatePatientLogin(Login login) {
        Map<String, String> response = new HashMap<>();

        // Find patient by email
        Patient patient = patientRepository.findByEmail(login.getEmail());
        if (patient == null) {
            response.put("message", "Patient not found");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        // Check password (assuming plain text; replace with hashed password check if applicable)
        if (!patient.getPassword().equals(login.getPassword())) {
            response.put("message", "Invalid password");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }

        // Generate token if valid
        String token = tokenService.generateToken(patient.getEmail());
        response.put("token", token);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
// 9. **filterPatient Method**
// This method filters a patient's appointment history based on condition and doctor name.
// - It extracts the email from the JWT token to identify the patient.
// - Depending on which filters (condition, doctor name) are provided, it delegates the filtering logic to PatientService.
// - If no filters are provided, it retrieves all appointments for the patient.
// This flexible method supports patient-specific querying and enhances user experience on the client side.

// Assuming these methods exist in this service:
    public ResponseEntity<Map<String, Object>> filterByCondition(String condition, Long patientId) {
        // Implementation here
        return null;
    }

    public ResponseEntity<Map<String, Object>> filterByDoctor(String name, Long patientId) {
        // Implementation here
        return null;
    }

    public ResponseEntity<Map<String, Object>> filterByDoctorAndCondition(String condition, String name, Long patientId) {
        // Implementation here
        return null;
    }

    public ResponseEntity<Map<String, Object>> filterPatient(String condition, String name, String token) {
        Map<String, Object> response = new HashMap<>();

        // Extract patient email from token
        String email = tokenService.extractEmail(token);
        if (email == null || email.isEmpty()) {
            response.put("message", "Invalid token");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }

        // Retrieve patient ID by email (assuming patientRepository is available)
        Long patientId = getPatientIdByEmail(email);
        if (patientId == null) {
            response.put("message", "Patient not found");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        // Apply filters based on input parameters
        if ((condition == null || condition.isEmpty()) && (name == null || name.isEmpty())) {
            // No filters applied, return all appointments
            return getAllAppointmentsForPatient(patientId);
        } else if (condition != null && !condition.isEmpty() && (name == null || name.isEmpty())) {
            return filterByCondition(condition, patientId);
        } else if ((condition == null || condition.isEmpty()) && name != null && !name.isEmpty()) {
            return filterByDoctor(name, patientId);
        } else {
            return filterByDoctorAndCondition(condition, name, patientId);
        }
    }

    // Placeholder method to get patient ID by email
    private Long getPatientIdByEmail(String email) {
        // Implement retrieval of patient ID from email using patientRepository
        return null;
    }

    // Placeholder method to get all appointments for a patient
    private ResponseEntity<Map<String, Object>> getAllAppointmentsForPatient(Long patientId) {
        // Implement retrieval of all appointments for patient
        return null;
    }

    /*
    // LM test - second test for the point above
        // Main filterPatient method called externally
    public ResponseEntity<Map<String, Object>> filterPatient(String condition, String name, String token) {
        Map<String, Object> response = new HashMap<>();

        // Extract email from token to identify patient
        String email = tokenService.extractEmail(token);
        if (email == null || email.isEmpty()) {
            response.put("message", "Invalid or missing token");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }

        // Retrieve patient by email
        Patient patient = patientRepository.findByEmail(email);
        if (patient == null) {
            response.put("message", "Patient not found");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        Long patientId = patient.getId();

        // Determine which filter to apply based on inputs
        if ((condition == null || condition.isEmpty()) && (name == null || name.isEmpty())) {
            // No filters: return all appointments for patient
            return getAllAppointments(patientId);
        } else if (condition != null && !condition.isEmpty() && (name == null || name.isEmpty())) {
            // Filter by condition only
            return filterByCondition(condition, patientId);
        } else if ((condition == null || condition.isEmpty()) && name != null && !name.isEmpty()) {
            // Filter by doctor name only
            return filterByDoctor(name, patientId);
        } else {
            // Filter by both condition and doctor name
            return filterByDoctorAndCondition(condition, name, patientId);
        }
    }

    // Example implementations of the filtering methods (you should implement these properly)
    private ResponseEntity<Map<String, Object>> getAllAppointments(Long patientId) {
        // Retrieve all appointments for the patient and return
        // Implementation depends on your appointmentRepository
        return null;  // Replace with actual implementation
    }

    private ResponseEntity<Map<String, Object>> filterByCondition(String condition, Long patientId) {
        // Filter appointments by condition (past/future) for the patient
        return null;  // Replace with actual implementation
    }

    private ResponseEntity<Map<String, Object>> filterByDoctor(String name, Long patientId) {
        // Filter appointments by doctor's name for the patient
        return null;  // Replace with actual implementation
    }

    private ResponseEntity<Map<String, Object>> filterByDoctorAndCondition(String condition, String name, Long patientId) {
        // Filter appointments by both doctor name and condition for the patient
        return null;  // Replace with actual implementation
    }
    // end LM test - second test for the point above
     */

}
