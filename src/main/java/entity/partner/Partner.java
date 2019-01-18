package entity.partner;

import entity.Account;
import entity.user.User;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.util.Objects;

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

    @Column(name = "KEY")
    private String key;

    @Column(name = "IS_WORKING")
    private boolean isWorking;

    public Partner() {}

    public Partner(User user, Account account, String key, boolean isWorking) {
        this(null, user, account, key, isWorking);
    }

    public Partner(Long id, User user, Account account, String key, boolean isWorking) {
        this.id = id;
        this.user = user;
        this.account = account;
        this.key = key;
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

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
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
                ", key='" + key + '\'' +
                ", isWorking=" + isWorking +
                '}';
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Partner partner = (Partner) o;
        return isWorking == partner.isWorking &&
                Objects.equals(id, partner.id) &&
                Objects.equals(user, partner.user) &&
                Objects.equals(account, partner.account) &&
                Objects.equals(key, partner.key);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, user, account, key, isWorking);
    }
}
