package com.project.back_end.controllers;

import com.project.back_end.models.Appointment;
import com.project.back_end.services.AppointmentService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import com.project.back_end.models.Admin;
import com.project.back_end.services.Service;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

// @RequestMapping("${api.path}appointments")
@RestController
@RequestMapping("/appointments")
public class AppointmentController {

// 1. Set Up the Controller Class:
//    - Annotate the class with `@RestController` to define it as a REST API controller.
//    - Use `@RequestMapping("/appointments")` to set a base path for all appointment-related endpoints.
//    - This centralizes all routes that deal with booking, updating, retrieving, and canceling appointments.


// 2. Autowire Dependencies:
//    - Inject `AppointmentService` for handling the business logic specific to appointments.
//    - Inject the general `Service` class, which provides shared functionality like token validation and appointment checks.
 private final AppointmentService appointmentService;
    private final Service service;  // Service for validation logic

    // @Autowired - commented out as not necessary
    public AppointmentController(AppointmentService appointmentService, Service service) {
        this.appointmentService = appointmentService;
        this.service = service;
    }


// 3. Define the `getAppointments` Method:
//    - Handles HTTP GET requests to fetch appointments based on date and patient name.
//    - Takes the appointment date, patient name, and token as path variables.
//    - First validates the token for role `"doctor"` using the `Service`.
//    - If the token is valid, returns appointments for the given patient on the specified date.
//    - If the token is invalid or expired, responds with the appropriate message and status code.

    @GetMapping("/{date}/{patientName}/{token}")
    public ResponseEntity<Map<String, Object>> getAppointments(
        @PathVariable String date,
        @PathVariable String patientName,
        @PathVariable String token) {

    Map<String, Object> response = new HashMap<>();

    // Validate token for role "doctor"
    ResponseEntity<Map<String, String>> validationResponse = service.validateToken(token, "doctor");

    if (validationResponse.getStatusCode() != HttpStatus.OK || 
        (validationResponse.getBody() != null && !validationResponse.getBody().isEmpty())) {
        // Token invalid or expired
        response.put("message", "Invalid or expired token.");
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

    try {
        // Parse date string to LocalDate
        LocalDate appointmentDate = LocalDate.parse(date);

        // Fetch appointments for patient on the given date
        Map<String, Object> appointmentMap = appointmentService.getAppointment(patientName, appointmentDate, token);

        @SuppressWarnings("unchecked")
        List<Appointment> appointments = (List<Appointment>) appointmentMap.get("appointments");

        response.put("appointments", appointments);
        return new ResponseEntity<>(response, HttpStatus.OK);

    } catch (DateTimeParseException e) {
        response.put("message", "Invalid date format. Please use YYYY-MM-DD.");
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    } catch (Exception e) {
        response.put("message", "An error occurred while fetching appointments.");
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}    

    /* 
    // LM previous verstion of the getAppointments method
    @GetMapping("/{date}/{patientName}/{token}")
    public ResponseEntity<?> getAppointments(@PathVariable String date,
                                             @PathVariable String patientName,
                                             @PathVariable String token) {
        // Validate token for "doctor" role
        ResponseEntity<Map<String, String>> validationResponse = service.validateToken(token, "doctor");
        if (validationResponse != null) {
            // Token invalid or unauthorized
            return validationResponse;
        }

        // Token valid, fetch appointments
        Map<String, Object> appointments = appointmentService.getAppointment(date, patientName, token);

        return new ResponseEntity<>(appointments, HttpStatus.OK);
    }
    // end LM test
    */

// 4. Define the `bookAppointment` Method:
//    - Handles HTTP POST requests to create a new appointment.
//    - Accepts a validated `Appointment` object in the request body and a token as a path variable.
//    - Validates the token for the `"patient"` role.
//    - Uses service logic to validate the appointment data (e.g., check for doctor availability and time conflicts).
//    - Returns success if booked, or appropriate error messages if the doctor ID is invalid or the slot is already taken.
    @PostMapping("/{token}")
    public ResponseEntity<Map<String, String>> bookAppointment(@PathVariable String token,
                                                               @RequestBody Appointment appointment) {
        // Validate token for patient role
        ResponseEntity<Map<String, String>> tokenValidation = service.validateToken(token, "patient");
        if (tokenValidation != null) {
            return tokenValidation;  // Return error if token invalid
        }

        // Validate appointment details
        int validationStatus = service.validateAppointment(appointment);
        if (validationStatus != 1) {
            String message = (validationStatus == 0) ? "Appointment time unavailable" : "Doctor does not exist";
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", message));
        }

        // Book appointment
        int bookingResult = appointmentService.bookAppointment(appointment);
        if (bookingResult == 1) {
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", "Appointment booked successfully"));
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "Error booking appointment"));
        }
    }


// 5. Define the `updateAppointment` Method:
//    - Handles HTTP PUT requests to modify an existing appointment.
//    - Accepts a validated `Appointment` object and a token as input.
//    - Validates the token for `"patient"` role.
//    - Delegates the update logic to the `AppointmentService`.
//    - Returns an appropriate success or failure response based on the update result.
    @PutMapping("/{token}")
    public ResponseEntity<Map<String, String>> updateAppointment(
        @RequestBody Appointment appointment,
        @PathVariable String token) {

        Map<String, String> response = new HashMap<>();

        // Validate token for "patient" role
        ResponseEntity<Map<String, String>> validationResponse = service.validateToken(token, "patient");

        if (validationResponse.getStatusCode() != HttpStatus.OK ||
            (validationResponse.getBody() != null && !validationResponse.getBody().isEmpty())) {
            response.put("message", "Invalid or expired token.");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }

        // Delegate update to AppointmentService
        boolean isUpdated = appointmentService.updateAppointment(appointment);

        if (isUpdated) {
            response.put("message", "Appointment updated successfully.");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            response.put("message", "Failed to update appointment. Please check the details and try again.");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }


/*
    // LM test - previous version
    @PutMapping("/{token}")
    public ResponseEntity<Map<String, String>> updateAppointment(@PathVariable String token,
                                                                 @RequestBody Appointment appointment) {
        // Validate token for patient role
        ResponseEntity<Map<String, String>> tokenValidation = service.validateToken(token, "patient");
        if (tokenValidation != null) {
            return tokenValidation;  // Return error if token invalid
        }

        // Update appointment and return result
        ResponseEntity<Map<String, String>> updateResponse = appointmentService.updateAppointment(appointment);
        return updateResponse;
    }
    // end of LM test
    */

// 6. Define the `cancelAppointment` Method:
//    - Handles HTTP DELETE requests to cancel a specific appointment.
//    - Accepts the appointment ID and a token as path variables.
//    - Validates the token for `"patient"` role to ensure the user is authorized to cancel the appointment.
//    - Calls `AppointmentService` to handle the cancellation process and returns the result.
    @DeleteMapping("/{id}/{token}")
    public ResponseEntity<Map<String, String>> cancelAppointment(@PathVariable Long id,
                                                                 @PathVariable String token) {
        // Validate token for patient role
        ResponseEntity<Map<String, String>> tokenValidation = service.validateToken(token, "patient");
        if (tokenValidation != null) {
            return tokenValidation;  // Return error if token invalid
        }

        // Cancel the appointment and return result
        ResponseEntity<Map<String, String>> cancelResponse = appointmentService.cancelAppointment(id, token);
        return cancelResponse;
    }

}
