package entity;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "contacts")
public class Contact {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "email", unique = true, updatable = true)
    private String email;

    @Column(name = "telephone", unique = true, updatable = true)
    private String telephone;

    public Contact(){}

    public Contact(String email, String telephone) {
        this(null, email, telephone);
    }

    public Contact(Long id, String email, String telephone) {
        this.id = id;
        this.email = email;
        this.telephone = telephone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public Long getId() {
        return id;
    }

    @Override
    public String toString() {
        return "Contact{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", telephone='" + telephone + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Contact contact = (Contact) o;
        return Objects.equals(id, contact.id) &&
                Objects.equals(email, contact.email) &&
                Objects.equals(telephone, contact.telephone);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, email, telephone);
    }
}
