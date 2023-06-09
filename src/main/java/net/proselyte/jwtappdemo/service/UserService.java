package net.proselyte.jwtappdemo.service;

import net.proselyte.jwtappdemo.model.User;

import java.util.List;

/**
 * Service interface for class {@link User}.
 *
 * @author Eugene Suleimanov
 * @version 1.0
 */

public interface UserService {          //сервис для пользователей

    void register(User user);

    void login(String username, String password);

    List<User> getAll();

    User findByUsername(String username);

    User findById(Long id);

    void delete(Long id);
}
