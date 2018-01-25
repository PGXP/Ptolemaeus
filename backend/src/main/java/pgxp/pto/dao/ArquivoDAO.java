package pgxp.pto.dao;

import com.google.api.client.util.ArrayMap;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import pgxp.pto.entity.Arquivo;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import static java.util.logging.Logger.getLogger;
import javax.imageio.ImageIO;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.core.MultivaluedMap;
import org.apache.lucene.search.Sort;

import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.graphics.PDXObject;
import org.apache.pdfbox.text.PDFTextStripper;
import org.demoiselle.jee.crud.AbstractDAO;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.FullTextQuery;
import org.hibernate.search.jpa.Search;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;
import pgxp.pto.constants.ResponseFTS;
import pgxp.pto.entity.Entidades;
import pgxp.pto.entity.Pagina;
import pgxp.pto.ia.AnalyzeText;
import pgxp.pto.ia.ImageToText;
import pgxp.pto.ia.NLPtools;

public class ArquivoDAO extends AbstractDAO< Arquivo, UUID> {

    private static final Logger LOG = getLogger(ArquivoDAO.class.getName());

    @Inject
    private PaginaDAO paginaDAO;

    @Inject
    private EntidadesDAO entidadesDAO;

    @Inject
    private ImageToText itt;

    @Inject
    private AnalyzeText at;

    @Inject
    private NLPtools nlp;

    @PersistenceContext(unitName = "ptoPU")
    protected EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public Arquivo salvarAnexo(MultipartFormDataInput input) {
        Arquivo arquivo = new Arquivo();
        Map<String, List<InputPart>> uploadForm = input.getFormDataMap();
        List<InputPart> inputParts = uploadForm.get("file");

        // Verifica se existem arquivos na requisição
        if (inputParts == null || inputParts.isEmpty()) {
            inputParts = uploadForm.get("file[]");
        }

        for (InputPart inputPart : inputParts) {

            try {
                MultivaluedMap<String, String> header = inputPart.getHeaders();
                String fileName = getFileName(header);

                InputStream inputStream = inputPart.getBody(InputStream.class, null);

                arquivo = ler(inputStream, fileName);

            } catch (IOException e) {
                throw new InternalServerErrorException(
                        "Ocorreu um erro no envio de um dos arquivos, tente novamente mais tarde.");
            }

        }
        return arquivo;
    }

    private String getFileName(MultivaluedMap<String, String> header) {
        String[] contentDisposition = header.getFirst("Content-Disposition").split(";");

        for (String filename : contentDisposition) {
            if ((filename.trim().startsWith("filename"))) {
                String[] name = filename.split("=");
                return name[1].trim().replaceAll("\"", "");
            }
        }
        return "unknown";
    }

    private Arquivo ler(InputStream inputStream, String namefile) {
        Arquivo arquivo = new Arquivo();
        try {
            if (verifica(namefile)) {
                LOG.log(Level.INFO, "{0} *********** Inicio ************", namefile);
                PDDocument pd;
                pd = PDDocument.load(inputStream);
                arquivo.setDescription(namefile);
                arquivo = persist(arquivo);

                for (int i = 0; i < pd.getNumberOfPages(); i++) {

                    Pagina pagina = new Pagina();
                    pagina.setArquivo(arquivo);
                    pagina.setDescription("Página " + (i + 1));
                    PDFTextStripper stripper = new PDFTextStripper();
                    stripper.setStartPage(i);
                    stripper.setEndPage(i);
                    String texto = stripper.getText(pd);

                    PDResources pdResources = pd.getPage(i).getResources();
                    for (COSName c : pdResources.getXObjectNames()) {
                        PDXObject o = pdResources.getXObject(c);
                        if (o instanceof org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject) {
                            byte[] imageInByte;
                            try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                                ImageIO.write(((org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject) o).getImage(), "jpg", baos);
                                baos.flush();
                                imageInByte = baos.toByteArray();
                            }
                            if (imageInByte.length > 0) {
                                texto = texto + " " + itt.syncRecognizeFile(imageInByte);
                            }

                        }
                    }

                    pagina.setTexto(texto);
                    Map<String, String> nlpResult = new ArrayMap<>();
                    nlpResult.putAll(nlp.persons(texto));
                    //nlpResult.putAll(at.analyzeEntitiesText(texto));
                    Entidades ents = new Entidades(nlpResult);
                    ents = entidadesDAO.persist(ents);
                    pagina.setEntidades(ents);
                    paginaDAO.persist(pagina);

                }
                pd.close();
                LOG.log(Level.INFO, "{0} *********** PROCESSADO ************", arquivo.getDescription());
            }

        } catch (IOException ex) {
            Logger.getLogger(ArquivoDAO.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(ArquivoDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return arquivo;
    }

    /**
     *
     * @param nome
     * @return
     */
    public List<ResponseFTS> listarFTS(String nome) {

        List<ResponseFTS> lista = new ArrayList<>();
        FullTextEntityManager fullTextEm = Search.getFullTextEntityManager(getEntityManager());
        QueryBuilder qb = fullTextEm.getSearchFactory()
                .buildQueryBuilder()
                .forEntity(Pagina.class)
                .get();

        org.apache.lucene.search.Query luceneQuery;

        if (nome.split(" ").length > 1) {
            luceneQuery = qb.phrase()
                    .onField("description")
                    .andField("texto")
                    .sentence(nome).createQuery();
        } else {
            luceneQuery = qb.keyword()
                    .onField("description")
                    .andField("texto")
                    .matching(nome).createQuery();
        }

        FullTextQuery fullTextQuery = fullTextEm.createFullTextQuery(luceneQuery, Pagina.class);
        fullTextQuery.setProjection(FullTextQuery.SCORE, FullTextQuery.THIS);

        fullTextQuery.setSort(Sort.RELEVANCE).getResultList().forEach((object) -> {
            try {

                Float score = (Float) ((Object[]) object)[0];
                Pagina pagina = (Pagina) ((Object[]) object)[1];

                if (pagina != null) {
                    ResponseFTS fts = new ResponseFTS();
                    fts.setPagina(pagina);
                    fts.setOcorrencias(score);

                    lista.add(fts);
                }
            } catch (Exception ex) {
                Logger.getLogger(ArquivoDAO.class.getName()).log(Level.SEVERE, null, ex);

            }
        });

        return lista;

    }

    public void reindex() {
        FullTextEntityManager fullTextEntityManager = org.hibernate.search.jpa.Search
                .getFullTextEntityManager(getEntityManager());
        try {
            fullTextEntityManager.createIndexer().optimizeOnFinish(Boolean.TRUE).startAndWait();
        } catch (InterruptedException e) {
            LOG.severe(e.getMessage());
        }
    }

    private boolean verifica(String description) {
        List<Arquivo> lista = getEntityManager().createQuery("Select a from Arquivo a where a.description = :desc", Arquivo.class).setParameter("desc", description).getResultList();
        return lista.isEmpty();
    }

}
