package entity.users.user;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "tokens")
public class Token {

    @OneToOne(fetch = FetchType.EAGER)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "USER_ID")
    private Long id;

    @Column(name = "TOKEN")
    private String token;

    @Column(name = "RELEASE_DATE_TIME")
    private LocalDateTime dateTime;

    public Token(){}

    public Token(long userId, @NotNull String passwordHash) throws Exception {
        id = userId;
        dateTime = LocalDateTime.now();
        token = issueToken(userId, getSecret(passwordHash, dateTime));
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
                    .withClaim("pid", userId)
                    .sign(algorithm);
        } catch (JWTCreationException exception){
            throw new Exception(exception);
        }
        return token;
    }

    public Long getId() {
        return id;
    }

    public String getToken() {
        return token;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    @NotNull
    public Token updateToken(@NotNull String passwordHash) throws Exception {
        dateTime = LocalDateTime.now();
        token = issueToken(id, getSecret(passwordHash, dateTime));
        return this;
    }

    @Override
    public String toString() {
        return "Token{" +
                "id=" + id +
                ", token='" + token + '\'' +
                ", dateTime=" + dateTime +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Token)) return false;
        Token token1 = (Token) o;
        return Objects.equals(getId(), token1.getId()) &&
                Objects.equals(getToken(), token1.getToken()) &&
                Objects.equals(getDateTime(), token1.getDateTime());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getToken(), getDateTime());
    }
}