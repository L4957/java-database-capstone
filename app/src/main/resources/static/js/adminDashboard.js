import { openModal } from "../components/modals.js";
import { getDoctors, filterDoctors, saveDoctor } from "./services/doctorServices.js";
import { createDoctorCard } from "./components/doctorCard.js"


document.getElementById('addDocBtn').addEventListener('click', () => {
    openModal('addDoctor');
});

document.getElementById('addDocBtn').addEventListener('click', () => {
    loadDoctorCards();
});

function loadDoctorCards() {getDoctors()
    .then(response => {
        const doctors = response.doctors;
        const contentDiv = document.getElementById("content");
        contentDiv.innerHTML = "";
  
        if (doctors.length > 0) {
          console.log(doctors);
          doctors.forEach(doctor => {
            const card = createDoctorCard(doctor);
            contentDiv.appendChild(card);
          });
        } else {
          contentDiv.innerHTML = "<p>No doctors found with the given filters.</p>";
          console.log("Nothing");
        }
      })
      .catch(error => {
        console.error("Failed to load doctors:", error);
        alert("❌ An error occurred while loading doctors.");
      });
  } 
  
document.getElementById("searchBar").addEventListener("input", filterDoctorsOnChange);
document.getElementById("filterTime").addEventListener("change", filterDoctorsOnChange);
document.getElementById("filterSpecialty").addEventListener("change", filterDoctorsOnChange);

function filterDoctorsOnChange() {
    const searchBar = document.getElementById("searchBar").value.trim();
    const filterTime = document.getElementById("filterTime").value;
    const filterSpecialty = document.getElementById("filterSpecialty").value;
  
  
    const name = searchBar.length > 0 ? searchBar : null;
    const time = filterTime.length > 0 ? filterTime : null;
    const specialty = filterSpecialty.length > 0 ? filterSpecialty : null;
  
    filterDoctors(name, time, specialty)
      getDoctors()  
        .then(response => {
        const doctors = response.doctors;
        const contentDiv = document.getElementById("content");
        contentDiv.innerHTML = "";
  
        if (doctors.length > 0) {
          console.log(doctors);
          doctors.forEach(doctor => {
            const card = createDoctorCard(doctor);
            contentDiv.appendChild(card);
          });
        } else {
          contentDiv.innerHTML = "<p>No doctors found with the given filters.</p>";
          console.log("Nothing");
        }
      })
      .catch(error => {
        console.error("Failed to filter doctors:", error);
        alert("❌ An error occurred while filtering doctors.");
      });
  }
  
function renderDoctorCards(doctors) {
  getDoctors()
    .then(response => {
        const contentDiv = document.getElementById("content");
        contentDiv.innerHTML = "";
  
        if (doctors.length > 0) {
          console.log(doctors);
          doctors.forEach(doctor => {
            const card = createDoctorCard(doctor);
            contentDiv.appendChild(card);
          });
        } else {
          contentDiv.innerHTML = "<p>No doctors found with the given filters.</p>";
          console.log("Nothing");
        }
      })
      .catch(error => {
        console.error("Failed to filter doctors:", error);
        alert("❌ An error occurred while filtering doctors.");
      });

  }

// Point 7: Part to be completed
// function adminAddDoctor()

  document.getElementById("Add Doctor").addEventListener("input", openModal);

  // 7.1. Open the modal when "Add Doctor" button is clicked
  document.getElementById('addDocBtn').addEventListener('click', () => {
    openModal('addDoctor');  // openModal imported from './components/modals.js'
  });

  // 7.2. Function to handle form submission
  async function adminAddDoctor(event) {
    event.preventDefault(); // prevent default form submit behavior

    // 7.3. Collect form data
    const name = document.getElementById('doctorName').value;
    const specialty = document.getElementById('doctorSpecialty').value;
    const email = document.getElementById('doctorEmail').value;
    const password = document.getElementById('doctorPassword').value;
    const mobileNo = document.getElementById('doctorMobile').value;

    // Collect availability checkboxes (example)
    const availability = [];
    document.querySelectorAll('input[name="availability"]:checked').forEach(cb => {
      availability.push(cb.value);
    });

    const doctor = {
      name,
      specialty,
      email,
      password,
      mobileNo,
      availability,
    };

    // 7.4. Verify admin token
    const token = localStorage.getItem('token');
    if (!token) {
      alert('Admin not authenticated. Please login.');
      return;
    }

    try {
      // 7.5. Send POST request to save doctor
      const response = await saveDoctor(doctor, token); // saveDoctor imported from './services/doctorServices.js'

      if (response.success) {
        alert('Doctor added successfully!');
        closeModal('addDoctor');  // closeModal should close the modal
        // Reload doctor list or page as needed
        location.reload();
      } else {
        alert('Failed to add doctor: ' + response.message);
      }
    } catch (error) {
      console.error('Error adding doctor:', error);
      alert('An error occurred while adding the doctor.');
    }
    // 7.6. Bind form submit event
    document.getElementById('addDoctorForm').addEventListener('submit', adminAddDoctor);
}


/*
  This script handles the admin dashboard functionality for managing doctors:
  - Loads all doctor cards
  - Filters doctors by name, time, or specialty
  - Adds a new doctor via modal form


  Attach a click listener to the "Add Doctor" button
  When clicked, it opens a modal form using openModal('addDoctor')


  When the DOM is fully loaded:
    - Call loadDoctorCards() to fetch and display all doctors


  Function: loadDoctorCards
  Purpose: Fetch all doctors and display them as cards

    Call getDoctors() from the service layer
    Clear the current content area
    For each doctor returned:
    - Create a doctor card using createDoctorCard()
    - Append it to the content div

    Handle any fetch errors by logging them


  Attach 'input' and 'change' event listeners to the search bar and filter dropdowns
  On any input change, call filterDoctorsOnChange()


  Function: filterDoctorsOnChange
  Purpose: Filter doctors based on name, available time, and specialty

    Read values from the search bar and filters
    Normalize empty values to null
    Call filterDoctors(name, time, specialty) from the service

    If doctors are found:
    - Render them using createDoctorCard()
    If no doctors match the filter:
    - Show a message: "No doctors found with the given filters."

    Catch and display any errors with an alert


  Function: renderDoctorCards
  Purpose: A helper function to render a list of doctors passed to it

    Clear the content area
    Loop through the doctors and append each card to the content area


  Function: adminAddDoctor
  Purpose: Collect form data and add a new doctor to the system

    Collect input values from the modal form
    - Includes name, email, phone, password, specialty, and available times

    Retrieve the authentication token from localStorage
    - If no token is found, show an alert and stop execution

    Build a doctor object with the form values

    Call saveDoctor(doctor, token) from the service

    If save is successful:
    - Show a success message
    - Close the modal and reload the page

    If saving fails, show an error message
*/
