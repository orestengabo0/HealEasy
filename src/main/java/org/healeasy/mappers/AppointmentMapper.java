package org.healeasy.mappers;

import org.healeasy.DTOs.AppointmentRequestDto;
import org.healeasy.DTOs.AppointmentResponseDto;
import org.healeasy.entities.Appointment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper for converting between Appointment entity and AppointmentDto.
 */
@Mapper(componentModel = "spring")
public interface AppointmentMapper {
    
    /**
     * Convert an Appointment entity to an AppointmentResponseDto.
     * 
     * @param appointment The Appointment entity to convert
     * @return The converted AppointmentResponseDto
     */
    @Mapping(source = "doctor.id", target = "doctorId")
    @Mapping(source = "doctor.user.username", target = "doctorUsername")
    @Mapping(source = "patient.id", target = "patientId")
    @Mapping(source = "patient.user.username", target = "patientUsername")
    AppointmentResponseDto toDto(Appointment appointment);
    
    /**
     * Convert an AppointmentRequestDto to an Appointment entity.
     * Note: This does not set the doctor and patient entities, only the IDs.
     * The service layer should handle setting the actual entities.
     * 
     * @param requestDto The AppointmentRequestDto to convert
     * @return The converted Appointment entity
     */
    Appointment toEntity(AppointmentRequestDto requestDto);
}