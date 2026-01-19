# QuickExam – Online Exam System (Jakarta EE)

## Project Overview
**QuickExam** is an online exam system developed using **Jakarta EE** and deployed on **WildFly**.  
The application allows users to create, manage, and take online exams in a secure and user-friendly environment.  
It uses **session-based authentication**, **clean URLs**, **email services**, and a modern UI built with **Tailwind CSS**.

---

## Technologies Used

### Backend
- **Jakarta EE**
- **WildFly Application Server**
- **JPA (Jakarta Persistence API)**
- **MySQL** database
- **JSF (Jakarta Faces)**
- **HttpSession** for storing the current authenticated user

### Frontend
- **JSF (Faces)**
- **Tailwind CSS** for responsive and modern design

### Web & Routing
- **RewriteConfiguration**
    - From `org.ocpsoft.rewrite.annotation.RewriteConfiguration`
    - Used to implement clean and SEO-friendly URLs.

- **WebFilter**
    - Authentication and authorization
    - Role-based access control
    - Session validation
    - Page protection

---

## Architecture
- **MVC Architecture**
    - **Model:** JPA Entities
    - **View:** JSF pages styled with Tailwind CSS
    - **Controller:** JSF Managed Beans
- **Session-based authentication** for user management

---

## Services
- **HashService**
    - Password hashing and verification

- **EmailService**
    - Sends system emails (verification, invitations, codes)

- **Code Generator Service**
    - Generates email validation codes
    - Generates exam session access codes

---

# QuickExam Features

## Authentication & Users
- User registration and login
- Email verification using a generated validation code
- Session-based user management
- Role-based access control

## Exam Management
- Create and manage exams
- Randomized question selection
- Configurable number of displayed questions
- Exam session access using unique codes

### Example
- An exam contains **20 questions**
- Only **4 questions** are displayed
- The system randomly selects **4 questions** for each attempt

## Exam Display
- If an **Admin** creates an exam, **“Created by”** displays **QuickExam**
- If a **Creator** creates an exam, their name appears in **“Created by”**

## Email System
Automatic emails are sent for:
- Exam registration
- Email verification
- Exam session code
- Requests to become a Creator

## User Roles
- **Candidate (Student):** takes exams and views results
- **Creator:** creates and manages exams
- **Admin:** manages the platform

## Clean URLs
SEO-friendly URLs implemented using RewriteConfiguration:
- `/access-exam`
- `/take-exam`
- `/creator-dashboard/my-exams`

## Exam Retake
- Users can receive a new invitation if an exam is missed or closed

## Results & Reports
- View exam results
- Display final score
- Display total points achieved

## Error Management
- **404 – Not Found**
- **403 – Forbidden**
- **500 – Internal Server Error**

---

## Security
- Secure password hashing via HashService
- Session validation using WebFilter
- Restricted access based on user roles

---

## Deployment
- Deployed on **WildFly**
- Connected to **MySQL**

---

## Conclusion
**QuickExam** is a full-featured Jakarta EE web application demonstrating clean architecture, session-based security, email workflows, and modern UI design using Tailwind CSS, suitable for academic and real-world use.

## QuickExam Demo

You can watch the QuickExam website demo here:

[Watch Video](https://isgacloud-my.sharepoint.com/:v:/g/personal/youssef_aouani_edu_isga_ma/IQBCkqOeCGiMSLwb03DHGwj6AYblsQZTjhDWQU4p30-VGxE?nav=eyJyZWZlcnJhbEluZm8iOnsicmVmZXJyYWxBcHAiOiJPbmVEcml2ZUZvckJ1c2luZXNzIiwicmVmZXJyYWxBcHBQbGF0Zm9ybSI6IldlYiIsInJlZmVycmFsTW9kZSI6InZpZXciLCJyZWZlcnJhbFZpZXciOiJNeUZpbGVzTGlua0NvcHkifX0&e=pC9owk)
