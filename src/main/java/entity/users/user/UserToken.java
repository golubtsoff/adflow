package entity.users.user;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

@Entity
@Table(name = "USER_TOKENS")
public class UserToken {

    private static long ACTION_TIME_MINUTES = 30;
    private static long EXTENDED_TIME_MINUTES = 15;

    @Id
    private Long id;

    @OneToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @PrimaryKeyJoinColumn
    private User user;

    @Column(name = "TOKEN")
    private String token;

    @Column(name = "RELEASE_DATE_TIME")
    private LocalDateTime releasedDateTime;

    @Column(name = "EXPIRED_DATE_TIME")
    private LocalDateTime expiredDateTime;

    public UserToken(){}

    public UserToken(User user) throws Exception {
        id = user.getId();
        assert (id != null);
        this.user = user;
        releasedDateTime = LocalDateTime.now();
        expiredDateTime = releasedDateTime.plusMinutes(ACTION_TIME_MINUTES);
        token = issueToken(id, getSecret(user.getHash(), releasedDateTime));
    }

    @NotNull
    private String getSecret(@NotNull String passwordHash, @NotNull LocalDateTime dt){
        return passwordHash + dt.toString();
    }

    @NotNull
    private String issueToken(long userId, @NotNull String secret) throws Exception {
        token = "";
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            token = JWT.create()
                    .withIssuer("app4pro.ru")
                    .withClaim("uid", userId)
                    .sign(algorithm);
        } catch (JWTCreationException exception){
            throw new Exception(exception);
        }
        return token;
    }

    public String getToken() {
        return token;
    }

    public LocalDateTime getDateTime() {
        return releasedDateTime;
    }

    @NotNull
    public UserToken updateToken() throws Exception {
        releasedDateTime = LocalDateTime.now();
        expiredDateTime = releasedDateTime.plusMinutes(ACTION_TIME_MINUTES);
        token = issueToken(id, getSecret(user.getHash(), releasedDateTime));
        return this;
    }

    public boolean updateExpiredDateTime(){
        long minutes = ChronoUnit.MINUTES.between(LocalDateTime.now(), expiredDateTime);
        if (minutes < 0) return false;
        if (minutes < ACTION_TIME_MINUTES)
            expiredDateTime = expiredDateTime.plusMinutes(EXTENDED_TIME_MINUTES);
        return true;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "Token{" +
                "id=" + id +
                ", user=" + user +
                ", token='" + token + '\'' +
                ", releasedDateTime=" + releasedDateTime +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserToken)) return false;
        UserToken token1 = (UserToken) o;
        return Objects.equals(getId(), token1.getId()) &&
                Objects.equals(getToken(), token1.getToken());
    }

    @Override
    public int hashCode() {

        return Objects.hash(getId(), getToken());
    }
}