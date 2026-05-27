GST Billing & Invoice Management System

A secure Spring Boot based GST Billing and Invoice Management REST API application with JWT Authentication, invoice management, email invoice functionality, search/filter operations, and Swagger API documentation.

🚀 Features
JWT Authentication & Authorization
User Registration & Login
Generate New GST Bills
Update Existing Bills
Delete Bills
Get Bill By Invoice ID
Search Bills Using Multiple Inputs
Sort Bills
Filter Bills By Dates
Generate GST Reports
Calculate Total GST Summary
Email Invoice with PDF Attachment
Swagger UI API Documentation
MySQL Database Integration
🛠️ Tech Stack
Java
Spring Boot
Spring Security
JWT
MySQL
Maven
Swagger / OpenAPI
Java Mail Sender
📁 Project Structure
src
└── main
    └── java
        └── com.fullstack.gstbillingwithinvoicemngmt
            ├── config
            │   ├── JWTFilter
            │   └── SecurityConfig
            │
            ├── controller
            │   ├── AuthController
            │   └── GSTBillController
            │
            ├── dao
            │   ├── impl
            │   ├── GSTBilldao
            │   └── UserInfodao
            │
            ├── dto
            ├── exception
            ├── model
            ├── repository
            │
            ├── service
            │   ├── impl
            │   │   ├── GSTBillServiceImpl
            │   │   └── UserInfoServiceImpl
            │   │
            │   ├── GSTBillService
            │   └── UserInfoService
            │
            ├── util
            │
            └── GstbillingwithinvoicemngmtApplication
🔐 Authentication APIs
Method	Endpoint	Description
POST	/v1/auth/register	Register New User
POST	/v1/auth/signin	User Login
📄 GST Billing APIs
Method	Endpoint	Description
POST	/v1/gstbills/	Generate New GST Bill
GET	/v1/gstbills/{invoiceId}	Get Bill By ID
PUT	/v1/gstbills/{invoiceId}	Update GST Bill
DELETE	/v1/gstbills/{invoiceId}	Delete Bill
GET	/v1/gstbills/	Get All Bills
GET	/v1/gstbills/sort	Sort Bills
GET	/v1/gstbills/search/{input}	Search Bills
GET	/v1/gstbills/filter	Filter Bills By Dates
GET	/v1/gstbills/report	Generate GST Report
GET	/v1/gstbills/all-totals	Get All Total Amounts
POST	/v1/gstbills/mail-invoice/{invoiceId}	Send Invoice Through Email
📬 Invoice Email Functionality

The application supports sending invoice PDFs directly through email using multipart file upload.

Workflow
Create GST Bill
Upload Invoice PDF
Send Invoice to Customer Email
Customer Receives Email with PDF Attachment
📷 Swagger API Documentation

The application includes Swagger UI for testing all REST APIs.

Swagger Home

📷 Email Invoice API Testing

Invoice email functionality tested successfully using multipart/form-data upload in Swagger UI.

📷 Successful Invoice Mail Response

Successfully sent invoice attachment through email.

🔎 Search Functionality

Search bills using:

Invoice ID
Customer Name
Email
Contact Number
Batch
Invoice Date
📊 Report Features
GST Summary Reports
Total Amount Calculation
CGST & SGST Totals
Date-wise Report Generation
🔒 Security Features
JWT Based Authentication
Protected APIs
Secure Endpoint Access
Authentication Filter Integration
▶️ Run Application
mvn spring-boot:run
📌 Swagger URL
http://localhost:8080/swagger-ui/index.html
👨‍💻 Author

Jyoti Sathe
- GitHub: https://github.com/JyotiSathe17
- LinkedIn: https://www.linkedin.com/in/jyoti-sathe-00431329a
