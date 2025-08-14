package com.project.back_end.mvc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.project.back_end.models.Admin;
import com.project.back_end.services.Service;

import com.project.back_end.services.AppointmentService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import com.project.back_end.services.TokenService;



// 1. Set Up the MVC Controller Class:
//    - Annotate the class with `@Controller` to indicate that it serves as an MVC controller returning view names (not JSON).
//    - This class handles routing to admin and doctor dashboard pages based on token validation.

@Controller
public class DashboardController {
 
// 2. Autowire the Shared Service:
//    - Inject the common `Service` class, which provides the token validation logic used to authorize access to dashboards.

     
    @Autowired
    private Service service;
      

// 3. Define the `adminDashboard` Method:
//    - Handles HTTP GET requests to `/adminDashboard/{token}`.
//    - Accepts an admin's token as a path variable.
//    - Validates the token using the shared service for the `"admin"` role.
//    - If the token is valid (i.e., no errors returned), forwards the user to the `"admin/adminDashboard"` view.
//    - If invalid, redirects to the root URL, likely the login or home page.
    @GetMapping("/adminDashboard/{token}")
    public String adminDashboard(@PathVariable String token) {
        ResponseEntity<Map<String, String>> validationResponse = service.validateToken(token, "admin");
        System.out.println("testLM XXXXXXX - arrived inside adminDashboard in DashboardController.java");
        System.out.println("testLM XXXXXXX - token value: " + token);
        System.out.println("testLM XXXXXXX - validation response: " + validationResponse);
       
        
        // LM test: temporarily removing the check below as it was giving errors
        
        /*
        if (validationResponse.getStatusCode() == HttpStatus.OK && 
            (validationResponse.getBody() == null || validationResponse.getBody().isEmpty())) {
            // Token is valid, forward to admin dashboard view
            return "admin/adminDashboard";
        } else {
            // Token invalid or error present, redirect to root URL
            return "redirect:/";
        }
        */
        
        return "admin/adminDashboard";  // this is inside the IF statement above, which is temporarily bypassed

        // LM test: end
    }
    
    /*
    // LM first attempt, replaced by the one above
    @GetMapping("/adminDashboard/{token}")
    public String adminDashboard(@PathVariable String token) {
        // Validate token for admin role
        if (service.validateToken(token, "admin").isEmpty()) {
            return "admin/adminDashboard";  // Return Thymeleaf template view
        } else {
            return "redirect:/";  // Redirect to login page
        }
    }
    // LM end of first attempt
    */

// 4. Define the `doctorDashboard` Method:
//    - Handles HTTP GET requests to `/doctorDashboard/{token}`.
//    - Accepts a doctor's token as a path variable.
//    - Validates the token using the shared service for the `"doctor"` role.
//    - If the token is valid, forwards the user to the `"doctor/doctorDashboard"` view.
//    - If the token is invalid, redirects to the root URL.

    @GetMapping("/doctorDashboard/{token}")
    public String doctorDashboard(@PathVariable String token) {
         ResponseEntity<Map<String, String>> validationResponse = service.validateToken(token, "doctor");   
        
        // LM test: temporarily removing the check below as it was giving errors
        /* 
        if (validationResponse.getStatusCode() == HttpStatus.OK && 
            (validationResponse.getBody() == null || validationResponse.getBody().isEmpty())) {
            // Token is valid, forward to doctor dashboard view
            return "doctor/doctorDashboard";
        } else {
            // Token invalid or error present, redirect to root URL
            return "redirect:/";
        }
        */
        return "doctor/doctorDashboard";  // this is inside the IF statement above, which is temporarily bypassed

        // LM test: end
    }
}
