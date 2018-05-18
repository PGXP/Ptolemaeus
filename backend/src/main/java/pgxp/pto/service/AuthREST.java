package pgxp.pto.service;

import io.swagger.annotations.Api;
import pgxp.pto.dao.UserDAO;
import pgxp.pto.security.Credentials;
import pgxp.pto.security.Social;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import javax.ws.rs.core.Response;
import static javax.ws.rs.core.Response.ok;
import org.demoiselle.jee.security.annotation.Authenticated;
import pgxp.pto.security.Subscription;

/**
 *
 * @author paulo
 */
@Api("Auth")
@Path("auth")
@Produces(APPLICATION_JSON)
@Consumes(APPLICATION_JSON)
public class AuthREST {

    @Inject
    private UserDAO dao;

    /**
     *
     * @param credentials
     * @return
     */
    @POST
    @Transactional
    public Response login(@Valid Credentials credentials) {
        return ok().entity(dao.login(credentials).toString()).build();
    }

    /**
     *
     * @return
     */
    @GET
    @Transactional
    @Authenticated
    public Response retoken() {
        return ok().entity(dao.retoken().toString()).build();
    }

    /**
     *
     * @param credentials
     */
    @POST
    @Transactional
    @Path("register")
    public void register(Credentials credentials) {
        dao.register(credentials);
    }

    /**
     *
     * @param credentials
     * @return
     */
    @POST
    @Transactional
    @Path("amnesia")
    public Response amnesia(Credentials credentials) {
        return ok().entity(dao.amnesia(credentials)).build();
    }

    /**
     *
     * @param credentials
     * @return
     */
    @POST
    @Transactional
    @Path("change")
    public Response resenha(Credentials credentials) {
        return ok().entity(dao.resenha(credentials)).build();
    }

    /**
     *
     * @param social
     * @return
     */
    @POST
    @Transactional
    @Path("social")
    public Response social(Social social) {
        return ok().entity(dao.social(social).toString()).build();
    }

    /**
     *
     * @param fingerprint
     * @return
     */
    @POST
    @Transactional
    @Path("fingerprint")
    public Response fingerprint(Subscription fingerprint) {
        dao.setFirebase(fingerprint);
        return ok().entity("").build();
    }

    /**
     *
     * @return
     */
    @GET
    @Transactional
    @Path("check")
    public Response check() {
        return ok().build();
    }

}
