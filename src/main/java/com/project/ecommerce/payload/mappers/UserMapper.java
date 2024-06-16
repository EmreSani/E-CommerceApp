package com.project.ecommerce.payload.mappers;

import com.project.ecommerce.entity.concretes.user.User;
import com.project.ecommerce.payload.request.user.UserRequest;
import com.project.ecommerce.payload.response.user.UserResponse;
import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@Component
public class UserMapper {
    public User mapUserRequestToUser(UserRequest userRequest) {

        return User.builder()
                .username(userRequest.getUsername())
                .name(userRequest.getName())
                .surname(userRequest.getSurname())
                .password(userRequest.getPassword())
                .birthDay(userRequest.getBirthDay())
                .birthPlace(userRequest.getBirthPlace())
                .phoneNumber(userRequest.getPhoneNumber())
                .gender(userRequest.getGender())
                .email(userRequest.getEmail())
                .isPremium(userRequest.getIsPremium())
                .built_in(userRequest.getBuiltIn())
                .build();
    }

    public UserResponse mapUserToUserResponse(User user){

        return UserResponse.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .name(user.getName())
                .surname(user.getSurname())
                .phoneNumber(user.getPhoneNumber())
                .gender(user.getGender())
                .birthDay(user.getBirthDay())
                .birthPlace(user.getBirthPlace())
                .email(user.getEmail())
                .userRole(user.getUserRole().getRoleType().name())
                .build();

    }
}
