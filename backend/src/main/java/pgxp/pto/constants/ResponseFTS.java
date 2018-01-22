package pgxp.pto.constants;

import java.io.Serializable;
import java.util.Objects;
import java.util.logging.Logger;
import pgxp.pto.entity.Entidades;
import pgxp.pto.entity.Pagina;

public class ResponseFTS implements Serializable {

    private static final long serialVersionUID = -1769589533175831560L;

    private Pagina pagina;
    private float ocorrencias;

    public Pagina getPagina() {
        return pagina;
    }

    public void setPagina(Pagina pagina) {
        this.pagina = pagina;
    }

    public float getOcorrencias() {
        return ocorrencias;
    }

    public void setOcorrencias(float ocorrencias) {
        this.ocorrencias = ocorrencias;
    }

    private static final Logger LOG = Logger.getLogger(ResponseFTS.class.getName());

}
