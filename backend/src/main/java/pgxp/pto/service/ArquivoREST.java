package pgxp.pto.service;

import pgxp.pto.entity.Arquivo;
import java.util.UUID;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import java.util.concurrent.ExecutorService;
import javax.ejb.Asynchronous;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.MediaType;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import javax.ws.rs.core.Response;
import org.demoiselle.jee.core.api.crud.Result;
import org.demoiselle.jee.crud.AbstractREST;
import org.demoiselle.jee.crud.Search;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;
import pgxp.pto.bc.ArquivoBC;

@Asynchronous
@Api("v1/Arquivos")
@Path("v1/arquivos")
public class ArquivoREST extends AbstractREST< Arquivo, UUID> {

    @GET
    @Override
    @Transactional
    @Search(fields = {"id", "description"}, withPagination = false) // Escolha quais campos vão para o frontend Ex: {"id", "description"}
    public Result find() {
        return bc.find();
    }

    @POST
    @Path(value = "upload")
    @Transactional
    @Consumes(value = MediaType.MULTIPART_FORM_DATA)
    public void salvarAnexo(@Suspended final AsyncResponse asyncResponse, final MultipartFormDataInput input) {
        asyncResponse.resume(doSalvarAnexo(input));
    }

    private Response doSalvarAnexo(MultipartFormDataInput input) {
        ((ArquivoBC) bc).salvarAnexo(input);
        return Response.ok().build();
    }

    @GET
    @Transactional
    @Path(value = "fts/{term}")
    @Consumes(value = APPLICATION_JSON)
    @Produces(value = APPLICATION_JSON)
    public void findFts(@Suspended final AsyncResponse asyncResponse, @PathParam(value = "term") final String texto) {
        asyncResponse.resume(doFindFts(texto));
    }

    private Response doFindFts(@PathParam("term") String texto) {
        return Response.ok().entity(((ArquivoBC) bc).listarFTS(texto)).build();
    }

    @GET
    @Path(value = "download/{id}")
    @Transactional
    @Produces(value = "application/force-download")
    public void download(@Suspended final AsyncResponse asyncResponse, @PathParam(value = "id") final Long id) {
        asyncResponse.resume(doDownload(id));
    }

    private Response doDownload(@PathParam("id") Long id) {
        return Response.ok().build();
    }

}
