package entity.users.user;

import javax.persistence.*;
import java.util.Objects;

@Embeddable
public class Contact {

    public static final String EMAIL = "EMAIL";
    public static final String PHONE = "PHONE";

    @Column(name = EMAIL, unique = true, updatable = true)
    private String email;

    @Column(name = PHONE, unique = true, updatable = true)
    private String phone;

    public Contact(){}

    public Contact(String email, String phone) {
        this.email = email;
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Override
    public String toString() {
        return "Contact{" +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Contact contact = (Contact) o;
        return Objects.equals(email, contact.email) &&
                Objects.equals(phone, contact.phone);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email, phone);
    }
}
