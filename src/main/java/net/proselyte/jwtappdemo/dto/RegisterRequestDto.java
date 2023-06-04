package net.proselyte.jwtappdemo.dto;

import lombok.Data;
import net.proselyte.jwtappdemo.model.User;

@Data
public class RegisterRequestDto {
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private String password;

    public User toUser(){       //возвращаем User на основании UserDto
        User user = new User();
        user.setUsername(username);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setPassword(password);

        return user;
    }

}
