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
