package entity.users.user;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

@Entity
@Table(name = "USER_TOKENS")
public class UserToken {

    private static final long ACTION_TIME_MINUTES = 30;
    private static final long EXTENDED_TIME_MINUTES = 15;

    public static final String TOKEN = "token";
    public static final String RELEASE_DATE_TIME = "release_date_time";
    public static final String EXPIRED_DATE_TIME = "expired_date_time";

    public static final String TOKEN_ISSUER = "app4pro.ru";
    public static final String UID = "uid";
    public static final String ROLE = "role";

    @Id
    private Long id;

    @OneToOne(
            fetch = FetchType.EAGER,
            optional = false
    )
    @PrimaryKeyJoinColumn
    private User user;

    @Column(name = TOKEN)
    private String token;

    @Column(name = RELEASE_DATE_TIME)
    private LocalDateTime releasedDateTime;

    @Column(name = EXPIRED_DATE_TIME)
    private LocalDateTime expiredDateTime;

    public UserToken(){}

    public UserToken(User user) throws Exception {
        id = user.getId();
        assert (id != null);
        this.user = user;
        releasedDateTime = LocalDateTime.now();
        expiredDateTime = releasedDateTime.plusMinutes(ACTION_TIME_MINUTES);
        token = issueToken(id, user.getRole(), getSecret(user.getPasswordHash(), releasedDateTime));
    }

    @NotNull
    private String getSecret(@NotNull String passwordHash, @NotNull LocalDateTime dt){
        return passwordHash + dt.toString();
    }

    @NotNull
    private String issueToken(long userId, Role role, @NotNull String secret) throws Exception {
        token = "";
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            token = JWT.create()
                    .withIssuer(TOKEN_ISSUER)
                    .withClaim(UID, userId)
                    .withClaim(ROLE, role.toString())
                    .sign(algorithm);
        } catch (JWTCreationException exception){
            throw new Exception(exception);
        }
        return token;
    }

    public String getToken() {
        return token;
    }

    public LocalDateTime getReleasedDateTime() {
        return releasedDateTime;
    }

    public LocalDateTime getExpiredDateTime() {
        return expiredDateTime;
    }

    @NotNull
    public UserToken updateToken() throws Exception {
        releasedDateTime = LocalDateTime.now();
        expiredDateTime = releasedDateTime.plusMinutes(ACTION_TIME_MINUTES);
        token = issueToken(id, user.getRole(), getSecret(user.getPasswordHash(), releasedDateTime));
        return this;
    }

    public boolean updateExpiredDateTime(){
        if (!isActual()) return false;
        long minutes = ChronoUnit.MINUTES.between(LocalDateTime.now(), expiredDateTime);
        if (minutes < ACTION_TIME_MINUTES)
            expiredDateTime = expiredDateTime.plusMinutes(EXTENDED_TIME_MINUTES);
        return true;
    }

    private boolean isActual(){
        return LocalDateTime.now().isBefore(expiredDateTime);
    }

    public void setExpired(){
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(expiredDateTime))
            expiredDateTime = now;
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