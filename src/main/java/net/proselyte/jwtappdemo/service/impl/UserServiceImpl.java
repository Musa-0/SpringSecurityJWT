package net.proselyte.jwtappdemo.service.impl;

import lombok.extern.slf4j.Slf4j;
import net.proselyte.jwtappdemo.model.Role;
import net.proselyte.jwtappdemo.model.Status;
import net.proselyte.jwtappdemo.model.User;
import net.proselyte.jwtappdemo.repository.RoleRepository;
import net.proselyte.jwtappdemo.repository.UserRepository;
import net.proselyte.jwtappdemo.security.jwt.JwtTokenProvider;
import net.proselyte.jwtappdemo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of {@link UserService} interface.
 * Wrapper for {@link UserRepository} + business logic.
 *
 * @author Eugene Suleimanov
 * @version 1.0
 */

@Service
@Slf4j      //логирование
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;        //хеширование пароля
    private final AuthenticationManager authenticationManager;



    @Autowired          //инициализируем
    public UserServiceImpl(AuthenticationManager authenticationManager,
                           UserRepository userRepository,
                           RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.authenticationManager = authenticationManager;

    }

    @Override
    public void register(User user) {                               //метод регистрации пользователя
        Role roleUser = roleRepository.findByName("ROLE_USER");     //создаем список ролей для нового юзера
        List<Role> userRoles = new ArrayList<>();
        userRoles.add(roleUser);
        String password = user.getPassword();

        user.setPassword(passwordEncoder.encode(password));       //хешируем пароль
        user.setRoles(userRoles);                                           //устанавливаем роли
        user.setStatus(Status.ACTIVE);                                      //и активируем аккаунт

        User registeredUser = userRepository.save(user);                    //сохраняем пользователя в бд

        log.info("IN register - user: {} successfully registered", registeredUser);
        login(user.getUsername(),password);
    }

    @Override
    public void login(String username, String password) {
        try {
            User user = findByUsername(username);

            if (user == null) {
                throw new UsernameNotFoundException("User with username: " + username + " not found");
            }
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));    //логиним пользователя

        } catch (AuthenticationException e) {
            throw new BadCredentialsException("Invalid username or password");
        }
    }

    @Override
    public List<User> getAll() {            //возвращаем всех пользователей
        List<User> result = userRepository.findAll();
        log.info("IN getAll - {} users found", result.size());
        return result;
    }

    @Override
    public User findByUsername(String username) {           //возвращаем пользователя по имени
        User result = userRepository.findByUsername(username);
        log.info("IN findByUsername - user: {} found by username: {}", result, username);
        return result;
    }

    @Override
    public User findById(Long id) {                             //возвращаем пользоватлея по id
        User result = userRepository.findById(id).orElse(null);  //если нет, то вернем null

        if (result == null) {
            log.warn("IN findById - no user found by id: {}", id);
            return null;
        }

        log.info("IN findById - user: {} found by id: {}", result);
        return result;
    }

    @Override
    public void delete(Long id) {               //удаляем пользователя по id
        userRepository.deleteById(id);
        log.info("IN delete - user with id: {} successfully deleted");
    }
}
