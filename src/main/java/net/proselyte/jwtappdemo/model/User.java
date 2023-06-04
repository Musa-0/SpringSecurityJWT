package net.proselyte.jwtappdemo.model;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import java.util.List;


/**
 * Simple domain object that represents application user.
 *
 * @author Eugene Suleimanov
 * @version 1.0
 */

@Entity
@Table(name = "users")
@Data
public class User extends BaseEntity {

    @Column(name = "username")
    private String username;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "email")
    private String email;

    @Column(name = "password")
    private String password;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_roles",                     //создаем промежуточную таблицу
            joinColumns = {@JoinColumn(name = "user_id", referencedColumnName = "id")},         //первая колонка с именем user_id которая указывает на поле id таблицы User
            inverseJoinColumns = {@JoinColumn(name = "role_id", referencedColumnName = "id")})  //вторая колонка с именем role_id указывает на поле id таблицы Role
    private List<Role> roles;
}
