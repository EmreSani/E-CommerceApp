package com.project.ecommerce.service.user;

import com.project.ecommerce.entity.concretes.user.User;
import com.project.ecommerce.entity.enums.RoleType;
import com.project.ecommerce.payload.mappers.UserMapper;
import com.project.ecommerce.payload.messages.SuccessMessages;
import com.project.ecommerce.payload.request.user.UserRequest;
import com.project.ecommerce.payload.response.business.ResponseMessage;
import com.project.ecommerce.payload.response.user.UserResponse;
import com.project.ecommerce.repository.user.UserRepository;
import com.project.ecommerce.service.helper.PageableHelper;
import com.project.ecommerce.service.validator.UniquePropertyValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UniquePropertyValidator uniquePropertyValidator;
    private final UserMapper userMapper;
    private final UserRoleService userRoleService;
    private final PasswordEncoder passwordEncoder;


    public ResponseMessage<UserResponse> saveUser(UserRequest userRequest, String userRole) {

        // is username - phoneNumber - email unique?
        uniquePropertyValidator.checkDuplicate(userRequest.getUsername(),
                userRequest.getPhoneNumber(), userRequest.getEmail());

        // DTO --> POJO
        User user = userMapper.mapUserRequestToUser(userRequest); // default: user is not premium

        // Setting role of the user
        if(userRole.equalsIgnoreCase(RoleType.ADMIN.name())){
            if(Objects.equals(userRequest.getUsername(),"Admin")){
                user.setBuilt_in(true);
            }
            user.setUserRole(userRoleService.getUserRole(RoleType.ADMIN));
        } else if (userRole.equalsIgnoreCase("Customer")) {
            user.setUserRole(userRoleService.getUserRole(RoleType.CUSTOMER));
        }


        // encoding the plain text password
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        User savedUser = userRepository.save(user);

        return ResponseMessage.<UserResponse>builder()
                .message(String.format(SuccessMessages.USER_CREATE, user.getId()))
                .object(userMapper.mapUserToUserResponse(savedUser))
                .build() ;

    }

    public long countAllAdmins(){
        return userRepository.countAdmin(RoleType.ADMIN);
    }
}
