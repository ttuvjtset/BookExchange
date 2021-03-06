package ee.ttu.BookExchange.api.models;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.sql.Timestamp;

@Entity
@Getter
@Setter
public class Users {
    @Id
    @GeneratedValue
    @Column(nullable = false)
    int id;

    @Column(nullable = false)
    @Size(max = 32)
    String username;

    @Column(nullable = false)
    @Size(max = 64)
    String email;

    @Size(max = 128)
    String full_name;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "city", referencedColumnName = "id")
    City city;

    @Column(nullable = false)
    @Size(max = 66)
    String pass_hash;

    @Column(nullable = false)
    @Size(max = 12)
    String pass_salt;

    @Column(nullable = false, columnDefinition = "tinyint(1) default 0")
    byte isverified = 0;

    @Column(nullable = false, columnDefinition = "timestamp")
    Timestamp regdate;

    @Size(max = 16)
    String phone;
}
