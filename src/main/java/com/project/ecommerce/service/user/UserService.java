package com.project.ecommerce.service.user;

import com.project.ecommerce.entity.concretes.user.User;
import com.project.ecommerce.entity.enums.RoleType;
import com.project.ecommerce.exception.ResourceNotFoundException;
import com.project.ecommerce.payload.mappers.UserMapper;
import com.project.ecommerce.payload.messages.ErrorMessages;
import com.project.ecommerce.payload.messages.SuccessMessages;
import com.project.ecommerce.payload.request.user.UserRequest;
import com.project.ecommerce.payload.response.business.ResponseMessage;
import com.project.ecommerce.payload.response.user.UserResponse;
import com.project.ecommerce.repository.user.UserRepository;
import com.project.ecommerce.service.helper.PageableHelper;
import com.project.ecommerce.service.validator.UniquePropertyValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UniquePropertyValidator uniquePropertyValidator;
    private final UserMapper userMapper;
    private final UserRoleService userRoleService;
    private final PasswordEncoder passwordEncoder;
    private final PageableHelper pageableHelper;


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

    public List<UserResponse> getAllUsers() {
       return userRepository.findAll().stream().map(userMapper::mapUserToUserResponse).collect(Collectors.toList());
    }

    public Page<UserResponse> getUsersByPage(int page, int size, String sort, String type) {
       Pageable pageable = pageableHelper.getPageableWithProperties(page,size,sort,type);
       return userRepository.findAll(pageable).map(userMapper::mapUserToUserResponse);
    }

    public ResponseMessage<UserResponse> getUserById(Long userId) {

    return ResponseMessage.<UserResponse>builder().message(SuccessMessages.USER_FOUND).
            httpStatus(HttpStatus.OK).object( userMapper.mapUserToUserResponse(isUserExist(userId))).build();

    }

    public User isUserExist(Long userId) {
        return userRepository.findById(userId).orElseThrow(() ->
                new ResourceNotFoundException(String.format(ErrorMessages.NOT_FOUND_USER_MESSAGE,
                        userId)));
    }


    public String deleteUserById(Long userId) {
       userRepository.delete(isUserExist(userId));

       return String.format(SuccessMessages.USER_DELETE, userId); //TODO: need to control roles

    }
}
