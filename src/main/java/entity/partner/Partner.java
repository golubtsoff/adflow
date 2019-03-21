package entity.partner;

import entity.Account;
import entity.user.User;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "partners")
public class Partner {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.EAGER)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "USER_ID")
    private User user;

    @OneToOne(fetch = FetchType.EAGER)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "ACCOUNT_ID")
    private Account account;

    @OneToMany(mappedBy = "partner",
            fetch = FetchType.LAZY,
            cascade = CascadeType.PERSIST
    )
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Set<AdvertisingPlatform> platforms = new HashSet<>();

    @Column(name = "IS_WORKING")
    private boolean isWorking;

    public Partner() {}

    public Partner(User user, Account account, boolean isWorking) {
        this(null, user, account, isWorking);
    }

    public Partner(Long id, User user, Account account, boolean isWorking) {
        this.id = id;
        this.user = user;
        this.account = account;
        this.isWorking = isWorking;
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

    public boolean isWorking() {
        return isWorking;
    }

    public void setWorking(boolean working) {
        isWorking = working;
    }


    @Override
    public String toString() {
        return "Partner{" +
                "id=" + id +
                ", user=" + user +
                ", account=" + account +
                ", isWorking=" + isWorking +
                '}';
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Partner partner = (Partner) o;
        return isWorking == Objects.equals(id, partner.id) &&
                Objects.equals(user, partner.user) &&
                Objects.equals(account, partner.account);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, user, account);
    }
}
