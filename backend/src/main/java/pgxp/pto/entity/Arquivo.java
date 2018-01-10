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
@Table(name = "arquivo")
public class Arquivo implements Serializable {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @Column(unique = true)
    private UUID id;

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 128)
    @Column(nullable = false, length = 128, unique = true)
    @Field(index = Index.YES, analyze = Analyze.YES, store = Store.NO)
    private String description;

    @Column(nullable = false, length = 128)
    private String pasta;

    @Column(length = 128)
    @Field(index = Index.YES, analyze = Analyze.YES, store = Store.NO)
    private String author;
    @Column(length = 128)
    @Field(index = Index.YES, analyze = Analyze.YES, store = Store.NO)
    private String creationDate;
    @Column(length = 128)
    @Field(index = Index.YES, analyze = Analyze.YES, store = Store.NO)
    private String creator;
    @Column(length = 128)
    @Field(index = Index.YES, analyze = Analyze.YES, store = Store.NO)
    private String keywords;
    @Column(length = 128)
    @Field(index = Index.YES, analyze = Analyze.YES, store = Store.NO)
    private String modificationDate;
    @Column(length = 128)
    @Field(index = Index.YES, analyze = Analyze.YES, store = Store.NO)
    private String producer;
    @Column(length = 128)
    @Field(index = Index.YES, analyze = Analyze.YES, store = Store.NO)
    private String subject;
    @Column(length = 128)
    @Field(index = Index.YES, analyze = Analyze.YES, store = Store.NO)
    private String title;
    @Column(length = 128)
    @Field(index = Index.YES, analyze = Analyze.YES, store = Store.NO)
    private String trapped;

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

    public String getPasta() {
        return pasta;
    }

    public void setPasta(String pasta) {
        this.pasta = pasta;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public String getModificationDate() {
        return modificationDate;
    }

    public void setModificationDate(String modificationDate) {
        this.modificationDate = modificationDate;
    }

    public String getProducer() {
        return producer;
    }

    public void setProducer(String producer) {
        this.producer = producer;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTrapped() {
        return trapped;
    }

    public void setTrapped(String trapped) {
        this.trapped = trapped;
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
        final Arquivo other = (Arquivo) obj;
        return Objects.equals(this.id, other.id);
    }

    @Override
    public String toString() {
        return "Arquivo{" + "id=" + id + ", description=" + description + ", pasta=" + pasta + ", author=" + author + ", creationDate=" + creationDate + ", creator=" + creator + ", keywords=" + keywords + ", modificationDate=" + modificationDate + ", producer=" + producer + ", subject=" + subject + ", title=" + title + ", trapped=" + trapped + '}';
    }

}
