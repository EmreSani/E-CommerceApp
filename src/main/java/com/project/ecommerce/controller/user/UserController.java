package com.project.ecommerce.controller.user;

import com.project.ecommerce.payload.request.user.UserRequest;
import com.project.ecommerce.payload.response.business.OrderItemResponse;
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

    @PostMapping("/save/{userRole}") // http://localhost:8080/users/save/Admin  + JSON + POST
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseEntity<ResponseMessage<UserResponse>> saveUser(@RequestBody @Valid UserRequest userRequest,
                                                                  @PathVariable String userRole) {
        return ResponseEntity.ok(userService.saveUser(userRequest, userRole));
    }

    @GetMapping("/all") //http://localhost:8080/users/all
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/{userId}") //http://localhost:8080/users/123
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseMessage<UserResponse> getUserById(@PathVariable Long userId) {
        return userService.getUserById(userId);
    }

    @DeleteMapping("/{userId}") //http://localhost:8080/users/123
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseEntity<String> deleteUserById(@RequestParam(value = "userId") Long userId) {
        return ResponseEntity.ok(userService.deleteUserById(userId));
    }

    //  5-id ile customer ı update etme -> http://localhost:8080/users/update/12 //Customer is updated successfully mesajı dönsün.
    //emaili update ederken yeni değer tabloda var ve kendi maili değilse hata fırlatır. (ConflictException)
    @PutMapping("/update/{userId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseMessage<UserResponse> updateUser(
            @RequestBody @Valid UserRequest userRequest,
            @PathVariable Long userId) {
        return userService.updateUser(userRequest, userId);
    }

    @GetMapping("/page") //http://localhost:8080/users/page?page=0&size=10&sort=name&type=desc
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


    @GetMapping("/username") //http://localhost:8080/users/username?userName=johndoe
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseMessage<UserResponse> getUserByUserName(@RequestParam(value = "userName") String userName) {
        return userService.getUserByUserName(userName);
    }

    //8-fullname ile customer getirme-> http://localhost:8080/users/fullname?name=John&lastName=Doe
    @GetMapping("/fullname")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseMessage<List<UserResponse>> getUserByFullName(
            @RequestParam(value = "name") String name,
            @RequestParam(value = "lastName") String lastname
    ) {
        return userService.getUserByFullName(name, lastname);
    }

    //9-İsmi ... içeren customerlar -> http://localhost:8080/users/contains?name=Ja
    @GetMapping("/contains")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseMessage<List<UserResponse>> getUserByNameContains(
            @RequestParam(value = "name") String name
    ) {
        return userService.getUserByContains(name);

    }

    @GetMapping("/search") //http://localhost:8080/users/search?letters=pa
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseMessage<List<UserResponse>> getUserByFullNameContainsTheseLetters(
            @RequestParam(value = "letters") String letters
    ) {
        return userService.getUserByFullNameContainsTheseLetters(letters);
    }

    //TODO: add updateUserForUsers

}
