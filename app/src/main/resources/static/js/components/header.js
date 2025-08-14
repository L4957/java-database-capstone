// header.js

  // Step-by-Step Explanation of Header Section Rendering

  // This code dynamically renders the header section of the page based on the user's role, session status, and available actions (such as login, logout, or role-switching).

  // 1. Define the `renderHeader` Function
export function renderHeader(role, token) {
    role = role || localStorage.getItem("userRole");
    token = token || localStorage.getItem("token");
  //   * The `renderHeader` function is responsible for rendering the entire header based on the user's session, role, and whether they are logged in.
    if (window.location.pathname.endsWith("/")) {
      localStorage.removeItem("userRole");
      localStorage.removeItem("token");
    }

  // 2. Select the Header Div

  //   * The `headerDiv` variable retrieves the HTML element with the ID `header`, where the header content will be inserted.
       
       const headerDiv = document.getElementById("header");
       

  // 3. Check if the Current Page is the Root Page

  //   * The `window.location.pathname` is checked to see if the current page is the root (`/`). If true, the user's session data (role) is removed from `localStorage`, and the header is rendered without any user-specific elements (just the logo and site title).
  //     ```javascript
       if (window.location.pathname.endsWith("/")) {
         localStorage.removeItem("userRole");
         headerDiv.innerHTML = `
           <header class="header">
             <div class="logo-section">
               <img src="../assets/images/logo/logo.png" alt="Hospital CRM Logo" class="logo-img">
               <span class="logo-title">Hospital CMS</span>
             </div>
           </header>`;
         return;
       }
  //     ```

  // 4. Retrieve the User's Role and Token from LocalStorage

  //   * The `role` (user role like admin, patient, doctor) and `token` (authentication token) are retrieved from `localStorage` to determine the user's current session.
  //     ```javascript
       role = localStorage.getItem("userRole");
       token = localStorage.getItem("token");
  //     ```

//  5. Initialize Header Content

  //   * The `headerContent` variable is initialized with basic header HTML (logo section), to which additional elements will be added based on the user's role.
  //     ```javascript
       let headerContent = `<header class="header">
         <div class="logo-section">
           <img src="../assets/images/logo/logo.png" alt="Hospital CRM Logo" class="logo-img">
           <span class="logo-title">Hospital CMS</span>
         </div>
         <nav>`;
  //     ```

  // 6. Handle Session Expiry or Invalid Login

  /* 
  //   * If a user with a role like `loggedPatient`, `admin`, or `doctor` does not have a valid `token`, the session is considered expired or invalid. The user is logged out, and a message is shown.
  //     ```javascript
       if ((role === "loggedPatient" || role === "admin" || role === "doctor") && !token) {
         localStorage.removeItem("userRole");
         alert("Session expired or invalid login. Please log in again.");
         window.location.href = "/";   // or a specific login page
         return;
       }
  //    ```

  // 7. Add Role-Specific Header Content

     // * Depending on the user's role, different actions or buttons are rendered in the header:
     //  - **Admin**: Can add a doctor and log out.
     //  - **Doctor**: Has a home button and log out.
     //  - **Patient**: Shows login and signup buttons.
     //  - **LoggedPatient**: Has home, appointments, and logout options.
     //  ```javascript
     
       else if (role === "admin") {
         headerContent += `
           <button id="addDocBtn" class="adminBtn" onclick="openModal('addDoctor')">Add Doctor</button>
           <a href="#" onclick="logout()">Logout</a>`;
       } else if (role === "doctor") {
         headerContent += `
           <button class="adminBtn"  onclick="selectRole('doctor')">Home</button>
           <a href="#" onclick="logout()">Logout</a>`;
       } else if (role === "patient") {
         headerContent += `
           <button id="patientLogin" class="adminBtn">Login</button>
           <button id="patientSignup" class="adminBtn">Sign Up</button>`;
       } else if (role === "loggedPatient") {
         headerContent += `
           <button id="home" class="adminBtn" onclick="window.location.href='/pages/loggedPatientDashboard.html'">Home</button>
           <button id="patientAppointments" class="adminBtn" onclick="window.location.href='/pages/patientAppointments.html'">Appointments</button>
           <a href="#" onclick="logoutPatient()">Logout</a>`;
       }
      // ```
      */


//  9. Close the Header Section
    headerDiv.innerHTML = `
      </header>`;  // <-- This is the closing of the header section


//  10. Render the Header Content

  //   * Insert the dynamically generated `headerContent` into the `headerDiv` element.
  //     ```javascript
        headerDiv.innerHTML = headerContent;
  //     ```


    // const headerDiv = document.getElementById("header");

    // Example: show logout link for logged-in users
    headerDiv.innerHTML = `
      <a href="#" id="logoutLink">Logout</a>
    `;

    
    /*
    // Attach event listener after inserting HTML
    const logoutLink = document.getElementById("logoutLink");
    if (logoutLink) {
      logoutLink.addEventListener("click", (event) => {
        event.preventDefault(); // prevent page jump
        logout();
      });
    }
    */
   
  // 11. Attach Event Listeners to Header Buttons

  //   * Call `attachHeaderButtonListeners` to add event listeners to any dynamically created buttons in the header (e.g., login, logout, home).
  //     ```javascript
       attachHeaderButtonListeners();
  //     ```
}

  // ### Helper Functions

  // 13. **attachHeaderButtonListeners**: Adds event listeners to login buttons for "Doctor" and "Admin" roles. If clicked, it opens the respective login modal.
export function attachHeaderButtonListeners() {
  const doctorLoginBtn = document.getElementById("doctorLoginBtn");
  const adminLoginBtn = document.getElementById("adminLoginBtn");

  if (doctorLoginBtn) {
      doctorLoginBtn.addEventListener("click", () => {
        // Open the Doctor login modal
        const doctorModal = document.getElementById("doctorLoginModal");
        if (doctorModal) {
          doctorModal.style.display = "block";
        }
      });
  }

    if (adminLoginBtn) {
      adminLoginBtn.addEventListener("click", () => {
        // Open the Admin login modal
        const adminModal = document.getElementById("adminLoginModal");
        if (adminModal) {
          adminModal.style.display = "block";
        }
      });
    }
}


//  14. **logout**: Removes user session data and redirects the user to the root page.
export function logout() {
  // Clear user session data from localStorage
  localStorage.removeItem("userRole");
  localStorage.removeItem("token");

  // Redirect to the root page
  window.location.href = "/";
}


export function attachLogoutListener() {
  const logoutLink = document.getElementById("logoutLink");
  if (logoutLink) {
    logoutLink.addEventListener("click", (event) => {
      event.preventDefault(); // Prevent default link behavior
      logout();
    });
  }
}

//  15. **logoutPatient**: Removes the patient's session token and redirects to the patient dashboard.
export function logoutPatient() {
  // Remove only the patient's token from localStorage
  localStorage.removeItem("token");

  // Redirect to the patient dashboard page
  window.location.href = "/patient/dashboard";
}

export function attachPatientLogoutListener() {
  const patientLogoutLink = document.getElementById("patientLogoutLink");
  if (patientLogoutLink) {
    patientLogoutLink.addEventListener("click", (event) => {
      event.preventDefault();
      logoutPatient();
    });
  }
}

//  16. **Render the Header**: Finally, the `renderHeader()` function is called to initialize the header rendering process when the page loads.
  
window.onload = () => {
  renderHeader();
//  attachHeaderButtonListeners()
//  attachLogoutListener();
//  attachPatientLogoutListener();
};
   

































//=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=

/* LM initial attempt
header.innerHTML = `
           <header class="header">
             <div class="logo-section">
               <img src="../assets/images/logo/logo.png" alt="Hospital CRM Logo" class="logo-img">
               <span class="logo-title">Hospital CMS</span>
             </div>
           </header>
           `;
*/

/* LM second attempt

// 1. Define the `renderHeader` Function
export function renderHeader(userRole, token) {
  userRole = userRole || localStorage.getItem("userRole");
  token = token || localStorage.getItem("token");
    // 2. Select the Header Div
    const headerDiv = document.getElementById("header");      
    
    // 3. Check if the Current Page is the Root Page
    if (window.location.pathname.endsWith("/")) {
      localStorage.removeItem("userRole");
      localStorage.removeItem("token");
      headerDiv.innerHTML = `
           <header class="header">
             <div class="logo-section">
               <img src="../assets/images/logo/logo.png" alt="Hospital CRM Logo" class="logo-img">
               <span class="logo-title">Hospital CMS</span>
             </div>
           </header>`;
         return;
    }

    const role = localStorage.getItem("userRole");
    // const token = localStorage.getItem("token");

    // 5. Initialize Header Content
    
    let headerContent = `<header class="header">
         <div class="logo-section">
           <img src="../assets/images/logo/logo.png" alt="Hospital CRM Logo" class="logo-img">
           <span class="logo-title">Hospital CMS</span>
         </div>
         <nav>`;

    
    //  6. Handle Session Expiry or Invalid Login
    if ((role === "loggedPatient" || role === "admin" || role === "doctor") && !token) {
      localStorage.removeItem("userRole");
      alert("Session expired or invalid login. Please log in again.");
      window.location.href = "/";   // or a specific login page
    return;
    }    

    // 7. Add Role-Specific Header Content

    headerContent = "";
    
    
    if (role === "admin") {
        headerContent += `
          <button id="addDocBtn" class="adminBtn" onclick="openModal('addDoctor')">Add Doctor</button>
          <a href="#" onclick="logout()">Logout</a>`;
      } else if (role === "doctor") {
        headerContent += `
          <button class="adminBtn"  onclick="selectRole('doctor')">Home</button>
          <a href="#" onclick="logout()">Logout</a>`;
      } else if (role === "patient") {
        headerContent += `
          <button id="patientLogin" class="adminBtn">Login</button>
          <button id="patientSignup" class="adminBtn">Sign Up</button>`;
      } else if (role === "loggedPatient") {
        headerContent += `
          <button id="home" class="adminBtn" onclick="window.location.href='/pages/loggedPatientDashboard.html'">Home</button>
          <button id="patientAppointments" class="adminBtn" onclick="window.location.href='/pages/patientAppointments.html'">Appointments</button>
          <a href="#" onclick="logoutPatient()">Logout</a>`;
      }

  headerContent += `
      </nav>
    </header>
  `;

  headerDiv.innerHTML = headerContent;

  attachHeaderButtonListeners();

   // Attach event listeners
  const addDocBtn = document.getElementById("addDocBtn");
  if (addDocBtn) {
    addDocBtn.addEventListener("click", () => openModal("addDoctor"));
  }

  const logoutBtn = document.getElementById("logoutBtn");
  if (logoutBtn) {
    logoutBtn.addEventListener("click", () => {
      localStorage.removeItem("userRole");
      localStorage.removeItem("token");
      window.location.href = "/";
    });
  }
  
  
}

function attachHeaderButtonListeners() {
  const addDocBtn = document.getElementById("addDocBtn");
  if (addDocBtn) {
    addDocBtn.addEventListener("click", () => openModal("addDoctor"));
  }


function logout() {
  // Remove the token from localStorage
  localStorage.removeItem("userRole");
  localStorage.removeItem("token");
  
  // Redirect to the patient dashboard or homepage
  window.location.href = "/"; 
}

  function logoutPatient() {
  // Remove the token from localStorage
  localStorage.removeItem("token");
  
  // Set the userRole back to "patient"
  localStorage.setItem("userRole", "patient");
  
  // Redirect to the patient dashboard or homepage
  window.location.href = "/patientDashboard"; // Change this path as needed
}


}

*/

/*
  Step-by-Step Explanation of Header Section Rendering

  This code dynamically renders the header section of the page based on the user's role, session status, and available actions (such as login, logout, or role-switching).

  1. Define the `renderHeader` Function

     * The `renderHeader` function is responsible for rendering the entire header based on the user's session, role, and whether they are logged in.

  2. Select the Header Div

     * The `headerDiv` variable retrieves the HTML element with the ID `header`, where the header content will be inserted.
       ```javascript
       const headerDiv = document.getElementById("header");
       ```

  3. Check if the Current Page is the Root Page

     * The `window.location.pathname` is checked to see if the current page is the root (`/`). If true, the user's session data (role) is removed from `localStorage`, and the header is rendered without any user-specific elements (just the logo and site title).
       ```javascript
       if (window.location.pathname.endsWith("/")) {
         localStorage.removeItem("userRole");
         headerDiv.innerHTML = `
           <header class="header">
             <div class="logo-section">
               <img src="../assets/images/logo/logo.png" alt="Hospital CRM Logo" class="logo-img">
               <span class="logo-title">Hospital CMS</span>
             </div>
           </header>`;
         return;
       }
       ```

  4. Retrieve the User's Role and Token from LocalStorage

     * The `role` (user role like admin, patient, doctor) and `token` (authentication token) are retrieved from `localStorage` to determine the user's current session.
       ```javascript
       const role = localStorage.getItem("userRole");
       const token = localStorage.getItem("token");
       ```

  5. Initialize Header Content

     * The `headerContent` variable is initialized with basic header HTML (logo section), to which additional elements will be added based on the user's role.
       ```javascript
       let headerContent = `<header class="header">
         <div class="logo-section">
           <img src="../assets/images/logo/logo.png" alt="Hospital CRM Logo" class="logo-img">
           <span class="logo-title">Hospital CMS</span>
         </div>
         <nav>`;
       ```

  6. Handle Session Expiry or Invalid Login

     * If a user with a role like `loggedPatient`, `admin`, or `doctor` does not have a valid `token`, the session is considered expired or invalid. The user is logged out, and a message is shown.
       ```javascript
       if ((role === "loggedPatient" || role === "admin" || role === "doctor") && !token) {
         localStorage.removeItem("userRole");
         alert("Session expired or invalid login. Please log in again.");
         window.location.href = "/";   or a specific login page
         return;
       }
       ```

  7. Add Role-Specific Header Content

     * Depending on the user's role, different actions or buttons are rendered in the header:
       - **Admin**: Can add a doctor and log out.
       - **Doctor**: Has a home button and log out.
       - **Patient**: Shows login and signup buttons.
       - **LoggedPatient**: Has home, appointments, and logout options.
       ```javascript
       else if (role === "admin") {
         headerContent += `
           <button id="addDocBtn" class="adminBtn" onclick="openModal('addDoctor')">Add Doctor</button>
           <a href="#" onclick="logout()">Logout</a>`;
       } else if (role === "doctor") {
         headerContent += `
           <button class="adminBtn"  onclick="selectRole('doctor')">Home</button>
           <a href="#" onclick="logout()">Logout</a>`;
       } else if (role === "patient") {
         headerContent += `
           <button id="patientLogin" class="adminBtn">Login</button>
           <button id="patientSignup" class="adminBtn">Sign Up</button>`;
       } else if (role === "loggedPatient") {
         headerContent += `
           <button id="home" class="adminBtn" onclick="window.location.href='/pages/loggedPatientDashboard.html'">Home</button>
           <button id="patientAppointments" class="adminBtn" onclick="window.location.href='/pages/patientAppointments.html'">Appointments</button>
           <a href="#" onclick="logoutPatient()">Logout</a>`;
       }
       ```



  9. Close the Header Section



  10. Render the Header Content

     * Insert the dynamically generated `headerContent` into the `headerDiv` element.
       ```javascript
       headerDiv.innerHTML = headerContent;
       ```

  11. Attach Event Listeners to Header Buttons

     * Call `attachHeaderButtonListeners` to add event listeners to any dynamically created buttons in the header (e.g., login, logout, home).
       ```javascript
       attachHeaderButtonListeners();
       ```


  ### Helper Functions

  13. **attachHeaderButtonListeners**: Adds event listeners to login buttons for "Doctor" and "Admin" roles. If clicked, it opens the respective login modal.

  14. **logout**: Removes user session data and redirects the user to the root page.

  15. **logoutPatient**: Removes the patient's session token and redirects to the patient dashboard.

  16. **Render the Header**: Finally, the `renderHeader()` function is called to initialize the header rendering process when the page loads.
*/
   

/* LM test 3rd
export function renderHeader(userRole, token) {
  userRole = userRole || localStorage.getItem("userRole");
  token = token || localStorage.getItem("token");

  const headerDiv = document.getElementById("header");

  // If on root page, clear session and show default header
  if (window.location.pathname.endsWith("/")) {
    localStorage.removeItem("userRole");
    localStorage.removeItem("token");
    headerDiv.innerHTML = `
      <header class="header">
        <div class="logo-section">
          <img src="../assets/images/logo/logo.png" alt="Hospital CRM Logo" class="logo-img">
          <span class="logo-title">Hospital CMS</span>
        </div>
      </header>`;
    return;
  }

  // If no token or role, show minimal header or prompt to login
  if (!token || !userRole) {
    headerDiv.innerHTML = `
      <header class="header">
        <div class="logo-section">
          <img src="../assets/images/logo/logo.png" alt="Hospital CRM Logo" class="logo-img">
          <span class="logo-title">Hospital CMS</span>
        </div>
        <nav>
          <a href="/login">Login</a>
        </nav>
      </header>`;
    return;
  }

  // Render header based on user role
  let navLinks = "";
  if (userRole === "admin") {
    navLinks = `
      <nav>
        <a href="/admin/dashboard">Dashboard</a>
        <a href="/admin/users">Manage Users</a>
        <a href="/logout">Logout</a>
      </nav>`;
  } else if (userRole === "doctor") {
    navLinks = `
      <nav>
        <a href="/doctor/appointments">Appointments</a>
        <a href="/doctor/patients">Patients</a>
        <a href="/logout">Logout</a>
      </nav>`;
  } else if (userRole === "patient") {
    navLinks = `
      <nav>
        <a href="/patient/appointments">My Appointments</a>
        <a href="/patient/profile">Profile</a>
        <a href="/logout">Logout</a>
      </nav>`;
  } else {
    // default or unknown role
    navLinks = `
      <nav>
        <a href="/logout">Logout</a>
      </nav>`;
  }

  headerDiv.innerHTML = `
    <header class="header">
      <div class="logo-section">
        <img src="../assets/images/logo/logo.png" alt="Hospital CRM Logo" class="logo-img">
        <span class="logo-title">Hospital CMS</span>
      </div>
      ${navLinks}
    </header>`;

}

window.onload = () => {
    renderHeader();
};

*/
