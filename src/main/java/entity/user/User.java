package entity.user;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "users")
public class User {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "LOGIN", unique = true, updatable = false)
    private String login;

    @Column(name = "PASSWORD_HASH")
    private String hash;

    @Column(name = "ROLE")
    @Enumerated(EnumType.ORDINAL)
    private Role role;

    @OneToOne(fetch = FetchType.EAGER)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "PERSON_ID")
    private Person person;

    @OneToOne(fetch = FetchType.EAGER)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "CONTACT_ID")
    private Contact contact;

    public User(){}

    public User(String login, String hash, Role role){
        this(null, login, hash, role, null, null);
    }

    public User(Long id, String login, String hash, Role role){
        this(id, login, hash, role, null, null);
    }

    public User(Long id, String login, String hash, Role role, Person person, Contact contact){
        this.id = id;
        this.login = login;
        this.hash = hash;
        this.role = role;
        this.person = person;
        this.contact = contact;
    }

    public Long getId() {
        return id;
    }

    public String getLogin() {
        return login;
    }

    public String getHash() {
        return hash;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", login='" + login + '\'' +
                ", hash='" + hash + '\'' +
                ", role=" + role +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id) &&
                Objects.equals(login, user.login) &&
                Objects.equals(hash, user.hash) &&
                role == user.role;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, login, hash, role);
    }
}
