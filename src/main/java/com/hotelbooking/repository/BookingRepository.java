
package com.hotelbooking.repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.hotelbooking.entity.Booking;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    // NIGHT booking overlap
    @Query("SELECT b FROM Booking b WHERE b.room.id = :roomId " +
           "AND b.status <> 'CANCELLED' " +
           "AND (:bookingId IS NULL OR b.id <> :bookingId) " +
           "AND b.checkInDate < :checkOutDate " +
           "AND b.checkOutDate > :checkInDate")
    List<Booking> findOverlappingBookings(
            @Param("roomId") Long roomId,
            @Param("checkInDate") LocalDate checkInDate,
            @Param("checkOutDate") LocalDate checkOutDate,
            @Param("bookingId") Long bookingId
    );

    // HOURLY booking overlap
    @Query("SELECT b FROM Booking b WHERE b.room.id = :roomId " +
           "AND b.status <> 'CANCELLED' " +
           "AND (:bookingId IS NULL OR b.id <> :bookingId) " +
           "AND b.checkInDate = :bookingDate " +
           "AND b.checkInTime < :newCheckOutTime " +
           "AND b.checkOutTime > :newCheckInTime")
    List<Booking> findOverlappingHourlyBookings(
            @Param("roomId") Long roomId,
            @Param("bookingDate") LocalDate bookingDate,
            @Param("newCheckInTime") LocalTime newCheckInTime,
            @Param("newCheckOutTime") LocalTime newCheckOutTime,
            @Param("bookingId") Long bookingId
    );

    List<Booking> findByRoom_IdAndStatusNot(Long roomId, String status);

    List<Booking> findByCustomerEmail(String customerEmail);

    @Query("SELECT COUNT(DISTINCT b.customerEmail) FROM Booking b")
    long countDistinctCustomers();

    List<Booking> findByCheckInDate(LocalDate checkInDate);
}
