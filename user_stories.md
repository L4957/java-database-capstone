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

