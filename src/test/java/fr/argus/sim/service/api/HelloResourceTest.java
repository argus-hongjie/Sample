package fr.argus.sim.service.api;

import static fr.argus.sim.service.domain.OffreRoleAuthoritiesConstants.FREEMIUM_DECIDEUR;
import static fr.argus.sim.service.domain.OffreRoleAuthoritiesConstants.INITIAL_ENGAGEMENT_DECIDEUR;
import static fr.argus.sim.service.domain.OffreRoleAuthoritiesConstants.INITIAL_M360_DECIDEUR;
import static fr.argus.sim.service.domain.OffreRoleAuthoritiesConstants.INITIAL_M360_LECTEUR;
import static fr.argus.sim.service.domain.OffreRoleAuthoritiesConstants.INITIAL_VEILLE_DECIDEUR;
import static fr.argus.sim.service.domain.OffreRoleAuthoritiesConstants.INITIAL_VEILLE_LECTEUR;
import static fr.argus.sim.service.domain.OffreRoleAuthoritiesConstants.ONE_DECIDEUR;
import static fr.argus.sim.service.domain.OffreRoleAuthoritiesConstants.ONE_LECTEUR;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.context.ContextLoaderListener;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import com.google.common.collect.ImmutableList;
import com.sun.jersey.spi.spring.container.servlet.SpringServlet;

import fr.argus.sim.AuthenticationFilterDummy;
import fr.argus.sim.service.domain.Foo;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:applicationContext.xml")
public class HelloResourceTest {
    protected static Server server;

    @Before
    public void setUpJetty() throws Exception {
        server = new Server(8484);

        // On lance le serveur Jetty
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);

        context.setDisplayName("Jetty Spring Security Webapp");
        context.setContextPath("/");
        context.getInitParams().put("contextConfigLocation", "classpath:applicationContext.xml");
        context.addEventListener(new ContextLoaderListener());




        FilterHolder springSecurityFilter = new FilterHolder(org.springframework.web.filter.DelegatingFilterProxy.class);
        springSecurityFilter.setName("springSecurityFilterChain");
        context.addFilter(springSecurityFilter, "/*", 1);

        FilterHolder springSecurityFilter2 = new FilterHolder(AuthenticationFilterDummy.class);
        springSecurityFilter2.setName("automatic_authentication");
        context.addFilter(springSecurityFilter2, "/*", 1);

        context.addServlet(SpringServlet.class, "/*");

        server.setHandler(context);
        server.start();
    }

    @After
    public void tearDownJetty() throws Exception {
        if (server != null) {
            server.stop();
        }
    }

    @Test
    public void testSayHello() {
        Client client = ClientBuilder.newClient();

        WebTarget r = client.target("http://localhost:8484").path("/hello");
        Response cr = r.queryParam("name", "loic").request().get();
        assertEquals(403, cr.getStatus());

        r = client.target("http://localhost:8484").path("/hello");
        cr = r.queryParam("name", "Loïc").request().get();
        assertEquals(200, cr.getStatus());
    }

    @Test
    public void testSayHello1() {
        Client client = ClientBuilder.newClient();
        WebTarget r = client.target("http://localhost:8484").path("/hello1");

        for (String role : ImmutableList.of(INITIAL_ENGAGEMENT_DECIDEUR)){
            Response cr = r.request().header(HttpHeaders.AUTHORIZATION, role).get();
            assertEquals(403, cr.getStatus());
        }

        for (String role : ImmutableList.of(INITIAL_M360_DECIDEUR, INITIAL_M360_LECTEUR, INITIAL_VEILLE_DECIDEUR, INITIAL_VEILLE_LECTEUR, ONE_DECIDEUR, ONE_LECTEUR, FREEMIUM_DECIDEUR)){
            Response cr = r.request().header(HttpHeaders.AUTHORIZATION, role).get();
            assertEquals(200, cr.getStatus());
        }
    }

    @Test
    public void testSayHello2() {
        Client client = ClientBuilder.newClient();
        WebTarget r = client.target("http://localhost:8484").path("/hello2");

        for (String role : ImmutableList.of(INITIAL_ENGAGEMENT_DECIDEUR)){
            Response cr = r.queryParam("name", "foo").request().header(HttpHeaders.AUTHORIZATION, role).get();
            assertEquals(403, cr.getStatus());
        }
        for (String role : ImmutableList.of(INITIAL_ENGAGEMENT_DECIDEUR)){
            Response cr = r.request().header(HttpHeaders.AUTHORIZATION, role).get();
            assertEquals(403, cr.getStatus());
        }


        for (String role : ImmutableList.of(ONE_DECIDEUR, ONE_LECTEUR, FREEMIUM_DECIDEUR)){
            Response cr = r.queryParam("name", "foo").request().header(HttpHeaders.AUTHORIZATION, role).get();
            assertEquals(403, cr.getStatus());
        }
        for (String role : ImmutableList.of(ONE_DECIDEUR, ONE_LECTEUR, FREEMIUM_DECIDEUR)){
            Response cr = r.request().header(HttpHeaders.AUTHORIZATION, role).get();
            assertEquals(200, cr.getStatus());
        }


        for (String role : ImmutableList.of(INITIAL_M360_DECIDEUR, INITIAL_M360_LECTEUR, INITIAL_VEILLE_DECIDEUR, INITIAL_VEILLE_LECTEUR)){
            Response cr = r.queryParam("name", "foo").request().header(HttpHeaders.AUTHORIZATION, role).get();
            assertEquals(200, cr.getStatus());
        }
        for (String role : ImmutableList.of(INITIAL_M360_DECIDEUR, INITIAL_M360_LECTEUR, INITIAL_VEILLE_DECIDEUR, INITIAL_VEILLE_LECTEUR)){
            Response cr = r.request().header(HttpHeaders.AUTHORIZATION, role).get();
            assertEquals(200, cr.getStatus());
        }
    }


    @Test
    public void testSayHello3() throws IOException {
        Client client = ClientBuilder.newClient().register(JacksonJsonProvider.class);
        WebTarget target = client.target("http://localhost:8484").path("/hello3");
        ObjectMapper mapper = new ObjectMapper();

        for (String role : ImmutableList.of(INITIAL_ENGAGEMENT_DECIDEUR)){
            JsonNode response = target.request(MediaType.APPLICATION_JSON_TYPE).header(HttpHeaders.AUTHORIZATION, role).get(JsonNode.class);  // Successful
            List<Foo> list = mapper.readValue(mapper.treeAsTokens(response), new TypeReference<List<Foo>>(){});
            assertEquals(0,list.size());
        }

        for (String role : ImmutableList.of(ONE_DECIDEUR, ONE_LECTEUR, FREEMIUM_DECIDEUR)){
            JsonNode response = target.request(MediaType.APPLICATION_JSON_TYPE).header(HttpHeaders.AUTHORIZATION, role).get(JsonNode.class);  // Successful
            List<Foo> list = mapper.readValue(mapper.treeAsTokens(response), new TypeReference<List<Foo>>(){});

            List<Foo> tmp = new ArrayList<Foo>();
            for(Foo foo : list){
                if (foo.getValue() >= 2) tmp.add(foo);
            }
            assertEquals(0, tmp.size());
        }

        for (String role : ImmutableList.of(INITIAL_M360_DECIDEUR, INITIAL_M360_LECTEUR, INITIAL_VEILLE_DECIDEUR, INITIAL_VEILLE_LECTEUR)){
            JsonNode response = target.request(MediaType.APPLICATION_JSON_TYPE).header(HttpHeaders.AUTHORIZATION, role).get(JsonNode.class);  // Successful
            List<Foo> list = mapper.readValue(mapper.treeAsTokens(response), new TypeReference<List<Foo>>(){});

            List<Foo> tmp = new ArrayList<Foo>();
            for(Foo foo : list){
                if (foo.getValue() >= 2) tmp.add(foo);
            }
            assertNotEquals(0, tmp.size());
        }
    }
}
