package entity.users.user;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "users")
@SecondaryTables({
        @SecondaryTable(name = "persons"),
        @SecondaryTable(name = "contacts"),
})
public class User {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "LOGIN", unique = true, updatable = false, nullable = false)
    private String login;

    @Column(name = "PASSWORD_HASH", nullable = false)
    private String hash;

    @Column(name = "ROLE", nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;

    @AttributeOverrides({
            @AttributeOverride(name="firstname", column=@Column(table="persons")),
            @AttributeOverride(name="lastname", column=@Column(table="persons"))
    })
    private Person person;

    @AttributeOverrides({
            @AttributeOverride(name="email", column=@Column(table="contacts")),
            @AttributeOverride(name="phone", column=@Column(table="contacts"))
    })
    private Contact contact;

    @Column(name = "STATUS", nullable = false)
    @Enumerated(EnumType.STRING)
    private UserStatus status;

    public User() {
    }

    public User(String login, String hash, Role role) {
        this(null, login, hash, role);
    }

    public User(Long id, String login, String hash, Role role) {
        this.id = id;
        this.login = login;
        this.hash = hash;
        this.role = role;
        this.status = UserStatus.CHECKING;
    }

    public Long getId() {
        return id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public Contact getContact() {
        return contact;
    }

    public void setContact(Contact contact) {
        this.contact = contact;
    }

    public UserStatus getStatus() {
        return status;
    }

    public void setStatus(UserStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", login='" + login + '\'' +
                ", hash='" + hash + '\'' +
                ", role=" + role +
                ", person=" + person +
                ", contact=" + contact +
                ", status=" + status +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        User user = (User) o;
        return Objects.equals(getLogin(), user.getLogin());
    }

    @Override
    public int hashCode() {

        return Objects.hash(getLogin());
    }
}