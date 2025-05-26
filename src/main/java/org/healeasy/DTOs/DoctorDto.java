package org.healeasy.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.healeasy.entities.AvailableSlots;
import org.healeasy.enums.DoctorStatus;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DoctorDto {
    private Long id;
    private String username;
    private String email;
    private String phoneNumber;
    private String specialization;
    private String licenceNumber;
    private String licenseDocumentUrl;
    private String idDocumentUrl;
    private DoctorStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private AvailableSlots availableSlots;
}
