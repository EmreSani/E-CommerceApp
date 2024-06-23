package com.project.ecommerce.service.user;

import com.project.ecommerce.entity.concretes.business.Cart;
import com.project.ecommerce.entity.concretes.business.OrderItem;
import com.project.ecommerce.entity.concretes.user.User;
import com.project.ecommerce.entity.enums.RoleType;
import com.project.ecommerce.exception.BadRequestException;
import com.project.ecommerce.exception.ResourceNotFoundException;
import com.project.ecommerce.payload.mappers.UserMapper;
import com.project.ecommerce.payload.messages.ErrorMessages;
import com.project.ecommerce.payload.messages.SuccessMessages;
import com.project.ecommerce.payload.request.user.UserRequest;
import com.project.ecommerce.payload.response.business.OrderItemResponse;
import com.project.ecommerce.payload.response.business.ResponseMessage;
import com.project.ecommerce.payload.response.user.UserResponse;
import com.project.ecommerce.repository.user.UserRepository;
import com.project.ecommerce.security.service.UserDetailsImpl;
import com.project.ecommerce.service.business.CartService;
import com.project.ecommerce.service.business.OrderItemService;
import com.project.ecommerce.service.helper.MethodHelper;
import com.project.ecommerce.service.helper.PageableHelper;
import com.project.ecommerce.service.validator.UniquePropertyValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
//    private final OrderItemService orderItemService;
    private final MethodHelper methodHelper;
    private final CartService cartService;


    @Transactional
    public ResponseMessage<UserResponse> saveUser(UserRequest userRequest, String userRole) {

        // is username - phoneNumber - email unique?
        uniquePropertyValidator.checkDuplicate(userRequest.getUsername(),
                userRequest.getPhoneNumber(), userRequest.getEmail());

        // DTO --> POJO
        User user = userMapper.mapUserRequestToUser(userRequest); // default: user is not premium

        // Setting role of the user
        if (userRole.equalsIgnoreCase(RoleType.ADMIN.name())) {
            if (Objects.equals(userRequest.getUsername(), "Admin")) {
                user.setBuilt_in(true);
            }
            user.setUserRole(userRoleService.getUserRole(RoleType.ADMIN));
        } else if (userRole.equalsIgnoreCase("Customer")) {
            user.setUserRole(userRoleService.getUserRole(RoleType.CUSTOMER));
        }


        // encoding the plain text password
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        Cart cart =cartService.createCartForUser(user);
        user.setCart(cart);
        User savedUser = userRepository.save(user);

        return ResponseMessage.<UserResponse>builder()
                .message(String.format(SuccessMessages.USER_CREATE, user.getId()))
                .object(userMapper.mapUserToUserResponse(savedUser))
                .build();

    }

    public long countAllAdmins() {
        return userRepository.countAdmin(RoleType.ADMIN);
    }

    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream().map(userMapper::mapUserToUserResponse).collect(Collectors.toList());
    }

    public Page<UserResponse> getUsersByPage(int page, int size, String sort, String type) {
        Pageable pageable = pageableHelper.getPageableWithProperties(page, size, sort, type);
        return userRepository.findAll(pageable).map(userMapper::mapUserToUserResponse);
    }

    public ResponseMessage<UserResponse> getUserById(Long userId) {

        return ResponseMessage.<UserResponse>builder().message(SuccessMessages.USER_FOUND).
                httpStatus(HttpStatus.OK).object(userMapper.mapUserToUserResponse(methodHelper.isUserExist(userId))).build();

    }

    @Transactional
    public String deleteUserById(Long userId) {
        userRepository.delete(methodHelper.isUserExist(userId));

        return String.format(SuccessMessages.USER_DELETE, userId);

    }


    @Transactional
    public ResponseMessage<UserResponse> getUserByUserName(String userName) {

        return ResponseMessage.<UserResponse>builder().
                message(SuccessMessages.USER_FOUND).
                httpStatus(HttpStatus.OK).
                object(userMapper.mapUserToUserResponse(userRepository.findByUsername(userName).
                        orElseThrow(() -> new ResourceNotFoundException
                                (String.format
                                        (ErrorMessages.NOT_FOUND_USER_MESSAGE_WITH_USERNAME, userName)))))
                .build();

    }

    @Transactional
    public User getUserByUserNameReturnsUser(String userName) {

        return userRepository.findByUsername(userName).
                orElseThrow(() -> new ResourceNotFoundException
                        (String.format
                                (ErrorMessages.NOT_FOUND_USER_MESSAGE_WITH_USERNAME, userName)));

    }


    @Transactional
    public ResponseMessage<List<UserResponse>> getUserByFullName(String name, String lastname) {

        List<User> userList = userRepository.findByNameAndLastName(name, lastname);

        if (userList.isEmpty()) {
            throw new ResourceNotFoundException(ErrorMessages.NOT_FOUND_USER_MESSAGE_WITHOUT_ID);
        }

        return ResponseMessage.<List<UserResponse>>builder().
                message(SuccessMessages.USERS_FOUND).
                httpStatus(HttpStatus.OK).
                object(userList.stream().
                        map(userMapper::mapUserToUserResponse).
                        collect(Collectors.toList())).
                build();

    }

    @Transactional
    public ResponseMessage<List<UserResponse>> getUserByContains(String name) {

        List<User> userList = userRepository.findByNameContains(name);

        if (userList.isEmpty()) {
            throw new ResourceNotFoundException(ErrorMessages.NOT_FOUND_USER_MESSAGE_WITHOUT_ID);
        }

        return ResponseMessage.<List<UserResponse>>builder().
                message(SuccessMessages.USERS_FOUND).
                httpStatus(HttpStatus.OK).
                object(userList.stream().
                        map(userMapper::mapUserToUserResponse).
                        collect(Collectors.toList())).
                build();
    }

    @Transactional
    public ResponseMessage<List<UserResponse>> getUserByFullNameContainsTheseLetters(String letters) {
        List<User> userList = userRepository.findByNameOrLastNameContains(letters);

        if (userList.isEmpty()) {
            throw new ResourceNotFoundException(ErrorMessages.NOT_FOUND_USER_MESSAGE_WITHOUT_ID);
        }

        return ResponseMessage.<List<UserResponse>>builder().
                message(SuccessMessages.USERS_FOUND).
                httpStatus(HttpStatus.OK).
                object(userList.stream().
                        map(userMapper::mapUserToUserResponse).
                        collect(Collectors.toList())).
                build();
    }

    @Transactional
    public ResponseMessage<UserResponse> updateUser(UserRequest userRequest, Long userId) {

        User foundUser = methodHelper.isUserExist(userId);

        uniquePropertyValidator.checkUniqueProperties(foundUser, userRequest);


        methodHelper.checkBuiltIn(foundUser);

        User updatedUser = userMapper.mapUserRequestToUpdatedUser(userRequest, userId);

        updatedUser.setPassword(passwordEncoder.encode(userRequest.getPassword()));
        updatedUser.setCart(foundUser.getCart());
        updatedUser.setUserRole(foundUser.getUserRole());

        User savedUser = userRepository.save(updatedUser);

        return ResponseMessage.<UserResponse>builder()
                .message(SuccessMessages.USER_UPDATE_MESSAGE)
                .httpStatus(HttpStatus.OK)
                .object(userMapper.mapUserToUserResponse(savedUser))
                .build() ;
    }

    @Transactional
    public ResponseMessage<UserResponse> updateSelf(UserRequest userRequest) {
        // Retrieve the authenticated user's ID from the SecurityContext
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = ((UserDetailsImpl) authentication.getPrincipal()).getId();

        // Retrieve the existing user entity
        User existingUser = methodHelper.isUserExist(userId);

        if(Boolean.TRUE.equals(existingUser.getBuilt_in())) { // null değerleriyle çalışırken güvenli bir yöntemdir. Boolean.TRUE.equals
            throw new BadRequestException(ErrorMessages.NOT_PERMITTED_METHOD_MESSAGE);
        }

        uniquePropertyValidator.checkUniqueProperties(existingUser,userRequest);
        // Map userRequest to existing User entity
        User updatedUser = userMapper.mapUserRequestToUpdatedUser(userRequest, userId);

        // Encode password if it's included in the request
        if (userRequest.getPassword() != null) {
            updatedUser.setPassword(passwordEncoder.encode(userRequest.getPassword()));
        } else {
            // Preserve existing password if password is not included in the request
            updatedUser.setPassword(existingUser.getPassword());
        }

        // Preserve the existing user role
        updatedUser.setUserRole(existingUser.getUserRole());

        // Preserve the existing cart
        updatedUser.setCart(existingUser.getCart());

        // Save the updated user
        User savedUser = userRepository.save(updatedUser);

        // Return ResponseMessage with success message and updated user response
        return ResponseMessage.<UserResponse>builder()
                .message(SuccessMessages.USER_UPDATE_MESSAGE)
                .httpStatus(HttpStatus.OK)
                .object(userMapper.mapUserToUserResponse(savedUser))
                .build();
    }
}


