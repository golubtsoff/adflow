package entity.users.user;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import entity.users.Status;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "users")
@SecondaryTables({
        @SecondaryTable(name = "persons"),
        @SecondaryTable(name = "contacts"),
})
public class User {

    public static final String ID = "user_id";
    public static final String LOGIN = "login";
    public static final String PASSWORD_HASH = "password_hash";
    public static final String ROLE = "role";
    public static final String CREATION_DATE = "creation_date";
    public static final String STATUS = "status";

    @Id
    @Column(name = ID)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = LOGIN, unique = true, updatable = false, nullable = false)
    private String login;

    @Column(name = PASSWORD_HASH, nullable = false)
    private String hash;

    @Column(name = ROLE, nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(name = CREATION_DATE, nullable = false)
    private LocalDateTime creationDateTime;

    @AttributeOverrides({
            @AttributeOverride(name=Person.FIRSTNAME, column=@Column(table="persons")),
            @AttributeOverride(name=Person.LASTNAME, column=@Column(table="persons"))
    })
    private Person person;

    @AttributeOverrides({
            @AttributeOverride(name=Contact.EMAIL, column=@Column(table="contacts")),
            @AttributeOverride(name=Contact.PHONE, column=@Column(table="contacts"))
    })
    private Contact contact;

    @Column(name = STATUS, nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status;

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
        this.status = Status.CHECKING;
        this.creationDateTime = LocalDateTime.now();
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

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public LocalDateTime getCreationDateTime() {
        return creationDateTime;
    }

    public void setCreationDateTime(LocalDateTime creationDateTime) {
        this.creationDateTime = creationDateTime;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", login='" + login + '\'' +
                ", hash='" + hash + '\'' +
                ", role=" + role +
                ", creationDateTime=" + creationDateTime +
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