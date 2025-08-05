package com.project.back_end.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import com.project.back_end.models.Prescription;
import com.project.back_end.services.PrescriptionService;

import java.util.Map;

import com.project.back_end.models.Admin;
import com.project.back_end.services.Service;


@RestController
@RequestMapping("${api.path}prescription")
public class PrescriptionController {
    
    
// 1. Set Up the Controller Class:
//    - Annotate the class with `@RestController` to define it as a REST API controller.
//    - Use `@RequestMapping("${api.path}prescription")` to set the base path for all prescription-related endpoints.
//    - This controller manages creating and retrieving prescriptions tied to appointments.


// 2. Autowire Dependencies:
//    - Inject `PrescriptionService` to handle logic related to saving and fetching prescriptions.
//    - Inject the shared `Service` class for token validation and role-based access control.
//    - Inject `AppointmentService` to update appointment status after a prescription is issued.
    private final PrescriptionService prescriptionService;
    private final Service service;  // For token validation and common functionality

    // @Autowired - commented out as not necessary
    public PrescriptionController(PrescriptionService prescriptionService, Service service) {
        this.prescriptionService = prescriptionService;
        this.service = service;
    }
    


// 3. Define the `savePrescription` Method:
//    - Handles HTTP POST requests to save a new prescription for a given appointment.
//    - Accepts a validated `Prescription` object in the request body and a doctor’s token as a path variable.
//    - Validates the token for the `"doctor"` role.
//    - If the token is valid, updates the status of the corresponding appointment to reflect that a prescription has been added.
//    - Delegates the saving logic to `PrescriptionService` and returns a response indicating success or failure.
    @PostMapping("/{token}")
    public ResponseEntity<Map<String, String>> savePrescription(@PathVariable String token,
                                                                @RequestBody Prescription prescription) {
        // Validate token for doctor role
        ResponseEntity<Map<String, String>> validationResponse = service.validateToken(token, "doctor");
        if (validationResponse != null) {
            return validationResponse;  // Return error if token invalid or unauthorized
        }

        // Save the prescription
        ResponseEntity<Map<String, String>> saveResponse = prescriptionService.savePrescription(prescription);
        return saveResponse;
    }

// 4. Define the `getPrescription` Method:
//    - Handles HTTP GET requests to retrieve a prescription by its associated appointment ID.
//    - Accepts the appointment ID and a doctor’s token as path variables.
//    - Validates the token for the `"doctor"` role using the shared service.
//    - If the token is valid, fetches the prescription using the `PrescriptionService`.
//    - Returns the prescription details or an appropriate error message if validation fails.
    @GetMapping("/{appointmentId}/{token}")
    public ResponseEntity<Map<String, Object>> getPrescriptionByAppointmentId(@PathVariable Long appointmentId,
                                                                             @PathVariable String token) {
        // Validate token for doctor role
        ResponseEntity<Map<String, String>> validationResponse = service.validateToken(token, "doctor");
        if (validationResponse != null) {
            return new ResponseEntity<>(Map.of("message", "Invalid or unauthorized token"), HttpStatus.UNAUTHORIZED);
        }

        // Retrieve the prescription
        ResponseEntity<Map<String, Object>> prescriptionResponse = prescriptionService.getPrescription(appointmentId);
        return prescriptionResponse;
    }

}
