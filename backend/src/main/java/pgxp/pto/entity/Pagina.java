package pgxp.pto.entity;

import java.io.Serializable;
import java.util.Map;
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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
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
@DynamicInsert
@DynamicUpdate
@Indexed
@Cacheable
@XmlRootElement
@Analyzer
@Table(name = "pagina")
@NamedQueries({
    @NamedQuery(name = "Pagina.findAll", query = "SELECT m FROM Pagina m WHERE m.validado is false")})
public class Pagina implements Serializable {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @Column(unique = true)
    private UUID id;

    @ManyToOne
    @JoinColumn
    private Arquivo arquivo;

    @Column(length = 2560)
    @Field(index = Index.YES, analyze = Analyze.YES, store = Store.NO)
    private String persons = "";
    @Column(length = 2560)
    @Field(index = Index.YES, analyze = Analyze.YES, store = Store.NO)
    private String organizations = "";
    @Column(length = 2560)
    @Field(index = Index.YES, analyze = Analyze.YES, store = Store.NO)
    private String groups = "";
    @Column(length = 2560)
    @Field(index = Index.YES, analyze = Analyze.YES, store = Store.NO)
    private String places = "";
    @Column(length = 2560)
    @Field(index = Index.YES, analyze = Analyze.YES, store = Store.NO)
    private String events = "";
    @Column(length = 2560)
    @Field(index = Index.YES, analyze = Analyze.YES, store = Store.NO)
    private String artprods = "";
    @Column(length = 2560)
    @Field(index = Index.YES, analyze = Analyze.YES, store = Store.NO)
    private String abstracts = "";
    @Column(length = 2560)
    @Field(index = Index.YES, analyze = Analyze.YES, store = Store.NO)
    private String things = "";
    @Column(length = 2560)
    @Field(index = Index.YES, analyze = Analyze.YES, store = Store.NO)
    private String times = "";
    @Column(length = 2560)
    @Field(index = Index.YES, analyze = Analyze.YES, store = Store.NO)
    private String numerics = "";
    @Column(length = 2560)
    @Field(index = Index.YES, analyze = Analyze.YES, store = Store.NO)
    private String unknowns = "";
    @Column(length = 2560)
    @Field(index = Index.YES, analyze = Analyze.YES, store = Store.NO)
    private String consumerGoods = "";
    @Column(length = 2560)
    @Field(index = Index.YES, analyze = Analyze.YES, store = Store.NO)
    private String other = "";

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 128)
    @Column(nullable = false, length = 128)
    @Field(index = Index.YES, analyze = Analyze.YES, store = Store.NO)
    private String description;

    @Column(length = 204800)
    @Field(index = Index.YES, analyze = Analyze.YES, store = Store.NO)
    private String texto;

    private Boolean validado;

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

    public String getPersons() {
        return persons;
    }

    public void setPersons(String persons) {
        this.persons = persons;
    }

    public String getOrganizations() {
        return organizations;
    }

    public void setOrganizations(String organizations) {
        this.organizations = organizations;
    }

    public String getGroups() {
        return groups;
    }

    public void setGroups(String groups) {
        this.groups = groups;
    }

    public String getPlaces() {
        return places;
    }

    public void setPlaces(String places) {
        this.places = places;
    }

    public String getEvents() {
        return events;
    }

    public void setEvents(String events) {
        this.events = events;
    }

    public String getArtprods() {
        return artprods;
    }

    public void setArtprods(String artprods) {
        this.artprods = artprods;
    }

    public String getAbstracts() {
        return abstracts;
    }

    public void setAbstracts(String abstracts) {
        this.abstracts = abstracts;
    }

    public String getThings() {
        return things;
    }

    public void setThings(String things) {
        this.things = things;
    }

    public String getTimes() {
        return times;
    }

    public void setTimes(String times) {
        this.times = times;
    }

    public String getNumerics() {
        return numerics;
    }

    public void setNumerics(String numerics) {
        this.numerics = numerics;
    }

    public String getUnknowns() {
        return unknowns;
    }

    public void setUnknowns(String unknowns) {
        this.unknowns = unknowns;
    }

    public String getConsumerGoods() {
        return consumerGoods;
    }

    public void setConsumerGoods(String consumerGoods) {
        this.consumerGoods = consumerGoods;
    }

    public String getOther() {
        return other;
    }

    public void setOther(String other) {
        this.other = other;
    }

    public Boolean getValidado() {
        return validado;
    }

    public void setValidado(Boolean validado) {
        this.validado = validado;
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

    public void carregar(Map<String, String> mapa) {
        mapa.entrySet().forEach((entry) -> {
            String key = entry.getKey();
            String value = entry.getValue();
            if (value.toLowerCase().contains("person")) {
                persons = key + ";" + persons;
            }
            if (value.toLowerCase().contains("organization")) {
                organizations = key + ";" + organizations;
            }
            if (value.toLowerCase().contains("group")) {
                groups = key + ";" + groups;
            }
            if (value.toLowerCase().contains("event")) {
                events = key + ";" + events;
            }
            if (value.toLowerCase().contains("place") || value.toLowerCase().contains("location")) {
                places = key + ";" + places;
            }
            if (value.toLowerCase().contains("artprod") || value.toLowerCase().contains("work_of_art")) {
                artprods = key + ";" + artprods;
            }
            if (value.toLowerCase().contains("abstract")) {
                abstracts = key + ";" + abstracts;
            }
            if (value.toLowerCase().contains("thing") || value.toLowerCase().contains("other")) {
                things = key + ";" + things;
            }
            if (value.toLowerCase().contains("time")) {
                times = key + ";" + times;
            }
            if (value.toLowerCase().contains("numeric")) {
                numerics = key + ";" + numerics;
            }
            if (value.toLowerCase().contains("consumer_good")) {
                consumerGoods = key + ";" + consumerGoods;
            }
            if (value.toLowerCase().contains("unknown")) {
                unknowns = key + ";" + unknowns;
            }
        });

    }
}
