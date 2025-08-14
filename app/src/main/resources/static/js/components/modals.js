// modals.js
import { adminLoginHandler } from "../services/index.js";
import { doctorLoginHandler } from "../services/index.js";
import { patientLoginHandler } from "../services/index.js";
// import { DOCTOR_API } from "../config/config.js";




export function openModal(type) {
  let modalContent = '';
  if (type === 'addDoctor') {
    modalContent = `
         <h2>Add Doctor</h2>
         <input type="text" id="doctorName" placeholder="Doctor Name" class="input-field">
         <select id="specialization" class="input-field select-dropdown">
             <option value="">Specialization</option>
                        <option value="cardiologist">Cardiologist</option>
                        <option value="dermatologist">Dermatologist</option>
                        <option value="neurologist">Neurologist</option>
                        <option value="pediatrician">Pediatrician</option>
                        <option value="orthopedic">Orthopedic</option>
                        <option value="gynecologist">Gynecologist</option>
                        <option value="psychiatrist">Psychiatrist</option>
                        <option value="dentist">Dentist</option>
                        <option value="ophthalmologist">Ophthalmologist</option>
                        <option value="ent">ENT Specialist</option>
                        <option value="urologist">Urologist</option>
                        <option value="oncologist">Oncologist</option>
                        <option value="gastroenterologist">Gastroenterologist</option>
                        <option value="general">General Physician</option>

        </select>
        <input type="email" id="doctorEmail" placeholder="Email" class="input-field">
        <input type="password" id="doctorPassword" placeholder="Password" class="input-field">
        <input type="text" id="doctorPhone" placeholder="Mobile No." class="input-field">
        <div class="availability-container">
        <label class="availabilityLabel">Select Availability:</label>
          <div class="checkbox-group">
              <label><input type="checkbox" name="availability" value="09:00-10:00"> 9:00 AM - 10:00 AM</label>
              <label><input type="checkbox" name="availability" value="10:00-11:00"> 10:00 AM - 11:00 AM</label>
              <label><input type="checkbox" name="availability" value="11:00-12:00"> 11:00 AM - 12:00 PM</label>
              <label><input type="checkbox" name="availability" value="12:00-13:00"> 12:00 PM - 1:00 PM</label>
          </div>
        </div>
        <button class="dashboard-btn" id="saveDoctorBtn">Save</button>
      `;
  } else if (type === 'patientLogin') {
    modalContent = `
        <h2>Patient Login</h2>
        <input type="text" id="email" placeholder="Email" class="input-field">
        <input type="password" id="password" placeholder="Password" class="input-field">
        <button class="dashboard-btn" id="patientLoginBtn">Login</button>
      `;
  }
  else if (type === "patientSignup") {
    modalContent = `
      <h2>Patient Signup</h2>
      <input type="text" id="name" placeholder="Name" class="input-field">
      <input type="email" id="email" placeholder="Email" class="input-field">
      <input type="password" id="password" placeholder="Password" class="input-field">
      <input type="text" id="phone" placeholder="Phone" class="input-field">
      <input type="text" id="address" placeholder="Address" class="input-field">
      <button class="dashboard-btn" id="signupBtn">Signup</button>
    `;

  } else if (type === 'adminLogin') {
    modalContent = `
        <h2>Admin Login</h2>
        <input type="text" id="username" placeholder="Username" class="input-field">
        <input type="password" id="password" placeholder="Password" class="input-field">
        <button class="dashboard-btn" id="adminLoginBtn" >Login</button>
      `;
      console.log('Clicking the AdminLogin is detected and arrives inside modals.js part for adminLogin')
  } else if (type === 'doctorLogin') {
    modalContent = `
        <h2>Doctor Login</h2>
        <input type="text" id="email" placeholder="Email" class="input-field">
        <input type="password" id="password" placeholder="Password" class="input-field">
        <button class="dashboard-btn" id="doctorLoginBtn" >Login</button>
      `;
  
  } 


  const modalContentContainer = document.querySelector('#modal .modal-content');
  modalContentContainer.innerHTML = `<span id="modalClose" class="modal-close">&times;</span>` + modalContent;

  const modal = document.getElementById('modal');
  modal.style.display = 'flex';

  // Close modal on clicking close button
  document.getElementById('modalClose').addEventListener('click', closeModal);

  /*
  // Attach event listener for login button
  const loginBtn = document.getElementById('adminLoginBtn');
  if (loginBtn) {
    loginBtn.addEventListener('click', async () => {
      const username = document.getElementById('username').value.trim();
      const password = document.getElementById('password').value.trim();

      if (!username || !password) {
        alert('Please enter username and password.');
        return;
      }

      const result = await adminLoginHandler(username, password);
      if (result.success) {
        alert('Login successful!');
        closeModal();
        // Redirect or load dashboard here:
        // window.location.href = '/admin/dashboard.html';
      } else {
        alert('Login failed: ' + result.message);
      }
    });
  }
    */

  if (type === "patientSignup") {
    document.getElementById("signupBtn").addEventListener("click", signupPatient);
  }

  if (type === "patientLogin") {
    document.getElementById("patientLoginBtn").addEventListener('click', async () => {
      const email = document.getElementById('email').value.trim();
      const password = document.getElementById('password').value.trim();
      console.log("XXXXX Email:", email);
      console.log("XXXXX Password:", password);
      const patient = { email, password };  // map username to email
      console.log("XXXXX Patient:", patient);
      console.log('Patient login from modals.js in the "patientLoginHandler" section');
      patientLoginHandler(email, password);
      /*
      try {
        const response = await fetch("http://localhost:8080/patient/login", {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify(patient)
        });
        
        console.log("XXXXX response: ", response);
        const result = await response.json();
        
        console.log("XXXXX response result: ", result);

        if (!response.ok) {
          throw new Error(result.message);
        }

        // handle successful login
        console.log('Login successful', result);
      } catch (error) {
        console.error('Login failed:', error.message);
      }
      */
    });
  }

  if (type === 'addDoctor') {
    document.getElementById('saveDoctorBtn').addEventListener('click', adminAddDoctor);
  }

  if (type === 'adminLogin') {
    // document.getElementById('adminLoginBtn').addEventListener('click', () => adminLoginHandler);
    document.getElementById('adminLoginBtn').addEventListener('click', async () => {
      const email = document.getElementById('username').value.trim();
      const password = document.getElementById('password').value.trim();
      console.log("XXXXX Email:", email);
      console.log("XXXXX Password:", password);
      const admin = { username: email, password };  // map username to email
      console.log("XXXXX Admin:", admin);
      console.log('Admin login from modals.js in the "adminLoginHandler" section');
      adminLoginHandler(email, password);
      /*
      try {
        const response = await fetch("http://localhost:8080/admin/login", {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify(admin)
        });
        
        console.log("XXXXX response: ", response);
        const result = await response.json();
        
        console.log("XXXXX response result: ", result);

        if (!response.ok) {
          throw new Error(result.message);
        }

        // handle successful login
        console.log('Login successful', result);
      } catch (error) {
        console.error('Login failed:', error.message);
      }
    */   
    });
     
  }

  if (type === 'doctorLogin') {
    //document.getElementById('doctorLoginBtn').addEventListener('click', doctorLoginHandler);
      document.getElementById('doctorLoginBtn').addEventListener('click', async () => {
      const email = document.getElementById('email').value.trim();
      const password = document.getElementById('password').value.trim();
      console.log("XXXXX Email:", email);
      console.log("XXXXX Password:", password);
      const doctor = { email, password };  // map username to email
      console.log("XXXXX Doctor:", doctor);
      console.log('Doctor login from modals.js in the "doctorLoginHandler" section');
      doctorLoginHandler(email, password);
      /*
      try {
        const response = await fetch("http://localhost:8080/doctor/login", {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify(doctor)
        });
        
        console.log("XXXXX response: ", response);
        const result = await response.json();
        
        console.log("XXXXX response result: ", result);

        if (!response.ok) {
          throw new Error(result.message);
        }

        // handle successful login
        console.log('Login successful', result);
      } catch (error) {
        console.error('Login failed:', error.message);
      }
      */
    });
  }
}

// Close modal function
function closeModal() {
  const modal = document.getElementById('modal');
  modal.style.display = 'none';
}

/* LM temp save
 } else if (type === 'adminLogin') {
    modalContent = `
       =<h2>Admin Login</h2>
        <input type="text" id="username" name="username" placeholder="Username" class="input-field">
        <input type="password" idssword" name="password" placeholder="Password" class="input-field">
        <button class="dashboard-btn" id="adminLoginBtn" >Login</button>
      `;


  } else if (type === 'doctorLogin') {
    modalContent = `
        <h2>Doctor Login</h2>
        <input type="text" id="email" placeholder="Email" class="input-field">
        <input type="password" id="password" placeholder="Password" class="input-field">
        <button class="dashboard-btn" id="doctorLoginBtn" >Login</button>
      `;
  
  } 

*/