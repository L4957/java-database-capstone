// render.js
import { setRole } from "./util.js";
import { getRole } from "./util.js";

export function selectRole(role) {
  setRole(role);
  const token = localStorage.getItem('token');
  if (role === "admin") {
    if (token) {
      window.location.href = `/adminDashboard/${token}`;
      //window.location.href = `/admin/adminDashboard`;
    }
  } if (role === "patient") {
    window.location.href = "/pages/patientDashboard.html";
  } else if (role === "doctor") {
    if (token) {
      window.location.href = `/doctorDashboard/${token}`;
    } else if (role === "loggedPatient") {
      window.location.href = "loggedPatientDashboard.html";
    }
  }
}



export function renderContent() {
  const role = localStorage.getItem("userRole");
  const token = localStorage.getItem("token");

  // If no role, redirect to role selection or login page
  if (!role) {
    window.location.href = "/";
    return;
  }

  // If role exists but token is missing, clear role and redirect to login
  if ((role === "admin" || role === "doctor" || role === "loggedPatient") && !token) {
    localStorage.removeItem("userRole");
    alert("Session expired or invalid login. Please log in again.");
    window.location.href = "/";
    return;
  }

  // Redirect based on role and token presence
  if (role === "admin" && token) {
    window.location.href = `/adminDashboard/${encodeURIComponent(token)}`;
  } else if (role === "doctor" && token) {
    window.location.href = `/doctorDashboard/${encodeURIComponent(token)}`;
  } else if (role === "loggedPatient" && token) {
    window.location.href = `/patientDashboard/${encodeURIComponent(token)}`;
  } else {
    // For any other role or missing token, redirect to homepage
    window.location.href = "/";
  }
}


/* LM: first attempt
export function renderContent() {
  const role = getRole();
  if (!role) {
    window.location.href = "/"; // if no role, send to role selection page
    return;
  }
}
*/  
