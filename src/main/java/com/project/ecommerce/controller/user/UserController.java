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
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/save/{userRole}") // http://localhost:8080/user/save/Admin  + JSON + POST
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseEntity<ResponseMessage<UserResponse>> saveUser(@RequestBody @Valid UserRequest userRequest,
                                                                  @PathVariable String userRole){
        return ResponseEntity.ok(userService.saveUser(userRequest, userRole));
    }

    @GetMapping("/allUsers")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseEntity<List<UserResponse>> getAllUsers (){
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/page")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseEntity<Page<UserResponse>> getUserByPage(
            @RequestParam(value = "page",defaultValue = "0") int page,
            @RequestParam(value = "size",defaultValue = "10") int size,
            @RequestParam(value = "sort",defaultValue = "name") String sort,
            @RequestParam(value = "type",defaultValue = "desc") String type
    ){
        Page<UserResponse> usersByPage = userService.getUsersByPage(page,size,sort,type);
        return new ResponseEntity<>(usersByPage, HttpStatus.OK) ;
    }

    @GetMapping //http://localhost:8080/user/custom?id=1
    @PreAuthorize("hasAnyAuthority('ADMIN)")
    public ResponseMessage<UserResponse> getUserById (@RequestParam Long userId){
        return userService.getUserById(userId);
    }

    @DeleteMapping
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseEntity<String> deleteUserById (@RequestParam Long userId){
        return ResponseEntity.ok(userService.deleteUserById(userId));
    }


}
