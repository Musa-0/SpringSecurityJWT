package net.proselyte.jwtappdemo.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import net.proselyte.jwtappdemo.model.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;

/**
 * Util class that provides methods for generation, validation, etc. of JWT token.
 *
 * @author Eugene Suleimanov
 * @version 1.0
 */
@Component
public class JwtTokenProvider {         //генерация Jwt Token

    @Value("${jwt.token.secret}")
    private String secret;          //берем из файла properties наш секретный ключ для шифровки подписи токена

    @Value("${jwt.token.expired}")
    private long validityInMilliseconds;    //берем срок действитя токена из properties файла. в миллисекундах


    @Autowired
    private UserDetailsService userDetailsService;




    @PostConstruct          //генерируем байтовую строку на основе соли которую мы прописали в properties
    protected void init() {
        secret = Base64.getEncoder().encodeToString(secret.getBytes());
    }

    public String createToken(String username, List<Role> roles) {      //создание токина

        Claims claims = Jwts.claims().setSubject(username);     //Claims библиотека для JwtTokens. которая на основании имени и ролей генерирует токен
        claims.put("roles", getRoleNames(roles));

        Date now = new Date();
        Date validity = new Date(now.getTime() + validityInMilliseconds);       //создаем дату истечения срока токена

        return Jwts.builder()//         создаем сам jwt токен
                .setClaims(claims)//        часть токена на основании имени и ролей
                .setIssuedAt(now)//         дата создания
                .setExpiration(validity)//  дата истечения срока
                .signWith(SignatureAlgorithm.HS256, secret)//   подпись токена на основе нашего секретного ключа
                .compact();         //делаем единый токен
    }

    public Authentication getAuthentication(String token) {         //возвращает Spring пользователя на основании токена
        UserDetails userDetails = userDetailsService.loadUserByUsername(getUsername(token));
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    public String getUsername(String token) {       //возвращает имя пользователя по токену
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody().getSubject();
    }

    public String resolveToken(HttpServletRequest req) {        //анализируем токен. который передается в заголовке
        String bearerToken = req.getHeader("Authorization");  //берем параметр из заголовка Authorization
        if (bearerToken != null && bearerToken.startsWith("Bearer_")) { //токен должен начинаться с Bearer_
            return bearerToken.substring(7, bearerToken.length());      //берем сам токен без Bearer_
        }
        return null;        //иначе вернем null
    }

    public boolean validateToken(String token) {            //проверка активен ли токен, или его время истекло
        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(secret).parseClaimsJws(token); //конвертируем сам токен в класс Jwts
            if (claims.getBody().getExpiration().before(new Date())) {      //и сверяем истекло ли время токена
                return false;
            }

            return true;
        } catch (JwtException | IllegalArgumentException e) {
            throw new JwtAuthenticationException("JWT token is expired or invalid");
        }
    }

    private List<String> getRoleNames(List<Role> userRoles) {        //возвращает имена ролей на основании объектов Role
        List<String> result = new ArrayList<>();

        userRoles.forEach(role -> {
            result.add(role.getName());
        });

        return result;          //возвращаем список стрингов ролей
    }
}
