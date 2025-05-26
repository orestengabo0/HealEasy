package org.healeasy.DTOs;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for creating or updating an appointment
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentRequestDto {
    
    @NotNull(message = "Doctor ID is required")
    private Long doctorId;
    
    @NotNull(message = "Patient ID is required")
    private Long patientId;
    
    @NotNull(message = "Schedule time is required")
    @Future(message = "Schedule time must be in the future")
    private LocalDateTime scheduleTime;
    
    @Positive(message = "Duration must be positive")
    private Integer durationMinutes = 30; // Default 30 minutes
    
    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;
}