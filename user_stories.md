# User Story Template

**Title:**
_As a [user role], I want [feature/goal], so that [reason]._

**Acceptance Criteria:**
1. [Criteria 1]
2. [Criteria 2]
3. [Criteria 3]

**Priority:** [High/Medium/Low]
**Story Points:** [Estimated Effort in Points]
**Notes:**
- [Additional information or edge cases]

## Admin User Stories

### Admin User Story 1

**Title:**
As an Admin, I want to log into the portal with my username and password to manage the platform securely.

**Acceptance Criteria:**
1. Make sure that admin sections are allowed only to Admin and not to the users
2. Use a password of min 8 chatacters with at least 1 symbol
3. Be requested to automatically update the password every 2 months

**Priority:** High
**Story Points:** 5
**Notes:**
- Consider applying Two-Factor Authentication (2FA).


### Admin User Story 2

**Title:**
As an Admin, I want to log out of the portal to protect system access.

**Acceptance Criteria:**
1. After exiting, make sure that admin sections are no more accessible
2. Include a message that recommends closing the browser after havign logged out

**Priority:** High
**Story Points:** 5
**Notes:**
- N/A


### Admin User Story 3

**Title:**
As an Admin, I want to add doctors to the portal.

**Acceptance Criteria:**
1. Demonstrate that after adding a new doctor profile via the User Interface, the item is also added in the MySQL database for persistance.
2. Add a confirmation message of the added doctor.

**Priority:** Medium
**Story Points:** 8
**Notes:**
- Make sure that the doctor schedule can be inserted.


### Admin User Story 4

**Title:**
As an Admin, I want to delete doctor's profile from the portal.

**Acceptance Criteria:**
1. Demonstrate that after deleting the doctor profile via the User Interface, the item is also deleted in the MySQL database.
2. Add a confirmation message of the deleted doctor.

**Priority:** Medium
**Story Points:** 8
**Notes:**
- N/A.


### Admin User Story 5

**Title:**
As an Admin, I want to run a stored procedure in MySQL CLI to get the number of appointments per month and track usage statistics.

**Acceptance Criteria:**
1. Display the calculated statistics in a dashboard.
2. Allow comparison of statistics across different months (current vs previous month and current vs previous year).

**Priority:** Medium
**Story Points:** 13
**Notes:**
- Use CSS for style so that it is flexible to change in style we may want to apply (like Christmas style in December).


## Patient User Stories

### Partient User Story 1

**Title:**
As a Patient, I want to view a list of doctors without logging in to explore options before registering.

**Acceptance Criteria:**
1. Allow the selection of doctors based on their specialization.
2. Display the CV and expertise of the  selected doctor.

**Priority:** High
**Story Points:** 5
**Notes:**
- Allow possibility to include doctor picture in the profile.


### Partient User Story 2

**Title:**
As a Patient, I want to sign up using email and password to book appointments.

**Acceptance Criteria:**
1. Receive a confirmation message of the access to the site.

**Priority:** Medium
**Story Points:** 3
**Notes:**
- N/A.


### Partient User Story 3

**Title:**
As a Patient, I want to log into the portal to manage your bookings.

**Acceptance Criteria:**
1. Display all my bookings when logging into the portal.

**Priority:** Medium
**Story Points:** 3
**Notes:**
- N/A.


### Partient User Story 4

**Title:**
As a Patient, I want to log in and book an hour-long appointment to consult with a doctor.

**Acceptance Criteria:**
1. Allow to visualize the Doctor's available slots and to book the preferred one.

**Priority:** Medium
**Story Points:** 8
**Notes:**
- N/A.


### Partient User Story 5

**Title:**
As a Patient, I want to view my upcoming appointments so that I can prepare accordingly.

**Acceptance Criteria:**
1. Receive an email one week, three days and the day before the appointment.
2. Include in the email if there are particular documents to bring to the appointment.

**Priority:** Low
**Story Points:** 13
**Notes:**
- N/A.


## Doctor User Stories

### Doctor User Story 1

**Title:**
As an Doctor, I want to Log into the portal to manage my appointments.

**Acceptance Criteria:**
1. Display the appointments using Agenda format, including the name and contacs of the patient.

**Priority:** High
**Story Points:** 5
**Notes:**
- N/A.


### Doctor User Story 2

**Title:**
As an Doctor, I want to Log out of the portal to protect my data.

**Acceptance Criteria:**
1. After exiting the portal, display confirmation message that the log out has been accomplished.

**Priority:** High
**Story Points:** 3
**Notes:**
- Verify that data are effectively protected after log out.


### Doctor User Story 3

**Title:**
As an Doctor, I want to View my appointment calendar to stay organized.

**Acceptance Criteria:**
1. Send a notification email when there are new appointments.
2. In the notification email, include the link to updated calendar.

**Priority:** High
**Story Points:** 8
**Notes:**
- N/A.


### Doctor User Story 4

**Title:**
As an Doctor, I want to Mark my unavailability to show patients only the available slots.

**Acceptance Criteria:**
1. The unavilable time slots should not be offered to Patients to book appointments.

**Priority:** High
**Story Points:** 13
**Notes:**
- use clear color coding to highilight unavailable slots.


### Doctor User Story 5

**Title:**
As an Doctor, I want to Update my profile with specialization and contact information so that patients have up-to-date information.

**Acceptance Criteria:**
1. Create a dedicated section where the profile can be updated.
2. Once the profile is updated it should be visible to all patients.

**Priority:** Low
**Story Points:** 3
**Notes:**
- N/A.
