package org.healeasy.mappers;

import org.healeasy.DTOs.DoctorDto;
import org.healeasy.entities.Doctor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper for converting between Doctor entity and DoctorDto.
 */
@Mapper(componentModel = "spring")
public interface DoctorMapper {
    
    /**
     * Convert a Doctor entity to a DoctorDto.
     * 
     * @param doctor The Doctor entity to convert
     * @return The converted DoctorDto
     */
    @Mapping(source = "user.username", target = "username")
    @Mapping(source = "user.email", target = "email")
    @Mapping(source = "user.phoneNumber", target = "phoneNumber")
    DoctorDto toDto(Doctor doctor);
}