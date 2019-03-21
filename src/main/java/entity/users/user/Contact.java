package entity.users.user;

import javax.persistence.*;
import java.util.Objects;

@Embeddable
public class Contact {

    @Column(name = "email", unique = true, updatable = true)
    private String email;

    @Column(name = "phone", unique = true, updatable = true)
    private String telephone;

    public Contact(){}

    public Contact(String email, String telephone) {
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

    @Override
    public String toString() {
        return "Contact{" +
                ", email='" + email + '\'' +
                ", telephone='" + telephone + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Contact contact = (Contact) o;
        return Objects.equals(email, contact.email) &&
                Objects.equals(telephone, contact.telephone);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email, telephone);
    }
}
