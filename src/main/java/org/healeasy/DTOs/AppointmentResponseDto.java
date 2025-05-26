package org.healeasy.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.healeasy.enums.AppointmentStatus;

import java.time.LocalDateTime;

/**
 * DTO for returning appointment information
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentResponseDto {
    
    private Long id;
    private Long doctorId;
    private String doctorUsername;
    private Long patientId;
    private String patientUsername;
    private LocalDateTime scheduleTime;
    private Integer durationMinutes;
    private String description;
    private AppointmentStatus status;
    private String zoomJoinUrl;
    private String zoomPassword;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}