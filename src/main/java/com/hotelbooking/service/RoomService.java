package com.hotelbooking.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.hotelbooking.entity.Room;
import com.hotelbooking.repository.RoomRepository;

@Service
public class RoomService {

	private final RoomRepository roomRepository;
	
	public RoomService(RoomRepository roomRepository)
	{
		this.roomRepository = roomRepository;
	}
	
	
	// Add or Update Room 
	public Room saveRoom(Room room)
	{
		return roomRepository.save(room);
	}
	
	// Get All Rooms
	public List<Room> getAllRooms()
	{
		return roomRepository.findAll();
	}
	
	// Get Room By ID
	public Room getRoomById(long id)
	{
		return roomRepository.findById(id).orElseThrow(() -> new RuntimeException("Room not found with ID: " + id));
	}
	
	// Delete Room
	public void deleteRoom(long id)
	{
		Room room=getRoomById(id);
		roomRepository.delete(room);
	}
	
	public long getAvailableRoomCount() {
	    return roomRepository.countByAvailableTrue();
	}
}
