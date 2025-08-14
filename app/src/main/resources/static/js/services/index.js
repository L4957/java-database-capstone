// Import the openModal function to handle showing login popups/modals
import { openModal } from "../components/modals.js";
import { selectRole } from '../render.js';
// Import the base API URL from the config file
// import { API_BASE_URL } from "../config/config.js";
import { ADMIN_API  } from "../config/config.js";
import { DOCTOR_API } from "../config/config.js";
import { PATIENT_API } from "../config/config.js";



window.addEventListener('load', () => {
  const adminBtn = document.getElementById('Admin_Button');
  if (adminBtn) {
    adminBtn.addEventListener('click', () => {
      console.log('Admin_Button click is detected inside index.js');
      openModal('adminLogin');
    });
  }
});


window.addEventListener('load', () => {
  const adminBtn = document.getElementById('Doctor_Button');
  if (adminBtn) {
    adminBtn.addEventListener('click', () => {
      console.log('Doctor_Button click is detected inside index.js');
      openModal('doctorLogin');
    });
  }
});


window.addEventListener('load', () => {
  const adminBtn = document.getElementById('Patient_Button');
  if (adminBtn) {
    adminBtn.addEventListener('click', () => {
      console.log('Patient_Button click is detected inside index.js');
      openModal('patientLogin');
    });
  }
});

/* LM note: using the window.onload worked only for the second button (Doctor_Button), 
    but not for the first (Admin_Button). The approach above solves for both the buttons.
window.onload = function () {
    const adminBtn = document.getElementById('Admin_Button');
    if (adminBtn) {
        adminBtn.addEventListener('click', () => {
          console.log('Admin_Button click is detected inside index.js');
          openModal('adminLogin');
        });
    }
}

window.onload = function () {
    const doctorBtn = document.getElementById('Doctor_Button');
    if (doctorBtn) {
        doctorBtn.addEventListener('click', () => { 
          console.log('Doctor_Button click is detected inside index.js');
          openModal('doctorLogin');
        });
    }
}

*/


// Define a function named adminLoginHandler on the global window object
// This function will be triggered when the admin submits their login credentials


export async function adminLoginHandler(email, password) {
  try {
    
    console.log("XXXXX Username: ", email);
    console.log("XXXXX Password: ", password);
    
    const admin = { username: email, password };
    console.log("XXXXX admin", admin);
    
    const response = await fetch(`${ADMIN_API}/login`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(admin)
    
      });
    console.log("XXXXX response: ", response);  
    const result = await response.json();
    console.log("XXXXX response result: ", result);
    
    if (!response.ok) {
      alert("Invalid username or password. Please try again.");
      throw new Error(result.message);
    }
    // Store token in localStorage
    const token = result.token;
    localStorage.setItem("token", token);
    console.log("XXXXX check stored token: ", localStorage.getItem("token"));

    // Proceed with admin-specific behavior
    selectRole("admin");
    return { success: response.ok, message: result.message }
  }
  catch (error) {
        console.error("Error :: adminLoginHandler :: ", error)
        return { success: false, message: error.message }
  }
}
  

// Define a function named doctorLoginHandler on the global window object
// This function will be triggered when a doctor submits their login credentials

export async function doctorLoginHandler(email, password) {
  try {  
        const doctor = { email, password };
        const response = await fetch(`${DOCTOR_API}/login`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(doctor)
        });
        console.log("XXXXX response: ", response); 
        const result = await response.json();
        console.log("XXXXX response result: ", result);
        if (!response.ok) {
          alert("Invalid username or password. Please try again.");
          throw new Error(result.message);
        }
        // Store token in localStorage
        const token = result.token;
        localStorage.setItem("token", token);
        console.log("XXXXX check stored token: ", localStorage.getItem("token"));
        // Proceed with doctor-specific behavior
        selectRole("doctor");
        return { success: response.ok, message: result.message }
    }
  catch (error) {
            console.error("Error :: doctorLoginHandler :: ", error)
            return { success: false, message: error.message }
    }
}            

/*
export async function doctorLoginHandler(username, password) {
  
  const doctor = { email: username, password };  // map username to email  
  
  try {  
        const response = await fetch(DOCTOR_API, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(doctor)
      });
      const result = await response.json();
      if (!response.ok) {
        throw new Error(result.message);
      }
      return { success: response.ok, message: result.message }
  }
  catch (error) {
      console.error("Error :: doctorLoginHandler :: ", error)
      return { success: false, message: error.message }
  }
}
*/

// patientLoginHandler - triggered by modals.js
export async function patientLoginHandler(email, password) {
  try {  
        const patient = { email, password };
        const response = await fetch(`${PATIENT_API}/login`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(patient)
        });
        console.log("XXXXX response: ", response); 
        const result = await response.json();
        console.log("XXXXX response result: ", result);
        if (!response.ok) {
          alert("Invalid username or password. Please try again.");
          throw new Error(result.message);
        }
        // Store token in localStorage
        const token = result.token;
        localStorage.setItem("token", token);
        console.log("XXXXX check stored token: ", localStorage.getItem("token"));
        // Proceed with admin-specific behavior
        selectRole("patient");
        return { success: response.ok, message: result.message }
    }
  catch (error) {
            console.error("Error :: patientLoginHandler :: ", error)
            return { success: false, message: error.message }
    }
}                 

/*
  Import the openModal function to handle showing login popups/modals
  Import the base API URL from the config file
  Define constants for the admin and doctor login API endpoints using the base URL

  Use the window.onload event to ensure DOM elements are available after page load
  Inside this function:
    - Select the "adminLogin" and "doctorLogin" buttons using getElementById
    - If the admin login button exists:
        - Add a click event listener that calls openModal('adminLogin') to show the admin login modal
    - If the doctor login button exists:
        - Add a click event listener that calls openModal('doctorLogin') to show the doctor login modal


  Define a function named adminLoginHandler on the global window object
  This function will be triggered when the admin submits their login credentials

  Step 1: Get the entered username and password from the input fields
  Step 2: Create an admin object with these credentials

  Step 3: Use fetch() to send a POST request to the ADMIN_API endpoint
    - Set method to POST
    - Add headers with 'Content-Type: application/json'
    - Convert the admin object to JSON and send in the body

  Step 4: If the response is successful:
    - Parse the JSON response to get the token
    - Store the token in localStorage
    - Call selectRole('admin') to proceed with admin-specific behavior

  Step 5: If login fails or credentials are invalid:
    - Show an alert with an error message

  Step 6: Wrap everything in a try-catch to handle network or server errors
    - Show a generic error message if something goes wrong


  Define a function named doctorLoginHandler on the global window object
  This function will be triggered when a doctor submits their login credentials

  Step 1: Get the entered email and password from the input fields
  Step 2: Create a doctor object with these credentials

  Step 3: Use fetch() to send a POST request to the DOCTOR_API endpoint
    - Include headers and request body similar to admin login

  Step 4: If login is successful:
    - Parse the JSON response to get the token
    - Store the token in localStorage
    - Call selectRole('doctor') to proceed with doctor-specific behavior

  Step 5: If login fails:
    - Show an alert for invalid credentials

  Step 6: Wrap in a try-catch block to handle errors gracefully
    - Log the error to the console
    - Show a generic error message
*/
