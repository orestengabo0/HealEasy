package org.healeasy.controllers;

import lombok.AllArgsConstructor;
import org.healeasy.DTOs.AvailableSlotDto;
import org.healeasy.Iservices.IAvailableSlotsService;
import org.healeasy.entities.AvailableSlots;
import org.healeasy.exceptions.DoctorNotFoundException;
import org.healeasy.exceptions.SlotNotFoundException;
import org.healeasy.repositories.DoctorRepository;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller for managing doctor available slots.
 */
@RestController
@RequestMapping("/api/v1/slots")
@AllArgsConstructor
public class AvailableSlotsController {

    private final IAvailableSlotsService availableSlotsService;
    private final DoctorRepository doctorRepository;

    /**
     * Create a new available slot for a doctor.
     * 
     * @param startTime The start time of the slot
     * @param endTime The end time of the slot
     * @return The created available slot
     */
    @PostMapping
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<AvailableSlotDto> createSlot(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            Long doctorId = Long.valueOf(authentication.getPrincipal().toString());
            if(!doctorRepository.isDoctor(doctorId))
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            AvailableSlots slot = availableSlotsService.createSlot(doctorId, startTime, endTime);
            return ResponseEntity.status(HttpStatus.CREATED).body(convertToDto(slot));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (DoctorNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Get all available slots for a doctor.
     * 
     * @param doctorId The ID of the doctor
     * @return List of available slots for the doctor
     */
    @GetMapping
    public ResponseEntity<List<AvailableSlotDto>> getSlotsByDoctorId(@RequestParam Long doctorId) {
        try {
            List<AvailableSlots> slots = availableSlotsService.getSlotsByDoctorId(doctorId);
            List<AvailableSlotDto> slotDtos = slots.stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(slotDtos);
        } catch (DoctorNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Get all available slots for a doctor within a time range.
     * 
     * @param doctorId The ID of the doctor
     * @param startTime The start of the time range
     * @param endTime The end of the time range
     * @return List of available slots for the doctor within the time range
     */
    @GetMapping("/range")
    public ResponseEntity<List<AvailableSlotDto>> getSlotsByDoctorIdAndTimeRange(
            @RequestParam Long doctorId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        
        try {
            List<AvailableSlots> slots = availableSlotsService.getSlotsByDoctorIdAndTimeRange(doctorId, startTime, endTime);
            List<AvailableSlotDto> slotDtos = slots.stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(slotDtos);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (DoctorNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Get an available slot by ID.
     * 
     * @param slotId The ID of the slot
     * @return The available slot with the given ID
     */
    @GetMapping("/{slotId}")
    public ResponseEntity<AvailableSlotDto> getSlotById(@PathVariable Long slotId) {
        try {
            AvailableSlots slot = availableSlotsService.getSlotById(slotId);
            return ResponseEntity.ok(convertToDto(slot));
        } catch (SlotNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Update an existing available slot.
     * 
     * @param slotId The ID of the slot to update
     * @param startTime The new start time
     * @param endTime The new end time
     * @return The updated available slot
     */
    @PutMapping("/{slotId}")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<AvailableSlotDto> updateSlot(
            @PathVariable Long slotId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        
        try {
            AvailableSlots slot = availableSlotsService.updateSlot(slotId, startTime, endTime);
            return ResponseEntity.ok(convertToDto(slot));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (SlotNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Delete an available slot.
     * 
     * @param slotId The ID of the slot to delete
     * @return No content if successful
     */
    @DeleteMapping("/{slotId}")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<Void> deleteSlot(@PathVariable Long slotId) {
        try {
            availableSlotsService.deleteSlot(slotId);
            return ResponseEntity.noContent().build();
        } catch (SlotNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Convert an AvailableSlots entity to an AvailableSlotDto.
     * 
     * @param slot The AvailableSlots entity
     * @return The AvailableSlotDto
     */
    private AvailableSlotDto convertToDto(AvailableSlots slot) {
        AvailableSlotDto dto = new AvailableSlotDto();
        dto.setId(slot.getId());
        dto.setDoctorId(slot.getDoctor().getId());
        dto.setStartTime(slot.getStartTime());
        dto.setEndTime(slot.getEndTime());
        return dto;
    }
}