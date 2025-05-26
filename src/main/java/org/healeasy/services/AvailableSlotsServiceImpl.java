package org.healeasy.services;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.healeasy.Iservices.IAvailableSlotsService;
import org.healeasy.entities.AvailableSlots;
import org.healeasy.entities.Doctor;
import org.healeasy.exceptions.DoctorNotFoundException;
import org.healeasy.exceptions.SlotNotFoundException;
import org.healeasy.repositories.AvailableSlotsRepository;
import org.healeasy.repositories.DoctorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Implementation of the IAvailableSlotsService interface.
 * Handles business logic for doctor available slots operations.
 */
@Service
@AllArgsConstructor
@Slf4j
public class AvailableSlotsServiceImpl implements IAvailableSlotsService {

    private final AvailableSlotsRepository availableSlotsRepository;
    private final DoctorRepository doctorRepository;

    /**
     * Create a new available slot for a doctor.
     * 
     * @param doctorId The ID of the doctor
     * @param startTime The start time of the slot
     * @param endTime The end time of the slot
     * @return The created available slot
     */
    @Override
    @Transactional
    public AvailableSlots createSlot(Long doctorId, LocalDateTime startTime, LocalDateTime endTime) {
        // Validate input
        if (startTime == null || endTime == null) {
            throw new IllegalArgumentException("Start time and end time cannot be null");
        }
        
        if (startTime.isAfter(endTime) || startTime.isEqual(endTime)) {
            throw new IllegalArgumentException("Start time must be before end time");
        }
        
        // Get doctor
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new DoctorNotFoundException("Doctor not found with ID: " + doctorId));
        
        // Create new slot
        AvailableSlots slot = new AvailableSlots();
        slot.setDoctor(doctor);
        slot.setStartTime(startTime);
        slot.setEndTime(endTime);
        
        // Save and return
        return availableSlotsRepository.save(slot);
    }

    /**
     * Get all available slots for a doctor.
     * 
     * @param doctorId The ID of the doctor
     * @return List of available slots for the doctor
     */
    @Override
    public List<AvailableSlots> getSlotsByDoctorId(Long doctorId) {
        // Check if doctor exists
        if (!doctorRepository.existsById(doctorId)) {
            throw new DoctorNotFoundException("Doctor not found with ID: " + doctorId);
        }
        
        // Get and return slots
        return availableSlotsRepository.findByDoctorId(doctorId);
    }

    /**
     * Get all available slots for a doctor within a time range.
     * 
     * @param doctorId The ID of the doctor
     * @param startTime The start of the time range
     * @param endTime The end of the time range
     * @return List of available slots for the doctor within the time range
     */
    @Override
    public List<AvailableSlots> getSlotsByDoctorIdAndTimeRange(Long doctorId, LocalDateTime startTime, LocalDateTime endTime) {
        // Validate input
        if (startTime == null || endTime == null) {
            throw new IllegalArgumentException("Start time and end time cannot be null");
        }
        
        if (startTime.isAfter(endTime)) {
            throw new IllegalArgumentException("Start time must be before end time");
        }
        
        // Check if doctor exists
        if (!doctorRepository.existsById(doctorId)) {
            throw new DoctorNotFoundException("Doctor not found with ID: " + doctorId);
        }
        
        // Get and return slots
        return availableSlotsRepository.findByDoctorIdAndStartTimeGreaterThanEqualAndEndTimeLessThanEqual(
                doctorId, startTime, endTime);
    }

    /**
     * Update an existing available slot.
     * 
     * @param slotId The ID of the slot to update
     * @param startTime The new start time
     * @param endTime The new end time
     * @return The updated available slot
     */
    @Override
    @Transactional
    public AvailableSlots updateSlot(Long slotId, LocalDateTime startTime, LocalDateTime endTime) {
        // Validate input
        if (startTime == null || endTime == null) {
            throw new IllegalArgumentException("Start time and end time cannot be null");
        }
        
        if (startTime.isAfter(endTime) || startTime.isEqual(endTime)) {
            throw new IllegalArgumentException("Start time must be before end time");
        }
        
        // Get slot
        AvailableSlots slot = availableSlotsRepository.findById(slotId)
                .orElseThrow(() -> new SlotNotFoundException("Slot not found with ID: " + slotId));
        
        // Update slot
        slot.setStartTime(startTime);
        slot.setEndTime(endTime);
        
        // Save and return
        return availableSlotsRepository.save(slot);
    }

    /**
     * Delete an available slot.
     * 
     * @param slotId The ID of the slot to delete
     */
    @Override
    @Transactional
    public void deleteSlot(Long slotId) {
        // Check if slot exists
        if (!availableSlotsRepository.existsById(slotId)) {
            throw new SlotNotFoundException("Slot not found with ID: " + slotId);
        }
        
        // Delete slot
        availableSlotsRepository.deleteById(slotId);
    }

    /**
     * Get an available slot by ID.
     * 
     * @param slotId The ID of the slot
     * @return The available slot with the given ID
     */
    @Override
    public AvailableSlots getSlotById(Long slotId) {
        return availableSlotsRepository.findById(slotId)
                .orElseThrow(() -> new SlotNotFoundException("Slot not found with ID: " + slotId));
    }
}