package org.healeasy.mappers;

import org.healeasy.DTOs.UserDTO;
import org.healeasy.entities.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDTO toDto(User user);
}
