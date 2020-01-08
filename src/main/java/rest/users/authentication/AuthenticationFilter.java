package rest.users.authentication;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import entity.users.Status;
import entity.users.user.Role;
import entity.users.user.UserToken;
import service.UserService;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

@Secured
@Provider
@Priority(Priorities.AUTHENTICATION)
public class AuthenticationFilter implements ContainerRequestFilter {
    private static final String AUTHENTICATION_SCHEME = "Basic";

    private class Validation{
        private long id;
        private Role role;
    }

    @Override
    public void filter(ContainerRequestContext requestContext){

        String authorizationHeader =
                requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);

        if (!isTokenBasedAuthentication(authorizationHeader)) {
            abortWithUnauthorized(requestContext);
            return;
        }

        String token = authorizationHeader
                .substring(AUTHENTICATION_SCHEME.length()).trim();

        try {
            Validation validation = validateToken(token);
            if (validation == null){
                abortWithUnauthorized(requestContext);
                return;
            }
            requestContext.getHeaders().add(UserToken.UID, Long.toString(validation.id));
            requestContext.getHeaders().add(UserToken.ROLE, validation.role.toString());
        } catch (Exception e) {
            abortWithUnauthorized(requestContext);
        }
    }

    private boolean isTokenBasedAuthentication(String authorizationHeader) {
        return authorizationHeader != null && authorizationHeader.toLowerCase()
                .startsWith(AUTHENTICATION_SCHEME.toLowerCase() + " ");
    }

    private void abortWithUnauthorized(ContainerRequestContext requestContext) {
        requestContext.abortWith(
                Response.status(Response.Status.UNAUTHORIZED)
                        .header(HttpHeaders.WWW_AUTHENTICATE,
                                AUTHENTICATION_SCHEME)
                        .build());
    }

    private Validation validateToken(String token) throws Exception {
        DecodedJWT jwt;
        try {
            Validation validation = new Validation();
            jwt = JWT.decode(token);
            validation.id = jwt.getClaim(UserToken.UID).asLong();
            UserToken savedToken = UserService.getToken(validation.id);
            if (savedToken.getUser().getStatus() == Status.WORKING
                    && savedToken.updateExpiredDateTime()
                    && savedToken.getToken().equals(token)){
                UserService.setToken(savedToken);
                validation.role = Role.valueOf(jwt.getClaim(UserToken.ROLE).asString());
                return validation;
            }
            return null;
        } catch (Exception e){
            throw new Exception(e);
        }
    }
}
