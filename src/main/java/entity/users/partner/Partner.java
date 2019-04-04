package entity.users.partner;

import entity.users.Account;
import entity.users.ConcreteRole;
import entity.users.user.User;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "partners")
public class Partner implements ConcreteRole {

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

    @OneToMany(mappedBy = "partner",
            fetch = FetchType.LAZY,
            cascade = CascadeType.PERSIST
    )
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Set<AdvertisingPlatform> platforms = new HashSet<>();

    public Partner() {}

    public Partner(User user) {
        this(null, user, new Account(BigDecimal.valueOf(0)));
    }

    public Partner(Long id, User user, Account account) {
        this.id = id;
        this.user = user;
        this.account = account;
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

    public Set<AdvertisingPlatform> getPlatforms() {
        return platforms;
    }

    public void setPlatforms(Set<AdvertisingPlatform> platforms) {
        this.platforms = platforms;
    }

    @Override
    public String toString() {
        return "Partner{" +
                "id=" + id +
                ", user=" + user +
                ", account=" + account +
                ", platforms=" + platforms +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Partner)) return false;
        Partner partner = (Partner) o;
        return Objects.equals(getUser(), partner.getUser());
    }

    @Override
    public int hashCode() {

        return Objects.hash(getUser());
    }
}
