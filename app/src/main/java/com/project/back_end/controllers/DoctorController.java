package com.project.back_end.controllers;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import com.project.back_end.repo.DoctorRepository;
import com.project.back_end.DTO.Login;
import com.project.back_end.models.Doctor;
import com.project.back_end.services.DoctorService;
import com.project.back_end.services.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${api.path}doctor")
public class DoctorController {

 

// 1. Set Up the Controller Class:
//    - Annotate the class with `@RestController` to define it as a REST controller that serves JSON responses.
//    - Use `@RequestMapping("${api.path}doctor")` to prefix all endpoints with a configurable API path followed by "doctor".
//    - This class manages doctor-related functionalities such as registration, login, updates, and availability.


// 2. Autowire Dependencies:
//    - Inject `DoctorService` for handling the core logic related to doctors (e.g., CRUD operations, authentication).
//    - Inject the shared `Service` class for general-purpose features like token validation and filtering.
    private final DoctorService doctorService;
    private final Service service;  // For token validation and filtering


    @Autowired
    public DoctorController(DoctorService doctorService, Service service) {
        this.doctorService = doctorService;
        this.service = service;
    }


// 3. Define the `getDoctorAvailability` Method:
//    - Handles HTTP GET requests to check a specific doctor’s availability on a given date.
//    - Requires `user` type, `doctorId`, `date`, and `token` as path variables.
//    - First validates the token against the user type.
//    - If the token is invalid, returns an error response; otherwise, returns the availability status for the doctor.
    // Get Doctor Availability
    @GetMapping("/availability/{user}/{doctorId}/{date}/{token}")
    public ResponseEntity<Map<String, Object>> getDoctorAvailability(
            @PathVariable String user,
            @PathVariable Long doctorId,
            @PathVariable String date,
            @PathVariable String token) {

        // Validate token for the user role
        ResponseEntity<Map<String, String>> validationResponse = service.validateToken(token, user);
        if (validationResponse != null) {
            // Token invalid or unauthorized
            return new ResponseEntity<>(Map.of("error", "Invalid or unauthorized token"), HttpStatus.UNAUTHORIZED);
        }

        // Parse date string to LocalDate
        LocalDate localDate;
        try {
            localDate = LocalDate.parse(date);
        } catch (Exception e) {
            return new ResponseEntity<>(Map.of("error", "Invalid date format"), HttpStatus.BAD_REQUEST);
        }

        // Fetch doctor's availability
        Map<String, Object> availability = doctorService.getDoctorAvailability(doctorId, localDate);
        return new ResponseEntity<>(availability, HttpStatus.OK);
    }


// 4. Define the `getDoctor` Method:
//    - Handles HTTP GET requests to retrieve a list of all doctors.
//    - Returns the list within a response map under the key `"doctors"` with HTTP 200 OK status.
    // Get List of Doctors
    @GetMapping
    public ResponseEntity<Map<String, Object>> getDoctors() {
        Map<String, Object> doctors = doctorService.getDoctors();
        return new ResponseEntity<>(doctors, HttpStatus.OK);
    } 

// 5. Define the `saveDoctor` Method:
//    - Handles HTTP POST requests to register a new doctor.
//    - Accepts a validated `Doctor` object in the request body and a token for authorization.
//    - Validates the token for the `"admin"` role before proceeding.
//    - If the doctor already exists, returns a conflict response; otherwise, adds the doctor and returns a success message.

    // Add New Doctor
    @PostMapping("/{token}")
    public ResponseEntity<Map<String, String>> addNewDoctor(@RequestBody Doctor doctor,
                                                            @PathVariable String token) {
        // Validate token for admin role
        ResponseEntity<Map<String, String>> validationResponse = service.validateToken(token, "admin");
        if (validationResponse != null) {
            return validationResponse;  // Return error if token invalid or unauthorized
        }

        int result = doctorService.saveDoctor(doctor);

        switch (result) {
            case 1:
                return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", "Doctor added to db"));
            case -1:
                return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message", "Doctor already exists"));
            default:
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(Map.of("message", "Some internal error occurred"));
        }
    }

// 6. Define the `doctorLogin` Method:
//    - Handles HTTP POST requests for doctor login.
//    - Accepts a validated `Login` DTO containing credentials.
//    - Delegates authentication to the `DoctorService` and returns login status and token information.
    // Doctor Login
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> doctorLogin(@RequestBody Login login) {
        return doctorService.validateDoctor(login);
    }

// 7. Define the `updateDoctor` Method:
//    - Handles HTTP PUT requests to update an existing doctor's information.
//    - Accepts a validated `Doctor` object and a token for authorization.
//    - Token must belong to an `"admin"`.
//    - If the doctor exists, updates the record and returns success; otherwise, returns not found or error messages.
    // Update Doctor Details
    @PutMapping("/{token}")
    public ResponseEntity<Map<String, String>> updateDoctor(@RequestBody Doctor doctor,
                                                            @PathVariable String token) {
        // Validate token
        ResponseEntity<Map<String, String>> validationResponse = service.validateToken(token, "admin");
        if (validationResponse != null) {
            return validationResponse;  // Return error if token invalid or unauthorized
        }

        int result = doctorService.updateDoctor(doctor);

        switch (result) {
            case 1:
                return ResponseEntity.ok(Map.of("message", "Doctor updated"));
            case -1:
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Doctor not found"));
            default:
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(Map.of("message", "Some internal error occurred"));
        }
    }

// 8. Define the `deleteDoctor` Method:
//    - Handles HTTP DELETE requests to remove a doctor by ID.
//    - Requires both doctor ID and an admin token as path variables.
//    - If the doctor exists, deletes the record and returns a success message; otherwise, responds with a not found or error message.
    // Delete Doctor
    @DeleteMapping("/{id}/{token}")
    public ResponseEntity<Map<String, String>> deleteDoctor(@PathVariable Long id,
                                                            @PathVariable String token) {
        // Validate token for admin role
        ResponseEntity<Map<String, String>> validationResponse = service.validateToken(token, "admin");
        if (validationResponse != null) {
            return validationResponse;  // Return error if token invalid or unauthorized
        }

        int result = doctorService.deleteDoctor(id);

        switch (result) {
            case 1:
                return ResponseEntity.ok(Map.of("message", "Doctor deleted successfully"));
            case -1:
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Doctor not found with id"));
            default:
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(Map.of("message", "Some internal error occurred"));
        }
    }

    // 8. Filter Doctors
    @GetMapping("/filter/{name}/{time}/{speciality}")
    public ResponseEntity<Map<String, Object>> filterDoctors(@PathVariable String name,
                                                             @PathVariable String time,
                                                             @PathVariable String speciality) {
        Map<String, Object> filteredDoctors = service.filterDoctor(name, speciality, time);
        return ResponseEntity.ok(filteredDoctors);
    }

}

/*
// LM test with @Controller instead of @RestCentroller
    @GetMapping
    public Map<String, Object> listDoctor() {
        Map<String, Object> map = new HashMap<>();
        map.put("doctors",doctorRepository.findAll());
        return map;
    }

    @GetMapping("/{id}")
    public Map<String, Object> getDoctorbyId(@PathVariable Long id) {
        System.out.println("result: "); 
        System.out.println("result: "); 
        System.out.println("result: "); 
        Map<String, Object> map = new HashMap<>();
        Doctor result = doctorRepository.findByid(id);

        System.out.println("results: "+result);
        map.put("doctors", result);
        return map;
    }

    @GetMapping("/doctors")
    public String getDoctors(Model model) {
        List<Doctor> doctors = doctorRepository.findAll();
        model.addAttribute("doctors", doctors);
        return "doctors";
    }
// end LM test
*/