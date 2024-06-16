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

    @GetMapping("/{userId}") //http://localhost:8080/user/custom?id=1
    @PreAuthorize("hasAnyAuthority('ADMIN)")
    public ResponseMessage<UserResponse> getUserById (@PathVariable Long userId){
        return userService.getUserById(userId);
    }

    @DeleteMapping
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseEntity<String> deleteUserById (@RequestParam(value = "userId") Long userId){
        return ResponseEntity.ok(userService.deleteUserById(userId));
    }

    //update method

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


    @GetMapping("/query")
    @PreAuthorize("hasAnyAuthority('ADMIN)")
    public ResponseMessage<UserResponse> getUserByUserName (@RequestParam(value = "userName") String userName){
        return userService.getUserByUserName(userName);
    }

    //8-fullname ile customer getirme-> http://localhost:8080/customers/fullquery? name=Jack&lastName=Sparrow
    @GetMapping("/fullquery")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseMessage<List<UserResponse>> getUserByFullName (
            @RequestParam(value = "name") String name,
            @RequestParam(value = "lastName") String lastname
    ){
       return userService.getUserByFullName(name,lastname);

    }
    //9-İsmi ... içeren customerlar -> http://localhost:8080/customers/jpql?name=Ja
    @GetMapping("/jpql")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseMessage<List<UserResponse>> getUserByNameContains(
            @RequestParam(value = "name") String name
    ){
        return userService.getUserByContains(name);

    }

    //10-Idsi verilen müşterinin tüm siparişlerini getirme -> http://localhost:8080/customers/allorder/1
    @GetMapping("/allorder/{userId}")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseMessage<List<OrderItemResponse>> getUsersOrderItemsById (
           @PathVariable Long id
    ){
        return userService.getUsersOrderItemsById(id);

    }

    //11-ÖDEV:Requestle gelen "harf dizisi" name veya lastname inde geçen customerları döndür. -> http://localhost:8080/customers/search?word=pa
@GetMapping("/search")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
public ResponseMessage<List<UserResponse>> getUserByFullNameContainsTheseLetters (
        @RequestParam(value = "letters") String letters
){
    return userService.getUserByFullNameContainsTheseLetters(letters);
}


}
