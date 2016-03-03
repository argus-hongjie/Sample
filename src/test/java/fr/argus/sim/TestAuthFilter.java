package fr.argus.sim;

import java.io.IOException;
import java.security.Principal;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.ext.Provider;

@Provider
@Priority(Priorities.AUTHENTICATION)
public class TestAuthFilter implements ContainerRequestFilter {
    @Override
    public void filter(final ContainerRequestContext requestContext) throws IOException {
        requestContext.setSecurityContext(new SecurityContext() {
            @Override
            public Principal getUserPrincipal() {
                return new Principal() {

                    @Override public String getName() {
                        return "";
                    }
                };
            }

            @Override
            public boolean isUserInRole(String role) {
                String roleRequest = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);
                return roleRequest==null ? true : role.equals(roleRequest);
            }

            @Override
            public boolean isSecure() { return true; }

            @Override
            public String getAuthenticationScheme() {
                return "";
            }
        });
    }
}
