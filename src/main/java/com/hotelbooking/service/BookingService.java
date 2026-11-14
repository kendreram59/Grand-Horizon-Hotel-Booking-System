
package com.hotelbooking.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.stereotype.Service;

import com.hotelbooking.entity.Booking;
import com.hotelbooking.entity.Room;
import com.hotelbooking.repository.BookingRepository;
import com.hotelbooking.repository.RoomRepository;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final RoomRepository roomRepository;

    public BookingService(BookingRepository bookingRepository,
                          RoomRepository roomRepository) {
        this.bookingRepository = bookingRepository;
        this.roomRepository = roomRepository;
    }

    // CUSTOMER VALIDATION
    private void validateCustomer(Booking booking) {

        if (booking.getCustomerName() == null ||
                booking.getCustomerName().trim().isEmpty()) {
            throw new RuntimeException("Customer name is required");
        }

        if (booking.getCustomerEmail() == null ||
                booking.getCustomerEmail().trim().isEmpty()) {
            throw new RuntimeException("Customer email is required");
        }

        String email = booking.getCustomerEmail().trim();

        if (!email.matches(
                "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            throw new RuntimeException("Invalid email format");
        }

        String mobile = booking.getCustomerMobile();

        if (mobile == null || !mobile.trim().matches("[0-9]{10}")) {
            throw new RuntimeException(
                    "Mobile number must contain exactly 10 digits");
        }

        booking.setCustomerName(booking.getCustomerName().trim());
        booking.setCustomerEmail(email);
        booking.setCustomerMobile(mobile.trim());
    }

    // ROOM VALIDATION
    private Room getRoom(Booking booking) {

        if (booking.getRoom() == null ||
                booking.getRoom().getId() <= 0) {
            throw new RuntimeException("Room ID is required");
        }

        Room room = roomRepository.findById(booking.getRoom().getId())
                .orElseThrow(() -> new RuntimeException("Room not found"));

        if (!room.isAvailable()) {
            throw new RuntimeException("Room is not available");
        }

        return room;
    }

    // SAVE BOOKING
    public Booking saveBooking(Booking booking) {

        validateCustomer(booking);

        if (booking.getCheckInDate() == null) {
            throw new RuntimeException("Check-in date is required");
        }

        if (booking.getCheckInDate().isBefore(LocalDate.now())) {
            throw new RuntimeException("Check-in date cannot be in the past");
        }

        Room room = getRoom(booking);

        String type = room.getPriceType();

        if (type == null) {
            throw new RuntimeException("Room price type is missing");
        }

        type = type.trim().toUpperCase();

        if ("NIGHT".equals(type)) {
            validateNightBooking(booking, room, null);
        } else if ("HOUR".equals(type) || "HOURLY".equals(type)) {
            validateHourlyBooking(booking, room, null);
        } else {
            throw new RuntimeException("Price type must be NIGHT or HOUR");
        }

        booking.setRoom(room);
        booking.setStatus("PENDING");

        return bookingRepository.save(booking);
    }

    // NIGHT BOOKING
    private void validateNightBooking(
            Booking booking, Room room, Long bookingId) {

        LocalDate checkIn = booking.getCheckInDate();
        LocalDate checkOut = booking.getCheckOutDate();

        if (checkOut == null) {
            throw new RuntimeException("Check-out date is required");
        }

        if (!checkOut.isAfter(checkIn)) {
            throw new RuntimeException(
                    "Check-out date must be after check-in date");
        }

        List<Booking> conflicts =
                bookingRepository.findOverlappingBookings(
                        room.getId(), checkIn, checkOut, bookingId);

        if (!conflicts.isEmpty()) {
            throw new RuntimeException(
                    "Room is already booked for the selected dates");
        }

        long days = ChronoUnit.DAYS.between(checkIn, checkOut);

        booking.setCheckInTime(null);
        booking.setCheckOutTime(null);
        booking.setTotalPrice(room.getPricePerNight() * days);
    }

    // HOURLY BOOKING
    private void validateHourlyBooking(
            Booking booking, Room room, Long bookingId) {

        LocalDate date = booking.getCheckInDate();

        LocalTime start = booking.getCheckInTime();
        LocalTime end = booking.getCheckOutTime();

        if (start == null) {
            throw new RuntimeException("Check-in time is required");
        }

        if (end == null) {
            throw new RuntimeException("Check-out time is required");
        }

        if (!end.isAfter(start)) {
            throw new RuntimeException(
                    "Check-out time must be after check-in time");
        }

        long minutes = ChronoUnit.MINUTES.between(start, end);

        if (minutes < 60 || minutes > 240 || minutes % 60 != 0) {
            throw new RuntimeException(
                    "Duration must be 1, 2, 3 or 4 hours");
        }

        if (date.equals(LocalDate.now()) &&
                start.isBefore(LocalTime.now())) {
            throw new RuntimeException(
                    "Check-in time cannot be in the past");
        }

        booking.setCheckOutDate(date);

        List<Booking> conflicts =
                bookingRepository.findOverlappingHourlyBookings(
                        room.getId(), date, start, end, bookingId);

        if (!conflicts.isEmpty()) {
            throw new RuntimeException(
                    "Room is already booked for the selected time");
        }

        long hours = minutes / 60;

        booking.setTotalPrice(room.getPricePerNight() * hours);
    }

    // GET ALL BOOKINGS
    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    // GET BOOKING BY ID
    public Booking getBookingById(Long id) {
        return bookingRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Booking not found with ID: " + id));
    }

    // CONFIRM BOOKING
    public Booking confirmBooking(Long id) {

        Booking booking = getBookingById(id);

        if ("CONFIRMED".equals(booking.getStatus())) {
            throw new RuntimeException("Booking is already confirmed");
        }

        if ("CANCELLED".equals(booking.getStatus())) {
            throw new RuntimeException(
                    "Cancelled booking cannot be confirmed");
        }

        booking.setStatus("CONFIRMED");

        return bookingRepository.save(booking);
    }

    // CANCEL BOOKING
    public Booking cancelBooking(Long id) {

        Booking booking = getBookingById(id);

        if ("CANCELLED".equals(booking.getStatus())) {
            throw new RuntimeException("Booking is already cancelled");
        }

        booking.setStatus("CANCELLED");

        return bookingRepository.save(booking);
    }

    // BOOKINGS BY CUSTOMER EMAIL
    public List<Booking> getBookingsByCustomerEmail(String email) {
        return bookingRepository.findByCustomerEmail(email);
    }

    // UPDATE BOOKING
    public Booking updateBooking(Long id, Booking updatedBooking) {

        Booking oldBooking = getBookingById(id);

        if ("CANCELLED".equals(oldBooking.getStatus())) {
            throw new RuntimeException("Cancelled booking cannot be updated");
        }

        validateCustomer(updatedBooking);

        if (updatedBooking.getCheckInDate() == null) {
            throw new RuntimeException("Check-in date is required");
        }

        if (updatedBooking.getCheckInDate().isBefore(LocalDate.now())) {
            throw new RuntimeException("Check-in date cannot be in the past");
        }

        Room room = oldBooking.getRoom();

        oldBooking.setCustomerName(updatedBooking.getCustomerName());
        oldBooking.setCustomerEmail(updatedBooking.getCustomerEmail());
        oldBooking.setCustomerMobile(updatedBooking.getCustomerMobile());
        oldBooking.setCheckInDate(updatedBooking.getCheckInDate());
        oldBooking.setCheckOutDate(updatedBooking.getCheckOutDate());
        oldBooking.setCheckInTime(updatedBooking.getCheckInTime());
        oldBooking.setCheckOutTime(updatedBooking.getCheckOutTime());

        String type = room.getPriceType().trim().toUpperCase();

        if ("NIGHT".equals(type)) {
            validateNightBooking(oldBooking, room, id);
        } else if ("HOUR".equals(type) || "HOURLY".equals(type)) {
            validateHourlyBooking(oldBooking, room, id);
        } else {
            throw new RuntimeException("Invalid room price type");
        }

        return bookingRepository.save(oldBooking);
    }
}
