# 🏨 Grand Horizon – Hotel Booking System

A Hotel Booking Management System developed using **Java, Spring Boot, Spring Data JPA, Hibernate, and MySQL**.

Grand Horizon provides a simple and user-friendly platform for customers to explore hotel rooms and make bookings. It also provides an admin dashboard to manage rooms and bookings.

## 🚀 Project Overview

Grand Horizon is a web-based hotel booking application designed to simplify hotel room management and reservation operations.

## ✨ Features

### Customer Features
- View available hotel rooms.
- Explore room details and room images.
- Book hotel rooms.
- Support for hourly and night-based bookings.
- Enter customer details during booking.
- View booking information.
- Cancel bookings.

### Admin Features
- Admin dashboard.
- Add new rooms.
- Update room details.
- Manage room availability.
- View all bookings.
- Confirm bookings.
- Cancel bookings.
- Manage hotel room information.

## 🛠️ Technologies Used

| Technology | Purpose |
|---|---|
| Java 17 | Backend Programming |
| Spring Boot | Backend Framework |
| Spring Data JPA | Database Operations |
| Hibernate | ORM |
| MySQL | Database |
| HTML5 | Frontend Structure |
| CSS3 | UI Design and Animations |
| JavaScript | Frontend Functionality |
| Maven | Dependency Management |
| Eclipse IDE | Development Environment |
| Git & GitHub | Version Control |

## 📂 Project Structure

```text
Grand-Horizon-Hotel-Booking-System
│
├── src
│   └── main
│       ├── java
│       │   └── com.hotelbooking
│       │       ├── controller
│       │       ├── entity
│       │       ├── repository
│       │       ├── service
│       │       └── HotelBookingSystemApplication.java
│       │
│       └── resources
│           ├── static
│           │   ├── images
│           │   ├── index.html
│           │   └── admin.html
│           └── application.properties
│
├── pom.xml
└── README.md
```

## ⚙️ How to Run the Project

1. Install Java 17 or compatible JDK.
2. Install Eclipse IDE.
3. Install MySQL.
4. Clone this repository:

```bash
git clone https://github.com/kendreram59/Grand-Horizon-Hotel-Booking-System.git
```

5. Create a MySQL database:

```sql
CREATE DATABASE hotel_booking_db;
```

6. Configure your database username and password in `application.properties`.
7. Import the project into Eclipse as an Existing Maven Project.
8. Update Maven dependencies.
9. Run `HotelBookingSystemApplication.java` as a Spring Boot Application.

Open the application:

```text
http://localhost:8080
```

## 🔗 REST API Endpoints

### Room APIs

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/rooms` | Add room |
| GET | `/api/rooms` | Get all rooms |
| PUT | `/api/rooms/{id}` | Update room |
| DELETE | `/api/rooms/{id}` | Delete room |

### Booking APIs

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/bookings` | Create booking |
| GET | `/api/bookings` | Get all bookings |
| GET | `/api/bookings/{id}` | Get booking by ID |
| PUT | `/api/bookings/{id}` | Update booking |
| PUT | `/api/bookings/confirm/{id}` | Confirm booking |
| PUT | `/api/bookings/cancel/{id}` | Cancel booking |
| GET | `/api/bookings/customer?email={email}` | Get customer bookings |

## 🎯 Project Objective

The main objective of this project is to develop a hotel booking application using Java Backend technologies and understand real-world concepts such as REST APIs, CRUD operations, database connectivity, and layered architecture.

## 👨‍💻 Developer

**Rameshwar Kendre**

Java Backend Developer

GitHub: [kendreram59](https://github.com/kendreram59)

---

⭐ If you like this project, don't forget to give it a star!
