package net.proselyte.jwtappdemo.dto;

import lombok.Data;

/**
 * DTO class for authentication (login) request.
 *
 * @author Eugene Suleimanov
 * @version 1.0
 */

@Data
public class AuthenticationRequestDto {         //класс который содержит в себе параметры запроса на вход
    private String username;
    private String password;
}
