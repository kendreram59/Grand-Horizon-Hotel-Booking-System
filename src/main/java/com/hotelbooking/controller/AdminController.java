
package com.hotelbooking.controller;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Map;
import java.util.HashMap;
import java.util.List;

import jakarta.servlet.http.HttpSession;

import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import com.hotelbooking.entity.Admin;
import com.hotelbooking.entity.Booking;
import com.hotelbooking.repository.AdminRepository;
import com.hotelbooking.repository.RoomRepository;
import com.hotelbooking.repository.BookingRepository;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoomRepository roomRepository;
    private final BookingRepository bookingRepository;

    public AdminController(
            AdminRepository adminRepository,
            PasswordEncoder passwordEncoder,
            RoomRepository roomRepository,
            BookingRepository bookingRepository) {

        this.adminRepository = adminRepository;
        this.passwordEncoder = passwordEncoder;
        this.roomRepository = roomRepository;
        this.bookingRepository = bookingRepository;
    }

    // ADMIN LOGIN
    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody Map<String, String> data,
            HttpSession session) {

        String username = data.get("username");
        String password = data.get("password");

        Admin admin = adminRepository.findByUsername(username)
                .orElse(null);

        if (admin == null ||
                !passwordEncoder.matches(password, admin.getPassword())) {

            return ResponseEntity.status(401)
                    .body("Invalid username or password");
        }

        session.setAttribute("adminId", admin.getId());
        session.setAttribute("adminUsername", admin.getUsername());
        session.setAttribute("adminRole", admin.getRole());

        return ResponseEntity.ok("Admin login successful");
    }

    // ADMIN LOGOUT
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpSession session) {

        session.invalidate();

        return ResponseEntity.ok("Admin logout successful");
    }

    // ADMIN DASHBOARD
    @GetMapping("/dashboard")
    public Map<String, Long> getDashboard(HttpSession session) {

        checkAdminLogin(session);

        Map<String, Long> dashboard = new HashMap<>();

        dashboard.put("totalRooms", roomRepository.count());
        dashboard.put("availableRooms",
                roomRepository.countByAvailableTrue());
        dashboard.put("totalBookings", bookingRepository.count());
        dashboard.put("totalCustomers",
                bookingRepository.countDistinctCustomers());

        return dashboard;
    }

    // GET ALL BOOKINGS
    @GetMapping("/bookings")
    public List<Booking> getAllBookings(HttpSession session) {

        checkAdminLogin(session);

        return bookingRepository.findAll();
    }

    // NEW: DATE-WISE BOOKINGS
    @GetMapping("/bookings/date")
    public List<Booking> getBookingsByDate(
            @RequestParam String date,
            HttpSession session) {

        checkAdminLogin(session);

        try {
            LocalDate selectedDate = LocalDate.parse(date);

            return bookingRepository.findByCheckInDate(selectedDate);

        } catch (Exception e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid date. Use YYYY-MM-DD format");
        }
    }

    // NEW: DAY-WISE BOOKINGS
    @GetMapping("/bookings/day")
    public List<Booking> getBookingsByDay(
            @RequestParam String day,
            HttpSession session) {

        checkAdminLogin(session);

        try {
            DayOfWeek selectedDay =
                    DayOfWeek.valueOf(day.toUpperCase());

            LocalDate today = LocalDate.now();

            LocalDate startDate = today.with(
                    java.time.temporal.TemporalAdjusters
                            .previousOrSame(selectedDay));

            LocalDate endDate = startDate.plusDays(6);

            return bookingRepository.findAll().stream()
                    .filter(booking ->
                            booking.getCheckInDate() != null &&
                            booking.getCheckInDate().getDayOfWeek()
                                    == selectedDay)
                    .toList();

        } catch (Exception e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid day. Use MONDAY, TUESDAY, etc.");
        }
    }

    // CONFIRM BOOKING
    @PutMapping("/bookings/confirm/{id}")
    public ResponseEntity<?> confirmBooking(
            @PathVariable Long id,
            HttpSession session) {

        checkAdminLogin(session);

        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Booking not found"));

        if ("CANCELLED".equalsIgnoreCase(booking.getStatus())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Cancelled booking cannot be confirmed");
        }

        if ("CONFIRMED".equalsIgnoreCase(booking.getStatus())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Booking is already confirmed");
        }

        booking.setStatus("CONFIRMED");
        bookingRepository.save(booking);

        return ResponseEntity.ok("Booking confirmed successfully");
    }

    // CANCEL BOOKING
    @PutMapping("/bookings/cancel/{id}")
    public ResponseEntity<?> cancelBooking(
            @PathVariable Long id,
            HttpSession session) {

        checkAdminLogin(session);

        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Booking not found"));

        if ("CANCELLED".equalsIgnoreCase(booking.getStatus())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Booking is already cancelled");
        }

        booking.setStatus("CANCELLED");
        bookingRepository.save(booking);

        return ResponseEntity.ok("Booking cancelled successfully");
    }

    // CHECK ADMIN LOGIN
    private void checkAdminLogin(HttpSession session) {

        if (session.getAttribute("adminId") == null) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Please login first");
        }
    }
}
