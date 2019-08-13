package rest.statistics;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import entity.users.Action;
import entity.users.partner.PlatformToken;
import service.PlatformService;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

@PlatformSecure
@Provider
@Priority(Priorities.AUTHENTICATION)
public class PlatformFilter implements ContainerRequestFilter {
    private static final String AUTHENTICATION_SCHEME = "Platform";

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
            Long id = validateToken(token);
            if (id == null){
                abortWithUnauthorized(requestContext);
                return;
            }
            requestContext.getHeaders().add(PlatformToken.PID, Long.toString(id));
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

    private Long validateToken(String token) throws Exception {
        DecodedJWT jwt;
        try {
            jwt = JWT.decode(token);
            Long id = jwt.getClaim(PlatformToken.PID).asLong();
            PlatformToken savedToken = PlatformService.getToken(id);
            if (savedToken != null
                    && savedToken.getPlatform().getAction() == Action.RUN
                    && savedToken.getToken().equals(token)){
                return id;
            }
            return null;
        } catch (Exception e){
            throw new Exception(e);
        }
    }
}
