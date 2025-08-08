package com.project.back_end.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;

import com.project.back_end.DTO.Login;
import com.project.back_end.models.Appointment;
import com.project.back_end.models.Doctor;
import com.project.back_end.repo.AppointmentRepository;
import com.project.back_end.repo.DoctorRepository;
import com.project.back_end.repo.PatientRepository;

// 1. **Add @Service Annotation**:
//    - This class should be annotated with `@Service` to indicate that it is a service layer class.
//    - The `@Service` annotation marks this class as a Spring-managed bean for business logic.
//    - Instruction: Add `@Service` above the class declaration.


@Service
public class DoctorService {


// 2. **Constructor Injection for Dependencies**:
//    - The `DoctorService` class depends on `DoctorRepository`, `AppointmentRepository`, and `TokenService`.
//    - These dependencies should be injected via the constructor for proper dependency management.
//    - Instruction: Ensure constructor injection is used for injecting dependencies into the service.
    private final DoctorRepository doctorRepository;
    private final AppointmentRepository appointmentRepository;
    private final TokenService tokenService;

    // Constructor injection for dependencies
    public DoctorService(DoctorRepository doctorRepository,
                         AppointmentRepository appointmentRepository,
                         TokenService tokenService) {
        this.doctorRepository = doctorRepository;
        this.appointmentRepository = appointmentRepository;
        this.tokenService = tokenService;
    }


// 3. **Add @Transactional Annotation for Methods that Modify or Fetch Database Data**:
//    - Methods like `getDoctorAvailability`, `getDoctors`, `findDoctorByName`, `filterDoctorsBy*` should be annotated with `@Transactional`.
//    - The `@Transactional` annotation ensures that database operations are consistent and wrapped in a single transaction.
//    - Instruction: Add the `@Transactional` annotation above the methods that perform database operations or queries.

// 4. **getDoctorAvailability Method**:
//    - Retrieves the available time slots for a specific doctor on a particular date and filters out already booked slots.
//    - The method fetches all appointments for the doctor on the given date and calculates the availability by comparing against booked slots.
//    - Instruction: Ensure that the time slots are properly formatted and the available slots are correctly filtered.
    
    public Map<String, Object> getDoctorAvailability(Long doctorId, LocalDate date) {
        Map<String, Object> response = new HashMap<>();

        try {
            // Fetch all appointments for the doctor on the given date
            List<Appointment> appointments = appointmentRepository.findByDoctorIdAndAppointmentTimeBetween(
                doctorId,
                date.atStartOfDay(),
                date.plusDays(1).atStartOfDay()
            );

            // Prepare a list or other structure representing available time slots
            List<String> availableSlots = calculateAvailableSlots(appointments);

            response.put("availableSlots", availableSlots);
            response.put("status", "success");
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Failed to fetch availability: " + e.getMessage());
        }

        return response;
    }

    // Helper method to calculate available time slots based on existing appointments
    private List<String> calculateAvailableSlots(List<Appointment> appointments) {
        // Example logic: define all possible slots and remove those booked
        List<String> allSlots = Arrays.asList("09:00 AM", "10:00 AM", "11:00 AM", "01:00 PM", "02:00 PM", "03:00 PM");
        Set<String> bookedSlots = appointments.stream()
            .map(appointment -> appointment.getAppointmentTime().toLocalTime().toString()) // adjust format as needed
            .collect(Collectors.toSet());

        return allSlots.stream()
            .filter(slot -> !bookedSlots.contains(slot))
            .collect(Collectors.toList());
    }


    /*
    // LM test: first version returning a List instead of a Map
    public List<String> getDoctorAvailability(Long doctorId, LocalDate date) {
        // Define all possible time slots (example: every hour from 9 AM to 5 PM)
        List<String> allSlots = new ArrayList<>();
        for (int hour = 9; hour <= 17; hour++) {
            allSlots.add(String.format("%02d:00", hour));
        }

        // Define start and end of the day for querying appointments
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);

        // Fetch all appointments for the doctor on the specified date
        List<Appointment> bookedAppointments = appointmentRepository.findByDoctorIdAndAppointmentTimeBetween(
                doctorId, startOfDay, endOfDay);

        // Extract booked time slots from appointments
        List<String> bookedSlots = new ArrayList<>();
        for (Appointment appointment : bookedAppointments) {
            bookedSlots.add(appointment.getDateTime().toLocalTime().toString().substring(0, 5)); // e.g., "09:00"
        }

        // Filter out booked slots from all slots
        List<String> availableSlots = new ArrayList<>();
        for (String slot : allSlots) {
            if (!bookedSlots.contains(slot)) {
                availableSlots.add(slot);
            }
        }

        return availableSlots;
    }
    // end of LM test
    */

// 5. **saveDoctor Method**:
//    - Used to save a new doctor record in the database after checking if a doctor with the same email already exists.
//    - If a doctor with the same email is found, it returns `-1` to indicate conflict; `1` for success, and `0` for internal errors.
//    - Instruction: Ensure that the method correctly handles conflicts and exceptions when saving a doctor.

    // Method to save a new doctor
    public int saveDoctor(Doctor doctor) {
        try {
            // Check if doctor already exists by email
            if (doctorRepository.findByEmail(doctor.getEmail()) != null) {
                return -1; // Doctor already exists
            }
            doctorRepository.save(doctor);
            return 1; // Success
        } catch (Exception e) {
            // Log exception if needed
            return 0; // Internal error
        }
    }

// 6. **updateDoctor Method**:
//    - Updates an existing doctor's details in the database. If the doctor doesn't exist, it returns `-1`.
//    - Instruction: Make sure that the doctor exists before attempting to save the updated record and handle any errors properly.
    // Method to update an existing doctor
    public int updateDoctor(Doctor doctor) {
        try {
            // Check if doctor exists by ID
            if (!doctorRepository.existsById(doctor.getId())) {
                return -1; // Doctor not found
            }
            doctorRepository.save(doctor);
            return 1; // Success
        } catch (Exception e) {
            // Log exception if needed
            return 0; // Internal error
        }
    }

// 7. **getDoctors Method**:
//    - Fetches all doctors from the database. It is marked with `@Transactional` to ensure that the collection is properly loaded.
//    - Instruction: Ensure that the collection is eagerly loaded, especially if dealing with lazy-loaded relationships (e.g., available times). 
    // Method to retrieve all doctors
    @Transactional
    public List<Doctor> getDoctors() {
        return doctorRepository.findAll();
    }
 
// 8. **deleteDoctor Method**:
//    - Deletes a doctor from the system along with all appointments associated with that doctor.
//    - It first checks if the doctor exists. If not, it returns `-1`; otherwise, it deletes the doctor and their appointments.
//    - Instruction: Ensure the doctor and their appointments are deleted properly, with error handling for internal issues.
    // Method to delete a doctor by ID
    @Transactional
    public int deleteDoctor(long id) {
        try {
            Optional<Doctor> doctorOpt = doctorRepository.findById(id);
            if (!doctorOpt.isPresent()) {
                return -1; // Doctor not found
            }

            // Delete all appointments associated with the doctor
            appointmentRepository.deleteAllByDoctorId(id);

            // Delete the doctor
            doctorRepository.deleteById(id);

            return 1; // Success
        } catch (Exception e) {
            // Log exception if needed
            return 0; // Internal error
        }
    }   

// LM test - modified as below
/*
export async function deleteDoctor(doctorId, token) {
        const response = await fetch(`/api/doctors/${doctorId}`, {
            method: "DELETE",
            headers: {
            "Authorization": `Bearer ${token}`
            }
        });

        if (!response.ok) {
            throw new Error("Delete failed");
        }

        return response.json();
    }  

    export async function deleteDoctor(doctorId, token) {
    const response = await fetch(`/api/doctors/${doctorId}/${token}`, {
        method: "DELETE"
    });

    if (!response.ok) {
        throw new Error("Delete failed");
    }

    return response.text(); // Assuming the API returns a success message string
   }
// end LM test
*/

// 9. **validateDoctor Method**:
//    - Validates a doctor's login by checking if the email and password match an existing doctor record.
//    - It generates a token for the doctor if the login is successful, otherwise returns an error message.
//    - Instruction: Make sure to handle invalid login attempts and password mismatches properly with error responses.
// Method to validate doctor login credentials
    public ResponseEntity<Map<String, String>> validateDoctor(Login login) {
        Map<String, String> response = new HashMap<>();
        
        System.out.println("testLM XXXXXXX entered in validateDoctor inside DoctorService.java");
        System.out.println("testLM XXXXXXX login: " + login);
        System.out.println("testLM XXXXXXX response: " + response);
        System.out.println("testLM XXXXXXX Login email: " + login.getEmail());
        Doctor doctor = doctorRepository.findByEmail(login.getEmail());
        if (doctor == null) {
            response.put("message", "Doctor not found");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        // Assuming passwords are stored hashed; replace with your password check logic
        if (!doctor.getPassword().equals(login.getPassword())) {
            response.put("message", "Invalid password");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }

        // Generate token if valid
        String token = tokenService.generateToken(doctor.getEmail());

        response.put("token", token);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

// 10. **findDoctorByName Method**:
//    - Finds doctors based on partial name matching and returns the list of doctors with their available times.
//    - This method is annotated with `@Transactional` to ensure that the database query and data retrieval are properly managed within a transaction.
//    - Instruction: Ensure that available times are eagerly loaded for the doctors.
    // Method to find doctors by partial name match
    public Map<String, Object> findDoctorByName(String name) {
        Map<String, Object> response = new HashMap<>();

        List<Doctor> doctors = doctorRepository.findByNameLike(name);
        response.put("doctors", doctors);

        return response;
    }

// 11. **filterDoctorsByNamespecialtyandTime Method**:
//    - Filters doctors based on their name, specialty, and availability during a specific time (AM/PM).
//    - The method fetches doctors matching the name and specialty criteria, then filters them based on their availability during the specified time period.
//    - Instruction: Ensure proper filtering based on both the name and specialty as well as the specified time period.
    // Filter doctors by name, specialty, and availability during AM/PM
    public Map<String, Object> filterDoctorsByNameSpecialtyAndTime(String name, String specialty, String amOrPm) {
        Map<String, Object> response = new HashMap<>();

        // Step 1: Filter by name and specialty (case-insensitive)
        List<Doctor> filteredDoctors = doctorRepository
                .findByNameContainingIgnoreCaseAndSpecialtyIgnoreCase(name, specialty);

        // Step 2: Further filter doctors by availability (AM or PM)
        List<Doctor> availableDoctors = filteredDoctors.stream()
                .filter(doc -> isAvailableAtTime(doc, amOrPm))
                .collect(Collectors.toList());

        response.put("doctors", availableDoctors);
        return response;
    }
// 12. **filterDoctorByTime Method**:
//    - Filters a list of doctors based on whether their available times match the specified time period (AM/PM).
//    - This method processes a list of doctors and their available times to return those that fit the time criteria.
//    - Instruction: Ensure that the time filtering logic correctly handles both AM and PM time slots and edge cases.
    // Filters doctors by their available times matching AM or PM
    private List<Doctor> filterDoctorByTime(List<Doctor> doctors, String amOrPm) {
        if (amOrPm == null || (!amOrPm.equalsIgnoreCase("AM") && !amOrPm.equalsIgnoreCase("PM"))) {
            // If invalid time period specified, return empty list or original list as per requirements
            return List.of(); // returning empty list here
        }

        String timePeriod = amOrPm.toUpperCase();

        return doctors.stream()
                .filter(doctor -> doctor.getAvailableTimes() != null &&
                        doctor.getAvailableTimes().stream()
                                .anyMatch(time -> time.equalsIgnoreCase(timePeriod)))
                .collect(Collectors.toList());
    }

// 13. **filterDoctorByNameAndTime Method**:
//    - Filters doctors based on their name and the specified time period (AM/PM).
//    - Fetches doctors based on partial name matching and filters the results to include only those available during the specified time period.
//    - Instruction: Ensure that the method correctly filters doctors based on the given name and time of day (AM/PM).
    // Filter doctors by name and availability during AM/PM
    public Map<String, Object> filterDoctorByNameAndTime(String name, String amOrPm) {
        Map<String, Object> response = new HashMap<>();

        // Step 1: Filter by name (case-insensitive)
        List<Doctor> filteredDoctors = doctorRepository.findByNameContainingIgnoreCase(name);

        // Step 2: Further filter doctors by availability (AM or PM)
        List<Doctor> availableDoctors = filteredDoctors.stream()
                .filter(doc -> isAvailableAtTime(doc, amOrPm))
                .collect(Collectors.toList());

        response.put("doctors", availableDoctors);
        return response;
    }

    // Helper method to check if doctor is available during AM or PM
    private boolean isAvailableAtTime(Doctor doctor, String amOrPm) {
        // Assuming Doctor has a method or field that indicates availability time slots
        // Example: doctor.getAvailableTimes() returns List<String> like ["AM", "PM"]
        if (doctor.getAvailableTimes() == null) {
            return false;
        }
        return doctor.getAvailableTimes().stream()
                .anyMatch(time -> time.equalsIgnoreCase(amOrPm));
    }
// 14. **filterDoctorByNameAndSpecialty Method**:
//    - Filters doctors by name and specialty.
//    - It ensures that the resulting list of doctors matches both the name (case-insensitive) and the specified specialty.
//    - Instruction: Ensure that both name and specialty are considered when filtering doctors.
   // Filter doctors by name and specialty
    public Map<String, Object> filterDoctorByNameAndSpecialty(String name, String specialty) {
        Map<String, Object> response = new HashMap<>();

        List<Doctor> doctors = doctorRepository.findByNameContainingIgnoreCaseAndSpecialtyIgnoreCase(name, specialty);

        response.put("doctors", doctors);
        return response;
    }


// 15. **filterDoctorByTimeAndSpecialty Method**:
//    - Filters doctors based on their specialty and availability during a specific time period (AM/PM).
//    - Fetches doctors based on the specified specialty and filters them based on their available time slots for AM/PM.
//    - Instruction: Ensure the time filtering is accurately applied based on the given specialty and time period (AM/PM).
    // Filter doctors by specialty and availability during AM/PM
    public Map<String, Object> filterDoctorByTimeAndSpecialty(String specialty, String amOrPm) {
        Map<String, Object> response = new HashMap<>();

        // Step 1: Filter by specialty (case-insensitive)
        List<Doctor> doctorsBySpecialty = doctorRepository.findBySpecialtyIgnoreCase(specialty);

        // Step 2: Filter the doctors by availability time (AM/PM)
        List<Doctor> filteredDoctors = filterDoctorByTime(doctorsBySpecialty, amOrPm);

        response.put("doctors", filteredDoctors);
        return response;
    }

    /* LM: commenting out because the method is already defined above
    // Helper method to filter doctors by AM/PM availability
    private List<Doctor> filterDoctorByTime(List<Doctor> doctors, String amOrPm) {
        if (amOrPm == null || (!amOrPm.equalsIgnoreCase("AM") && !amOrPm.equalsIgnoreCase("PM"))) {
            return List.of(); // return empty list if invalid time period
        }

        String timePeriod = amOrPm.toUpperCase();

        return doctors.stream()
                .filter(doc -> doc.getAvailableTimes() != null &&
                        doc.getAvailableTimes().stream()
                                .anyMatch(time -> time.equalsIgnoreCase(timePeriod)))
                .collect(Collectors.toList());
    }
    */

// 16. **filterDoctorBySpecialty Method**:
//    - Filters doctors based on their specialty.
//    - This method fetches all doctors matching the specified specialty and returns them.
//    - Instruction: Make sure the filtering logic works for case-insensitive specialty matching.
    // Method to filter doctors by specialty
    public Map<String, Object> filterDoctorBySpecialty(String specialty) {
        Map<String, Object> response = new HashMap<>();

        List<Doctor> doctors = doctorRepository.findBySpecialtyIgnoreCase(specialty);

        response.put("doctors", doctors);
        return response;
    }

// 17. **filterDoctorsByTime Method**:
//    - Filters all doctors based on their availability during a specific time period (AM/PM).
//    - The method checks all doctors' available times and returns those available during the specified time period.
//    - Instruction: Ensure proper filtering logic to handle AM/PM time periods.
    // Method to filter doctors by their availability during AM/PM
    public Map<String, Object> filterDoctorsByTime(String amOrPm) {
        Map<String, Object> response = new HashMap<>();

        // Fetch all doctors
        List<Doctor> allDoctors = doctorRepository.findAll();

        // Filter doctors by available time (AM/PM)
        List<Doctor> filteredDoctors = filterDoctorByTime(allDoctors, amOrPm);

        response.put("doctors", filteredDoctors);
        return response;
    }

    /* LM: commenting out because the method is already defined above
    // Private helper method to filter a list of doctors by their available times (AM/PM)
    private List<Doctor> filterDoctorByTime(List<Doctor> doctors, String amOrPm) {
        if (amOrPm == null || (!amOrPm.equalsIgnoreCase("AM") && !amOrPm.equalsIgnoreCase("PM"))) {
            return List.of(); // Return empty list if invalid input
        }

        String timePeriod = amOrPm.toUpperCase();

        return doctors.stream()
                .filter(doctor -> doctor.getAvailableTimes() != null &&
                        doctor.getAvailableTimes().stream()
                                .anyMatch(time -> time.equalsIgnoreCase(timePeriod)))
                .collect(Collectors.toList());
    }
    */
}
   

