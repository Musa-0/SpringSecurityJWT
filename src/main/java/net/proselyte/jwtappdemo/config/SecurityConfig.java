package net.proselyte.jwtappdemo.config;

import net.proselyte.jwtappdemo.security.jwt.JwtConfigurer;
import net.proselyte.jwtappdemo.security.jwt.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Security configuration class for JWT based Spring Security application.
 *
 * @author Eugene Suleimanov
 * @version 1.0
 */

@EnableWebSecurity      //указываем что это конфиг для securityC
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    private static final String ADMIN_ENDPOINT = "/api/v1/admin/**";
    private static final String LOGIN_ENDPOINT = "/api/v1/auth/**";


    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {            //бин для кодирования пароля пользователя
        return new BCryptPasswordEncoder();
    }


    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .httpBasic().disable()
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)//отключаем сессии
                .and()
                .authorizeRequests()
                .antMatchers(LOGIN_ENDPOINT).permitAll()        //запрос на авторизацию доступен всем
                .antMatchers(ADMIN_ENDPOINT).hasRole("ADMIN")   //этот url лишь для админа
                .anyRequest().authenticated()       //любые другие запросы должны быть лишь для аутефицированых
                .and()
                .apply(new JwtConfigurer(jwtTokenProvider));        //и добавляем нашу конфигурацию для Jwt фильтра
    }
}
