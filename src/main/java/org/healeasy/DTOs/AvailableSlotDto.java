package org.healeasy.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for transferring available slot data.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AvailableSlotDto {
    private Long id;
    private Long doctorId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}