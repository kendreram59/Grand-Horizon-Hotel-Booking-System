
package com.hotelbooking.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.hotelbooking.entity.Room;
import com.hotelbooking.service.RoomService;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/rooms")
public class RoomController {

    private final RoomService roomService;

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    // Check Admin Login
    private void checkAdminLogin(HttpSession session) {
        if (session.getAttribute("adminId") == null) {
            throw new ResponseStatusException(
                HttpStatus.UNAUTHORIZED,
                "Please login as Admin first"
            );
        }
    }

    // Add Room - Admin Only
    @PostMapping
    public Room addRoom(@RequestBody Room room, HttpSession session) {
        checkAdminLogin(session);
        return roomService.saveRoom(room);
    }

    // Get All Rooms - Public
    @GetMapping
    public List<Room> getAllRooms() {
        return roomService.getAllRooms();
    }

    // Get Room By ID - Public
    @GetMapping("/{id}")
    public Room getRoomById(@PathVariable Long id) {
        return roomService.getRoomById(id);
    }

    // Update Room - Admin Only
    @PutMapping("/{id}")
    public Room updateRoom(
            @PathVariable Long id,
            @RequestBody Room roomDetails,
            HttpSession session) {

        checkAdminLogin(session);

        Room room = roomService.getRoomById(id);

        room.setRoomNumber(roomDetails.getRoomNumber());
        room.setRoomType(roomDetails.getRoomType());
        room.setPricePerNight(roomDetails.getPricePerNight());
        room.setCapacity(roomDetails.getCapacity());
        room.setDescription(roomDetails.getDescription());
        room.setAvailable(roomDetails.isAvailable());
        room.setImageUrl(roomDetails.getImageUrl());
        room.setPriceType(roomDetails.getPriceType());

        return roomService.saveRoom(room);
    }

    // Delete Room - Admin Only
    @DeleteMapping("/{id}")
    public String deleteRoom(@PathVariable Long id, HttpSession session) {

        checkAdminLogin(session);

        roomService.deleteRoom(id);

        return "Room deleted successfully";
    }
}
