package org.healeasy.Iservices;

import org.healeasy.entities.AvailableSlots;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service interface for managing doctor available slots.
 */
public interface IAvailableSlotsService {
    
    /**
     * Create a new available slot for a doctor.
     * 
     * @param doctorId The ID of the doctor
     * @param startTime The start time of the slot
     * @param endTime The end time of the slot
     * @return The created available slot
     */
    AvailableSlots createSlot(Long doctorId, LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * Get all available slots for a doctor.
     * 
     * @param doctorId The ID of the doctor
     * @return List of available slots for the doctor
     */
    List<AvailableSlots> getSlotsByDoctorId(Long doctorId);
    
    /**
     * Get all available slots for a doctor within a time range.
     * 
     * @param doctorId The ID of the doctor
     * @param startTime The start of the time range
     * @param endTime The end of the time range
     * @return List of available slots for the doctor within the time range
     */
    List<AvailableSlots> getSlotsByDoctorIdAndTimeRange(Long doctorId, LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * Update an existing available slot.
     * 
     * @param slotId The ID of the slot to update
     * @param startTime The new start time
     * @param endTime The new end time
     * @return The updated available slot
     */
    AvailableSlots updateSlot(Long slotId, LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * Delete an available slot.
     * 
     * @param slotId The ID of the slot to delete
     */
    void deleteSlot(Long slotId);
    
    /**
     * Get an available slot by ID.
     * 
     * @param slotId The ID of the slot
     * @return The available slot with the given ID
     */
    AvailableSlots getSlotById(Long slotId);
}