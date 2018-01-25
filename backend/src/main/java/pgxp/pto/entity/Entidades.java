/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pgxp.pto.entity;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Logger;
import static java.util.logging.Logger.getLogger;
import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Cacheable
@DynamicInsert
@DynamicUpdate
@XmlRootElement
@Table(name = "entidades")
public class Entidades implements Serializable {

    private static final Logger LOG = getLogger(Entidades.class.getName());

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @Column(unique = true)
    private UUID id;

    @Column(length = 2560)
    private String persons = "";
    @Column(length = 2560)
    private String organizations = "";
    @Column(length = 2560)
    private String groups = "";
    @Column(length = 2560)
    private String places = "";
    @Column(length = 2560)
    private String events = "";
    @Column(length = 2560)
    private String artprods = "";
    @Column(length = 2560)
    private String abstracts = "";
    @Column(length = 2560)
    private String things = "";
    @Column(length = 2560)
    private String times = "";
    @Column(length = 2560)
    private String numerics = "";

    @Column(length = 2560)
    private String unknowns = "";
    @Column(length = 2560)
    private String consumerGoods = "";
    @Column(length = 2560)
    private String other = "";

    public Entidades() {
    }

    public Entidades(Map<String, String> nlpResult) {
        carregar(nlpResult);
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
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

    private void carregar(Map<String, String> mapa) {
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
