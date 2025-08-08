// config.js

/**
 * Configuration file for defining global constants and environment-specific settings.
 * 
 * API_BASE_URL:
 * - Base URL for all API requests made from the frontend.
 * - Easily switchable for different environments (development, staging, production).
 * 
 * Example usage:
 *   fetch(`${API_BASE_URL}/api/appointments`)
 */

export const API_BASE_URL = "http://localhost:8080";
// export const API_BASE_URL = "https://theiadockernext-0-labs-prod-theiak8s-4-tor01.labs.cognitiveclass.ai/user/lorenzomason/url";

// Define constants for the admin and doctor login API endpoints using the base URL
export const ADMIN_API = "http://localhost:8080/admin/login";
export const DOCTOR_API = "http://localhost:8080/doctor/login";