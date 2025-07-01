# Section 1: Architecture summary

*Smart Clinic Management System*
This Spring Boot application uses both MVC and REST controllers. Thymeleaf templates are used for the Admin and Doctor dashboards, while REST APIs serve all other modules. 
The application interacts with two databases—MySQL (for patient, doctor, appointment, and admin data) and MongoDB (for prescriptions). All controllers route requests through a common service layer, which in turn delegates to the appropriate repositories. 
MySQL uses JPA entities while MongoDB uses document models.


# Section 2: Numbered flow of data and control

1. User accesses AdminDashboard or Appointment pages.
2. The action is routed to the appropriate Thymeleaf or REST controller.
3. The controller calls the **Service Layer**, which acts as the hearth of the backend system.
4. The Service Layer communicates with the **Repository Layer** to perform data access operations. Repositories abstract the database access logic and expose a simple, declarative iterface for fetching and persisting data.
5. Each repositories (the MySQL and the MongoDB one) interfaces directly with the underlying database engine.
6. Once the dada is retrieved from the database, it is mapped into Java model classes that the application can work with. This process is known as **model binding**.
7. Finally the bound models are used in the responde layer: in *MVC flows*, the models are passed from the controller to Thymeleaf templates; in *REST flows*, the same models are serialized into JSON and sent back as part of an HTTP response.
 
