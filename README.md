# SecureVault – Secure File Storage and File Transfer

SecureVault is a backend-focused Spring Boot application built to securely store and manage user files with strong authentication, authorization, and encryption mechanisms.

## Features
- User authentication and role-based authorization using Spring Security
- Secure file upload and download
- OTP-based forgot password using email (SMTP)
- AES-GCM encryption for sensitive data
- Scheduled job to detect 1-year inactive users and transfer access to a trusted user
- Clean layered architecture (Controller, Service, Repository, DTO, Entity)

## Tech Stack
- Java
- Spring Boot
- Spring Security
- Spring Data JPA
- MySQL
- Maven

## Project Status
Core backend features completed. Deployment planned as next enhancement.

## Screenshots
*Captured from local development environment.*

### Login & Authentication
<img width="1920" height="1080" alt="Screenshot (334)" src="https://github.com/user-attachments/assets/5f872364-d05d-4922-b6e3-729b6df62c7a" />


### File Upload & Management
<img width="1920" height="1080" alt="Screenshot (340)" src="https://github.com/user-attachments/assets/d57979f9-4d6e-4d73-aa2f-3c79a8bdad68" />


### Secure File Sharing with OTP
![Share OTP](screenshots/share-otp.png)Screenshot (345)

### OTP Email Verification
![OTP Email](screenshots/otp-email.png)

### Trusted User Access (After 1-Year Inactivity)
![Trusted Files](screenshots/trusted-files.png)
