package pgxp.pto.bc;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.demoiselle.jee.crud.AbstractBusiness;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;
import pgxp.pto.constants.ResponseFTS;
import pgxp.pto.dao.ArquivoDAO;
import pgxp.pto.entity.Arquivo;

public class ArquivoBC extends AbstractBusiness< Arquivo, UUID> {

    public Arquivo salvarAnexo(MultipartFormDataInput input) {
        return ((ArquivoDAO) dao).salvarAnexo(input);
    }

    public List<ResponseFTS> listarFTS(String nome) {
        return ((ArquivoDAO) dao).listarFTS(nome);
    }

    public List<String> scan() {
        return ((ArquivoDAO) dao).scan();
    }

}
