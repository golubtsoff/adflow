package entity.users.customer;

import entity.users.Account;
import entity.users.UserStatus;
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

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "USER_ID")
    private User user;

     private Account account;

    @OneToMany(mappedBy = "customer",
        fetch = FetchType.LAZY,
        cascade = CascadeType.PERSIST
    )
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Set<Campaign> campaigns = new HashSet<>();

    @Column(name = "STATUS")
    @Enumerated(EnumType.STRING)
    private UserStatus status;

    public Customer() {
    }

    public Customer(User user) {
        this(null, user);
    }

    public Customer(Long id, User user) {
        this.id = id;
        this.user = user;
        this.account = new Account(BigDecimal.valueOf(0));
        this.status = UserStatus.CHECKING;
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

    public UserStatus getStatus() {
        return status;
    }

    public void setStatus(UserStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Customer{" +
                "id=" + id +
                ", user=" + user +
                ", account=" + account +
                ", campaigns=" + campaigns +
                ", status=" + status +
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
