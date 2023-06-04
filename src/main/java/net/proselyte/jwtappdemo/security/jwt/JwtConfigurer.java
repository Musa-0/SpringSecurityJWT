package net.proselyte.jwtappdemo.security.jwt;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * JWT configuration for application that add {@link JwtTokenFilter} for security chain.
 *
 * @author Eugene Suleimanov
 * @version 1.0
 */

public class JwtConfigurer extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {     //настройка для фильтра Jwt
    private JwtTokenProvider jwtTokenProvider;

    public JwtConfigurer(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public void configure(HttpSecurity httpSecurity) throws Exception {         //указываем нашей конфигурации Security
                        // чтобы http запрос проходил сперва через наш созданный фильтр. а уже потом попадал на сервер
        System.out.println(jwtTokenProvider);

        JwtTokenFilter jwtTokenFilter = new JwtTokenFilter(jwtTokenProvider);   //создаем объект нашего класса JwtTokenFilter
        httpSecurity.addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class);   //и добавляем фильтр в цепочку фильтров
    }
}
