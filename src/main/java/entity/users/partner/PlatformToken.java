package entity.users.partner;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Objects;

import static entity.users.user.UserToken.TOKEN_ISSUER;

@Entity
@Table(name = "PLATFORM_TOKENS")
public class PlatformToken {

    public static final String TOKEN = "token";
    public static final String RELEASED_DATE_TIME = "released_date_time";
    public static final String PID = "pid";

    @Id
    private Long id;

    @OneToOne(
            fetch = FetchType.EAGER,
            optional = false
    )
    @PrimaryKeyJoinColumn
    private Platform platform;

    @Column(name = TOKEN)
    private String token;

    @Column(name = RELEASED_DATE_TIME)
    private LocalDateTime releasedDateTime;

    public PlatformToken(){}

    public PlatformToken(Platform platform) throws Exception {
        id = platform.getId();
        assert (id != null);
        this.platform = platform;
        releasedDateTime = LocalDateTime.now();
        token = issueToken(id, getSecret(platform.getPartner().getUser().getPasswordHash(), releasedDateTime));
    }

    @NotNull
    private String getSecret(@NotNull String passwordHash, @NotNull LocalDateTime dt){
        return passwordHash + dt.toString();
    }

    @NotNull
    private String issueToken(long platformId, @NotNull String secret) throws Exception {
        token = "";
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            token = JWT.create()
                    .withIssuer(TOKEN_ISSUER)
                    .withClaim(PID, platformId)
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

    @NotNull
    public PlatformToken updateToken() throws Exception {
        releasedDateTime = LocalDateTime.now();
        token = issueToken(id, getSecret(platform.getPartner().getUser().getPasswordHash(), releasedDateTime));
        return this;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Platform getPlatform() {
        return platform;
    }

    public void setPlatform(Platform platform) {
        this.platform = platform;
    }

    @Override
    public String toString() {
        return "PlatformToken{" +
                "id=" + id +
                ", platform=" + platform.getId() +
                ", token='" + token + '\'' +
                ", releasedDateTime=" + releasedDateTime +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PlatformToken)) return false;
        PlatformToken token1 = (PlatformToken) o;
        return Objects.equals(getToken(), token1.getToken());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getToken());
    }
}