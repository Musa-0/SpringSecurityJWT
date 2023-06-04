package net.proselyte.jwtappdemo.model;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.util.Date;

/**
 * Base class with property 'id'.
 * Used as a base class for all objects that requires this property.
 *
 * @author Eugene Suleimanov
 * @version 1.0
 */

@MappedSuperclass  //говорит о том что для этого класса не нужна таблица.
                                        // класс с полями которые имеют все другие таблицы
@Data
public class BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreatedDate            //создает дату создания автоматически (spring аннотация)
    @Column(name = "created")
    private Date created;

    @LastModifiedDate       //создает дату последнего изменения
    @Column(name = "updated")
    private Date updated;

    @Enumerated(EnumType.STRING)        //меняет тип enum на sptring при записи в бд
    @Column(name = "status")
    private Status status;
}
