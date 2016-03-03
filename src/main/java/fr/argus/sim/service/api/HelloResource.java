package fr.argus.sim.service.api;

import static fr.argus.sim.service.domain.OffreRoleAuthoritiesConstants.FREEMIUM_DECIDEUR;
import static fr.argus.sim.service.domain.OffreRoleAuthoritiesConstants.INITIAL_M360_DECIDEUR;
import static fr.argus.sim.service.domain.OffreRoleAuthoritiesConstants.INITIAL_M360_LECTEUR;
import static fr.argus.sim.service.domain.OffreRoleAuthoritiesConstants.INITIAL_VEILLE_DECIDEUR;
import static fr.argus.sim.service.domain.OffreRoleAuthoritiesConstants.INITIAL_VEILLE_LECTEUR;
import static fr.argus.sim.service.domain.OffreRoleAuthoritiesConstants.ONE_DECIDEUR;
import static fr.argus.sim.service.domain.OffreRoleAuthoritiesConstants.ONE_LECTEUR;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;

import fr.argus.sim.service.domain.Foo;

@Path("/")
public class HelloResource {

    private String getList(String name){
        return "Hello "+name;
        //        ArrayList<Integer> ret = new ArrayList<Integer>();
        //        ret.addAll(ImmutableList.of(1, 2, 3, 4, 5));
        //        return ret;
    }

    @GET
    @Path("hello")
    @Produces("text/plain")
    @PreAuthorize("hasPermission(#name, 'hello')")
    public String say(@QueryParam("name") String name) {
        return getList(name);
    }

    @GET
    @Path("hello1")
    @Produces("text/plain")
    @RolesAllowed({ INITIAL_M360_DECIDEUR, INITIAL_M360_LECTEUR, INITIAL_VEILLE_DECIDEUR, INITIAL_VEILLE_LECTEUR, ONE_DECIDEUR, ONE_LECTEUR, FREEMIUM_DECIDEUR})
    public String say1(@QueryParam("name") String name) {
        return getList(name);
    }

    @GET
    @Path("hello2")
    @Produces("text/plain")
//    @RolesAllowed({ INITIAL_M360_DECIDEUR, INITIAL_M360_LECTEUR, INITIAL_VEILLE_DECIDEUR, INITIAL_VEILLE_LECTEUR, ONE_DECIDEUR, ONE_LECTEUR, FREEMIUM_DECIDEUR})
    @PreAuthorize("hasAnyRole('INITIAL_M360_DECIDEUR','INITIAL_M360_LECTEUR','INITIAL_VEILLE_DECIDEUR','INITIAL_VEILLE_LECTEUR') or ( hasAnyRole('ONE_DECIDEUR','ONE_LECTEUR','FREEMIUM_DECIDEUR') and #name == null )")
    public String say2(@QueryParam("name") String name) {
        return getList(name);
    }

    @GET
    @Path("hello3")
    @Produces(MediaType.APPLICATION_JSON)
    @PostFilter("hasAnyRole('INITIAL_M360_DECIDEUR','INITIAL_M360_LECTEUR','INITIAL_VEILLE_DECIDEUR','INITIAL_VEILLE_LECTEUR') or ( hasAnyRole('ONE_DECIDEUR','ONE_LECTEUR','FREEMIUM_DECIDEUR') and filterObject.value < 2 )")
    public List<Foo> say3() {
        List<Foo> list =  new ArrayList<Foo>();
        list.add(new Foo(1));
        list.add(new Foo(2));
        list.add(new Foo(3));

        return list;
    }

}
