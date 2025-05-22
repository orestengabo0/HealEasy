package org.healeasy.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.healeasy.entities.AvailableSlots;

@Data
@AllArgsConstructor
public class DoctorDto {
    private Long id;
    private String username;
    private String email;
    private String phoneNumber;
    private String specialization;
    private String licenceNumber;
    private AvailableSlots availableSlots;
}
