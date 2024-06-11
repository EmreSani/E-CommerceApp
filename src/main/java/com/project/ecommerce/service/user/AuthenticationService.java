package com.project.ecommerce.service.user;

import com.project.ecommerce.entity.concretes.user.User;
import com.project.ecommerce.exception.BadRequestException;
import com.project.ecommerce.payload.messages.ErrorMessages;
import com.project.ecommerce.payload.request.authentication.LoginRequest;
import com.project.ecommerce.payload.request.business.UpdatePasswordRequest;
import com.project.ecommerce.payload.response.authentication.AuthResponse;
import com.project.ecommerce.repository.user.UserRepository;
import com.project.ecommerce.security.jwt.JwtUtils;
import com.project.ecommerce.security.service.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    public final JwtUtils jwtUtils;
    public final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    // Not: Login() *************************************************
    public ResponseEntity<AuthResponse> authenticateUser(LoginRequest loginRequest){
        //!!! Gelen requestin icinden kullanici adi ve parola bilgisi aliniyor
        String username = loginRequest.getUsername();
        String password = loginRequest.getPassword();
        // !!! authenticationManager uzerinden kullaniciyi valide ediyoruz
        Authentication authentication =
                authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username,password));
        // !!! valide edilen kullanici Context e atiliyor
        SecurityContextHolder.getContext().setAuthentication(authentication);
        // !!! JWT token olusturuluyor
        String token = "Bearer " + jwtUtils.generateJwtToken(authentication);
        // !!! login islemini gerceklestirilen kullaniciya ulasiliyor
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        // !!!  Response olarak login islemini yapan kullaniciyi donecegiz gerekli fieldlar setleniyor
        // !!! GrantedAuthority turundeki role yapisini String turune ceviriliyor
        Set<String> roles = userDetails.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());
        //!!! bir kullanicinin birden fazla rolu olmayacagi icin ilk indexli elemani aliyoruz
        Optional<String> role = roles.stream().findFirst();
        // burada login islemini gerceklestiren kullanici bilgilerini response olarak
        // gonderecegimiz icin, gerekli bilgiler setleniyor.
        AuthResponse.AuthResponseBuilder authResponse = AuthResponse.builder();
        authResponse.username(userDetails.getUsername());
        authResponse.token(token.substring(7));
        authResponse.name(userDetails.getName());
        // !!! role bilgisi varsa response nesnesindeki degisken setleniyor
        role.ifPresent(authResponse::role);
        // !!! AuthResponse nesnesi ResponseEntity ile donduruyoruz
        return ResponseEntity.ok(authResponse.build());
    }

    // Not: updatePassword() *****************************************
    public void updatePassword(UpdatePasswordRequest updatePasswordRequest, HttpServletRequest request) {

        String userName = (String) request.getAttribute("username");
        User user = userRepository.findByUsername(userName);
        // !!! Builtin attribute: Datalarının Değişmesi istenmeyen bir objenin builtIn değeri true olur.
        if(Boolean.TRUE.equals(user.getBuilt_in())) { // null değerleriyle çalışırken güvenli bir yöntemdir. Boolean.TRUE.equals
            throw new BadRequestException(ErrorMessages.NOT_PERMITTED_METHOD_MESSAGE);
        }
        // !!! Eski sifre bilgisi dogrumu kontrolu
        if(!passwordEncoder.matches(updatePasswordRequest.getOldPassword(),user.getPassword())) {
            throw new BadRequestException(ErrorMessages.PASSWORD_NOT_MATCHED);
        }
        // !!! yeni sifre hashlenerek Kaydediliyor
        String hashedPassword=  passwordEncoder.encode(updatePasswordRequest.getNewPassword());

        user.setPassword(hashedPassword);
        userRepository.save(user);
    }
}
