package pgxp.pto.constants;

import java.io.Serializable;
import java.util.Objects;
import java.util.logging.Logger;

public class ResponseFTS implements Serializable {

    private static final long serialVersionUID = -1769589533175831560L;

    private String idOrigem;
    private String origem;
    private String nome;
    private float ocorrencias;
    private String texto;

    /**
     *
     * @return
     */
    public String getIdOrigem() {
        return idOrigem;
    }

    /**
     *
     * @param idOrigem
     */
    public void setIdOrigem(String idOrigem) {
        this.idOrigem = idOrigem;
    }

    /**
     *
     * @return
     */
    public String getOrigem() {
        return origem;
    }

    /**
     *
     * @param origem
     */
    public void setOrigem(String origem) {
        this.origem = origem;
    }

    /**
     *
     * @return
     */
    public float getOcorrencias() {
        return ocorrencias;
    }

    /**
     *
     * @param ocorrencias
     */
    public void setOcorrencias(float ocorrencias) {
        this.ocorrencias = ocorrencias;
    }

    /**
     *
     * @return
     */
    public String getTexto() {
        return texto;
    }

    /**
     *
     * @param texto
     */
    public void setTexto(String texto) {
        this.texto = texto;
    }

    /**
     *
     * @return
     */
    public String getNome() {
        return nome;
    }

    /**
     *
     * @param nome
     */
    public void setNome(String nome) {
        this.nome = nome;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 19 * hash + Objects.hashCode(this.idOrigem);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ResponseFTS other = (ResponseFTS) obj;
        if (!Objects.equals(this.idOrigem, other.idOrigem)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ResponseFTS{" + "idOrigem=" + idOrigem + ", origem=" + origem + ", nome=" + nome + ", ocorrencias=" + ocorrencias + ", texto=" + texto + '}';
    }
    
    
    private static final Logger LOG = Logger.getLogger(ResponseFTS.class.getName());

}
