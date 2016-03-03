package fr.argus.sim;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

public class AuthenticationFilterDummy implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest)request;
        String roleRequest = httpRequest.getHeader(HttpHeaders.AUTHORIZATION);
        HttpSession session = httpRequest.getSession(true);

        List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
        if (!StringUtils.isEmpty(roleRequest)) {
            authorities.add(new SimpleGrantedAuthority(roleRequest));
        }
        Authentication authToken = new UsernamePasswordAuthenticationToken("a", "b", authorities);
        SecurityContext securityContext = SecurityContextHolder.getContext();
        securityContext.setAuthentication(authToken);
        session.setAttribute("SPRING_SECURITY_CONTEXT", securityContext);

        chain.doFilter(httpRequest, response);
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        SpringBeanAutowiringSupport.processInjectionBasedOnServletContext(this, filterConfig.getServletContext());
    }

    @Override
    public void destroy() {
    }


}
