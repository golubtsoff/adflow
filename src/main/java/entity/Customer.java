package entity;

import entity.campaign.Campaign;
import entity.user.User;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.util.*;

@Entity
@Table(name = "customers")
public class Customer {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.EAGER)
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

    @Column(name = "REMOVED")
    private boolean removed;

    public Customer() {
    }

    public Customer(User user) {
        this(null, user);
    }

    public Customer(Long id, User user) {
        this.id = id;
        this.user = user;
        this.account = account;
        this.removed = false;
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

    public boolean isRemoved() {
        return removed;
    }

    public void setRemoved(boolean removed) {
        this.removed = removed;
    }

    @Override
    public String toString() {
        return "Customer{" +
                "id=" + id +
                ", user=" + user +
                ", account=" + account +
                ", removed=" + removed +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Customer customer = (Customer) o;
        return removed == customer.removed &&
                Objects.equals(id, customer.id) &&
                Objects.equals(user, customer.user) &&
                Objects.equals(account, customer.account);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, user, account, removed);
    }
}
