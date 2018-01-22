package pgxp.pto.entity;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;
import javax.persistence.Basic;
import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import org.apache.lucene.analysis.br.BrazilianAnalyzer;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.search.annotations.Analyze;
import org.hibernate.search.annotations.Analyzer;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.Store;

@Entity
@Cacheable
@DynamicInsert
@DynamicUpdate
@Indexed
@XmlRootElement
@Analyzer(impl = BrazilianAnalyzer.class)
@Table(name = "pagina")
public class Pagina implements Serializable {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @Column(unique = true)
    private UUID id;

    @ManyToOne
    @JoinColumn
    private Arquivo arquivo;

    @ManyToOne
    @JoinColumn
    private Entidades entidades;

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 128)
    @Column(nullable = false, length = 128)
    @Field(index = Index.YES, analyze = Analyze.YES, store = Store.NO)
    private String description;

    @Column(length = 204800)
    @Field(index = Index.YES, analyze = Analyze.YES, store = Store.NO)
    private String texto;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Arquivo getArquivo() {
        return arquivo;
    }

    public void setArquivo(Arquivo arquivo) {
        this.arquivo = arquivo;
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    public Entidades getEntidades() {
        return entidades;
    }

    public void setEntidades(Entidades entidades) {
        this.entidades = entidades;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 17 * hash + Objects.hashCode(this.id);
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
        final Pagina other = (Pagina) obj;
        return Objects.equals(this.id, other.id);
    }

    @Override
    public String toString() {
        return "Pagina{" + "id=" + id + ", arquivo=" + arquivo + ", description=" + description + ", texto=" + texto + '}';
    }

}
