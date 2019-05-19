package rest;

import entity.users.user.Role;
import entity.users.user.UserToken;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Roles
@Provider
@Priority(Priorities.AUTHORIZATION)
public class RolesFilter implements ContainerRequestFilter {

    @Context
    private ResourceInfo resourceInfo;

    @Context
    HttpHeaders headers;

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        Class<?> resourceClass = resourceInfo.getResourceClass();
        List<Role> classRoles = extractRoles(resourceClass);

        Method resourceMethod = resourceInfo.getResourceMethod();
        List<Role> methodRoles = extractRoles(resourceMethod);

        try {
            if (methodRoles.isEmpty()) {
                checkPermissions(classRoles);
            } else {
                checkPermissions(methodRoles);
            }
        } catch (Exception e) {
            requestContext.abortWith(
                    Response.status(Response.Status.FORBIDDEN).build());
        }
    }

    private List<Role> extractRoles(AnnotatedElement annotatedElement) {
        if (annotatedElement == null) {
            return new ArrayList<>();
        } else {
            Roles roles = annotatedElement.getAnnotation(Roles.class);
            if (roles == null) {
                return new ArrayList<Role>();
            } else {
                Role[] allowedRoles = roles.value();
                return Arrays.asList(allowedRoles);
            }
        }
    }

    private void checkPermissions(List<Role> allowedRoles) throws Exception {
        Role userRole = Role.valueOf(headers.getHeaderString(UserToken.ROLE).toUpperCase());
        for (Role allowedRole : allowedRoles) {
            if (allowedRole == userRole)
                return;
        }
        throw new Exception("Не хватает прав для доступа");
    }
}
