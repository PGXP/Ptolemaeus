package pgxp.pto.service;

import pgxp.pto.entity.Arquivo;
import java.util.UUID;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import javax.ejb.Asynchronous;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
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

    @GET
    @Transactional
    @Path("fts/{term}")
    @Consumes(APPLICATION_JSON)
    @Produces(APPLICATION_JSON)
    public Response findFts(@PathParam("term") String texto) {
        return Response.ok().entity(((ArquivoBC) bc).listarFTS(texto)).build();
    }

    /**
     *
     * @param input
     * @return
     */
    @POST
    @Path("upload")
    @Transactional
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response salvarAnexo(MultipartFormDataInput input) {
        ((ArquivoBC) bc).salvarAnexo(input);
        return Response.ok().build();
    }

    /**
     *
     * @param id
     * @return
     */
    @GET
    @Path("download/{id}")
    @Transactional
    @Produces("application/force-download")
    public Response download(@PathParam("id") Long id) {
        return Response.ok().build();
//        final ByteArrayInputStream in = new ByteArrayInputStream(anexo.getArquivo());
//        return Response.ok(in, MediaType.APPLICATION_OCTET_STREAM)
//            .header("content-disposition", "attachment; filename = '" + anexo.getNomeArquivo() + "'").build();
    }

    /**
     *
     * @param id
     * @return
     */
    @GET
    @Path("audio")
    @Transactional
    public Response audio() {
        return Response.ok().entity(((ArquivoBC) bc).audioToText("/opt/audio/flac")).build();
//        final ByteArrayInputStream in = new ByteArrayInputStream(anexo.getArquivo());
//        return Response.ok(in, MediaType.APPLICATION_OCTET_STREAM)
//            .header("content-disposition", "attachment; filename = '" + anexo.getNomeArquivo() + "'").build();
    }
}
