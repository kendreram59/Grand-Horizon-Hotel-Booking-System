
package com.hotelbooking.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.hotelbooking.entity.Booking;
import com.hotelbooking.service.BookingService;



@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingService bookingService;

    // Constructor Injection
    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    // Add Booking
    @PostMapping
    public Booking addBooking(@RequestBody Booking booking) {
        return bookingService.saveBooking(booking);
    }

    // Get All Bookings
    @GetMapping
    public List<Booking> getAllBookings() {
        return bookingService.getAllBookings();
    }

    // Get Booking By ID
    @GetMapping("/{id}")
    public Booking getBookingById(@PathVariable Long id) {
        return bookingService.getBookingById(id);
    }

    // Confirm Booking
    @PutMapping("/confirm/{id}")
    public Booking confirmBooking(@PathVariable Long id) {
        return bookingService.confirmBooking(id);
    }

    // Cancel Booking
    @PutMapping("/cancel/{id}")
    public Booking cancelBooking(@PathVariable Long id) {
        return bookingService.cancelBooking(id);
    }

    // Get Bookings By Customer Email
    @GetMapping("/customer")
    public List<Booking> getBookingsByCustomerEmail(
            @RequestParam String email) {
        return bookingService.getBookingsByCustomerEmail(email);
    }

    // Update Booking
    @PutMapping("/{id}")
    public Booking updateBooking(
            @PathVariable Long id,
            @RequestBody Booking booking) {
        return bookingService.updateBooking(id, booking);
    }
}
