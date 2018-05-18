/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pgxp.pto.entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

/**
 *
 * @author gladson
 */
@Entity
@Cacheable
@DynamicInsert
@DynamicUpdate
@Table
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Mensagem.findAll", query = "SELECT m FROM Mensagem m")
    , @NamedQuery(name = "Mensagem.findByDatahora", query = "SELECT m FROM Mensagem m WHERE m.datahora = :datahora")
    , @NamedQuery(name = "Mensagem.findByTextmessage", query = "SELECT m FROM Mensagem m WHERE m.textmessage = :textmessage")
    , @NamedQuery(name = "Mensagem.findByExtra", query = "SELECT m FROM Mensagem m WHERE m.extra = :extra")
    , @NamedQuery(name = "Mensagem.findByTopic", query = "SELECT m FROM Mensagem m WHERE m.topic = :topic")
    , @NamedQuery(name = "Mensagem.findByAddresses", query = "SELECT m FROM Mensagem m WHERE m.addresses = :addresses")
    , @NamedQuery(name = "Mensagem.findByTipo", query = "SELECT m FROM Mensagem m WHERE m.tipo = :tipo")
    , @NamedQuery(name = "Mensagem.findByEnviada", query = "SELECT m FROM Mensagem m WHERE m.enviada = :enviada")
    , @NamedQuery(name = "Mensagem.findById", query = "SELECT m FROM Mensagem m WHERE m.id = :id")})
public class Mensagem implements Serializable {

    private static final long serialVersionUID = 1L;
    @Column(name = "datahora")
    @Temporal(TemporalType.TIMESTAMP)
    private Date datahora;
    @Size(max = 4096)
    @Column(name = "textmessage")
    private String textmessage;
    @Size(max = 4096)
    @Column(name = "extra")
    private String extra;
    @Size(max = 512)
    @Column(name = "topic")
    private String topic;
    @Size(max = 512)
    @Column(name = "addresses")
    private String addresses;
    @Size(max = 4)
    @Column(name = "tipo")
    private String tipo;
    @Column(name = "enviada")
    private Boolean enviada;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;

    /**
     *
     */
    public Mensagem() {
    }

    /**
     *
     * @param id
     */
    public Mensagem(Long id) {
        this.id = id;
    }

    /**
     *
     * @return
     */
    public Date getDatahora() {
        return datahora;
    }

    /**
     *
     * @param datahora
     */
    public void setDatahora(Date datahora) {
        this.datahora = datahora;
    }

    /**
     *
     * @return
     */
    public String getTextmessage() {
        return textmessage;
    }

    /**
     *
     * @param textmessage
     */
    public void setTextmessage(String textmessage) {
        this.textmessage = textmessage;
    }

    /**
     *
     * @return
     */
    public String getExtra() {
        return extra;
    }

    /**
     *
     * @param extra
     */
    public void setExtra(String extra) {
        this.extra = extra;
    }

    /**
     *
     * @return
     */
    public String getTopic() {
        return topic;
    }

    /**
     *
     * @param topic
     */
    public void setTopic(String topic) {
        this.topic = topic;
    }

    /**
     *
     * @return
     */
    public String getAddresses() {
        return addresses;
    }

    /**
     *
     * @param addresses
     */
    public void setAddresses(String addresses) {
        this.addresses = addresses;
    }

    /**
     *
     * @return
     */
    public String getTipo() {
        return tipo;
    }

    /**
     *
     * @param tipo
     */
    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    /**
     *
     * @return
     */
    public Boolean getEnviada() {
        return enviada;
    }

    /**
     *
     * @param enviada
     */
    public void setEnviada(Boolean enviada) {
        this.enviada = enviada;
    }

    /**
     *
     * @return
     */
    public Long getId() {
        return id;
    }

    /**
     *
     * @param id
     */
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Mensagem)) {
            return false;
        }
        Mensagem other = (Mensagem) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "pgxp.app.tenant.Mensagem[ id=" + id + " ]";
    }

}
