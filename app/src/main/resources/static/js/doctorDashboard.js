// doctorDashboard.js

// Import getAllAppointments to fetch appointments from the backend
// Import createPatientRow to generate a table row for each patient appointment
import { getAllAppointments } from './services/appointmentRecordService.js';
import { createPatientRow } from './components/patientRows.js';

// Get the table body where patient rows will be added
// Initialize selectedDate with today's date in 'YYYY-MM-DD' format
// Get the saved token from localStorage (used for authenticated API calls)
// Initialize patientName to null (used for filtering by name)

const tableBody = document.getElementById("patientTableBody");
const token = localStorage.getItem("token");

var selectedDate = new Date();
console.log(selectedDate);

let patientName = null;

document.getElementById("searchBar").addEventListener("change", patientName);

async function patientName() {
    const searchBarValue = document.getElementById("searchBar").value.trim();
    
    const patientName = searchBarValue || null;
    return patientName;
  }

// Initialize selectedDate variable globally or within your module
let selectedDate = new Date().toISOString().split('T')[0]; // Format: YYYY-MM-DD

// Function to load appointments - assumed to be defined elsewhere
// async function loadAppointments() { ... }

// 5.1. Bind event listener to "Today's Appointments" button
document.getElementById('todayButton').addEventListener('click', () => {
  // Reset selectedDate to today
  selectedDate = new Date().toISOString().split('T')[0];
  
  // Update the date picker value to today's date
  const datePicker = document.getElementById('datePicker');
  if (datePicker) {
    datePicker.value = selectedDate;
  }
  
  // Call loadAppointments to refresh the appointment list
  loadAppointments();
});

// 5.2. Bind event listener to date picker input
document.getElementById('datePicker').addEventListener('change', (event) => {
  // Update selectedDate with the picked date
  selectedDate = event.target.value;
  
  // Call loadAppointments to fetch and display appointments for the selected date
  loadAppointments();
});

async function loadAppointments() {
  const tableBody = document.getElementById('patientTableBody');
  const token = localStorage.getItem('token'); // Assuming token is stored here
  const patientName = null; // Or set if you have a search/filter input
  // Clear existing table content
  tableBody.innerHTML = '';

  try {
    // Fetch appointments for the selected date
    const appointments = await getAllAppointments(selectedDate, patientName, token);

    if (!appointments || appointments.length === 0) {
      // No appointments found - show message row
      const noDataRow = document.createElement('tr');
      noDataRow.innerHTML = `<td colspan="6" class="text-center">No Appointments found for today.</td>`;
      tableBody.appendChild(noDataRow);
      return;
    }

    // Appointments found - create rows for each
    appointments.forEach(appointment => {
      const patientRow = createPatientRow(appointment);
      tableBody.appendChild(patientRow);
    });

  } catch (error) {
    console.error('Error loading appointments:', error);
    // Show error message row in the table
    const errorRow = document.createElement('tr');
    errorRow.innerHTML = `<td colspan="6" class="text-center text-danger">Error loading appointments. Please try again later.</td>`;
    tableBody.appendChild(errorRow);
  }
}

loadAppointments();

document.addEventListener('DOMContentLoaded', () => {
  // Call renderContent() if you have this function defined
  if (typeof renderContent === 'function') {
    renderContent();
  }

  // Set selectedDate to today’s date in YYYY-MM-DD format
  selectedDate = new Date().toISOString().split('T')[0];

  // Call loadAppointments() to load today's appointments by default
  loadAppointments();
});


// LM:  points 6,7,8 to be completed

/*
  Import getAllAppointments to fetch appointments from the backend
  Import createPatientRow to generate a table row for each patient appointment


  Get the table body where patient rows will be added
  Initialize selectedDate with today's date in 'YYYY-MM-DD' format
  Get the saved token from localStorage (used for authenticated API calls)
  Initialize patientName to null (used for filtering by name)


  Add an 'input' event listener to the search bar
  On each keystroke:
    - Trim and check the input value
    - If not empty, use it as the patientName for filtering
    - Else, reset patientName to "null" (as expected by backend)
    - Reload the appointments list with the updated filter


  Add a click listener to the "Today" button
  When clicked:
    - Set selectedDate to today's date
    - Update the date picker UI to match
    - Reload the appointments for today


  Add a change event listener to the date picker
  When the date changes:
    - Update selectedDate with the new value
    - Reload the appointments for that specific date


  Function: loadAppointments
  Purpose: Fetch and display appointments based on selected date and optional patient name

  Step 1: Call getAllAppointments with selectedDate, patientName, and token
  Step 2: Clear the table body content before rendering new rows

  Step 3: If no appointments are returned:
    - Display a message row: "No Appointments found for today."

  Step 4: If appointments exist:
    - Loop through each appointment and construct a 'patient' object with id, name, phone, and email
    - Call createPatientRow to generate a table row for the appointment
    - Append each row to the table body

  Step 5: Catch and handle any errors during fetch:
    - Show a message row: "Error loading appointments. Try again later."


  When the page is fully loaded (DOMContentLoaded):
    - Call renderContent() (assumes it sets up the UI layout)
    - Call loadAppointments() to display today's appointments by default
*/
