package com.project.ecommerce.controller.user;

import com.project.ecommerce.payload.request.user.UserRequest;
import com.project.ecommerce.payload.response.business.ResponseMessage;
import com.project.ecommerce.payload.response.user.UserResponse;
import com.project.ecommerce.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    /**
     * Saves a new user with the specified role.
     * Example URL: POST http://localhost:8080/users/save/Admin
     */
    @PostMapping("/save/{userRole}")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseEntity<ResponseMessage<UserResponse>> saveUser(@RequestBody @Valid UserRequest userRequest,
                                                                  @PathVariable String userRole) {
        return ResponseEntity.ok(userService.saveUser(userRequest, userRole));
    }

    /**
     * Retrieves all users.
     * Example URL: GET http://localhost:8080/users/all
     */
    @GetMapping("/all")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    /**
     * Retrieves a user by their ID.
     * Example URL: GET http://localhost:8080/users/{userId}
     */
    @GetMapping("/{userId}")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseMessage<UserResponse> getUserById(@PathVariable Long userId) {
        return userService.getUserById(userId);
    }

    /**
     * Deletes a user by their ID.
     * Example URL: DELETE http://localhost:8080/users/{userId}
     */
    @DeleteMapping("/{userId}")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseEntity<String> deleteUserById(@PathVariable Long userId) {
        return ResponseEntity.ok(userService.deleteUserById(userId));
    }

    /**
     * Updates a user with the specified ID.
     * Example URL: PUT http://localhost:8080/users/update/{userId}
     */
    @PutMapping("/update/{userId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseMessage<UserResponse> updateUser(
            @RequestBody @Valid UserRequest userRequest,
            @PathVariable Long userId) {
        return userService.updateUser(userRequest, userId);
    }

    /**
     * Retrieves users based on pagination parameters.
     * Example URL: GET http://localhost:8080/users/page?page=0&size=10&sort=name&type=desc
     */
    @GetMapping("/page")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseEntity<Page<UserResponse>> getUserByPage(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sort", defaultValue = "name") String sort,
            @RequestParam(value = "type", defaultValue = "desc") String type
    ) {
        Page<UserResponse> usersByPage = userService.getUsersByPage(page, size, sort, type);
        return new ResponseEntity<>(usersByPage, HttpStatus.OK);
    }

    /**
     * Retrieves a user by their username.
     * Example URL: GET http://localhost:8080/users/username?userName=johndoe
     */
    @GetMapping("/username")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseMessage<UserResponse> getUserByUserName(@RequestParam(value = "userName") String userName) {
        return userService.getUserByUserName(userName);
    }

    /**
     * Retrieves users by their full name.
     * Example URL: GET http://localhost:8080/users/fullname?name=John&lastName=Doe
     */
    @GetMapping("/fullname")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseMessage<List<UserResponse>> getUserByFullName(
            @RequestParam(value = "name") String name,
            @RequestParam(value = "lastName") String lastname
    ) {
        return userService.getUserByFullName(name, lastname);
    }

    /**
     * Retrieves users whose names contain a specified substring.
     * Example URL: GET http://localhost:8080/users/contains?name=Ja
     */
    @GetMapping("/contains")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseMessage<List<UserResponse>> getUserByNameContains(
            @RequestParam(value = "name") String name
    ) {
        return userService.getUserByContains(name);
    }

    /**
     * Retrieves users whose names or last names contain specified letters.
     * Example URL: GET http://localhost:8080/users/search?letters=pa
     */
    @GetMapping("/search")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseMessage<List<UserResponse>> getUserByFullNameContainsTheseLetters(
            @RequestParam(value = "letters") String letters
    ) {
        return userService.getUserByFullNameContainsTheseLetters(letters);
    }

    @PutMapping("/updateMyself")
    @PreAuthorize("hasAnyAuthority('CUSTOMER')")
    public ResponseEntity<ResponseMessage<UserResponse>> updateAuthenticatedUser(
            @RequestBody @Valid UserRequest userRequest) {

        // Delegate the update logic to the service layer
        ResponseMessage<UserResponse> response = userService.updateSelf(userRequest);

        // Return response based on the service layer result
        return ResponseEntity
                .status(response.getHttpStatus())
                .body(response);
    }
}
