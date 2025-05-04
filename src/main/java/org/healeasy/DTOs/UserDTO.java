package org.healeasy.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class UserDTO {
    private Long id;
    private String username;
    private String email;
    private String phoneNumber;
}
