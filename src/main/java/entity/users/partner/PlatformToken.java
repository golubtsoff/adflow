package entity.users.partner;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import entity.users.user.User;
import javafx.application.Platform;
import org.junit.Assert;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "PLATFORM_TOKENS")
public class PlatformToken {

    @Id
    private Long id;

    @OneToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @PrimaryKeyJoinColumn
    private AdvertisingPlatform platform;

    @Column(name = "TOKEN")
    private String token;

    @Column(name = "RELEASED_DATE_TIME")
    private LocalDateTime releasedDateTime;

    public PlatformToken(){}

    public PlatformToken(AdvertisingPlatform platform) throws Exception {
        id = platform.getId();
        assert (id != null);
        this.platform = platform;
        releasedDateTime = LocalDateTime.now();
        token = issueToken(id, getSecret(platform.getPartner().getUser().getHash(), releasedDateTime));
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
                    .withIssuer("app4pro.ru")
                    .withClaim("pid", platformId)
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
        token = issueToken(id, getSecret(platform.getPartner().getUser().getHash(), releasedDateTime));
        return this;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public AdvertisingPlatform getPlatform() {
        return platform;
    }

    public void setPlatform(AdvertisingPlatform platform) {
        this.platform = platform;
    }

    @Override
    public String toString() {
        return "PlatformToken{" +
                "id=" + id +
                ", platform=" + platform +
                ", token='" + token + '\'' +
                ", releasedDateTime=" + releasedDateTime +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PlatformToken)) return false;
        PlatformToken token1 = (PlatformToken) o;
        return Objects.equals(getId(), token1.getId()) &&
                Objects.equals(getToken(), token1.getToken());
    }

    @Override
    public int hashCode() {

        return Objects.hash(getId(), getToken());
    }
}