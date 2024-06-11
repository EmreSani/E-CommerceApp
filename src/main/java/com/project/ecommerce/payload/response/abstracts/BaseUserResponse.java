package com.project.ecommerce.payload.response.abstracts;


import com.project.ecommerce.entity.enums.Gender;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public abstract class BaseUserResponse {

    private Long userId;
    private String username;
    private String name;
    private String surname;
    private LocalDate birthDay;
    private String birthPlace;
    private String phoneNumber;
    private Gender gender;
    private String email;
    private String userRole;
}
