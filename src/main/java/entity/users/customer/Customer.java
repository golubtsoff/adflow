package entity.users.customer;

import entity.users.Account;
import entity.users.ConcreteRole;
import entity.users.user.User;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.*;

@Entity
@Table(name = "customers")
public class Customer implements ConcreteRole {

    public static final String ID = "ID";
    public static final String USER_ID = "USER_ID";

    @Id
    @Column(name = ID)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = USER_ID)
    private User user;

    private Account account;

    @OneToMany(mappedBy = "customer",
        fetch = FetchType.LAZY,
        cascade = CascadeType.PERSIST
    )
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Set<Campaign> campaigns = new HashSet<>();

    public Customer() {
    }

    public Customer(User user) {
        this(null, user);
    }

    public Customer(Long id, User user) {
        this.id = id;
        this.user = user;
        this.account = new Account(BigDecimal.valueOf(0));
    }

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public Set<Campaign> getCampaigns() {
        return campaigns;
    }

    public void setCampaigns(Set<Campaign> campaigns) {
        this.campaigns = campaigns;
    }

    @Override
    public String toString() {
        return "Customer{" +
                "id=" + id +
                ", user=" + user +
                ", account=" + account +
                ", campaigns=" + campaigns +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Customer)) return false;
        Customer customer = (Customer) o;
        return Objects.equals(getUser(), customer.getUser());
    }

    @Override
    public int hashCode() {

        return Objects.hash(getUser());
    }
}
