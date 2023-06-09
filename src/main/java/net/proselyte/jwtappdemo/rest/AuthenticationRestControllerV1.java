package net.proselyte.jwtappdemo.rest;

import net.proselyte.jwtappdemo.dto.AuthenticationRequestDto;
import net.proselyte.jwtappdemo.dto.RegisterRequestDto;
import net.proselyte.jwtappdemo.model.User;
import net.proselyte.jwtappdemo.security.jwt.JwtTokenProvider;
import net.proselyte.jwtappdemo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * REST controller for authentication requests (login, logout, register, etc.)
 *
 * @author Eugene Suleimanov
 * @version 1.0
 */

@RestController
@RequestMapping(value = "/api/v1/auth/")
public class AuthenticationRestControllerV1 {



    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;
    private final UserService userService;

    @Autowired
    public AuthenticationRestControllerV1(AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider, UserService userService) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userService = userService;
    }

    @PostMapping("login")
    public ResponseEntity login(@RequestBody AuthenticationRequestDto requestDto) {     //принимаем username и password
        String username = requestDto.getUsername();
        String password = requestDto.getPassword();
        User user = userService.findByUsername(username);
        userService.login(username, password);
        String token = jwtTokenProvider.createToken(username, user.getRoles()); //создаем токен на основании данных пользовтеля

        Map<Object, Object> response = new HashMap<>();
        response.put("username", username);
        response.put("token", token);

        return ResponseEntity.ok(response);     //возвращаем ему имя пользовтеля и его токен

    }
    @PostMapping("register")
    public ResponseEntity register(@RequestBody RegisterRequestDto registerRequestDto){
        userService.register(registerRequestDto.toUser());
        User user = userService.findByUsername(registerRequestDto.getUsername());
        String token = jwtTokenProvider.createToken(user.getUsername(), user.getRoles());
        Map<Object, Object> response = new HashMap<>();
        response.put("username", user.getUsername());
        response.put("token", token);

        return ResponseEntity.ok(response);
    }
}
