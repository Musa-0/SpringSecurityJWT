package net.proselyte.jwtappdemo.security.jwt;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * JWT token filter that handles all HTTP requests to application.
 *
 * @author Eugene Suliemanov
 * @version 1.0
 */

public class JwtTokenFilter extends GenericFilterBean {     //фильтер токенов

    private JwtTokenProvider jwtTokenProvider;      //наш класс обработки jwt токена


    public JwtTokenFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain filterChain)
            throws IOException, ServletException {
        String token = jwtTokenProvider.resolveToken((HttpServletRequest) req);     //получаем токен из запроса с помощью нашего метода
        if (token != null && jwtTokenProvider.validateToken(token)) {               //если токен не равен null, и его срок действия не истек тоо
            Authentication auth = jwtTokenProvider.getAuthentication(token);        //создаем пользователя на основе токена

            if (auth != null) {                                                     //и если такой пользовтель есть то
                SecurityContextHolder.getContext().setAuthentication(auth);         //авторизуем его в системе
            }
        }
        filterChain.doFilter(req, res);                 //передаем управление след фильтру цепочки
    }

}
