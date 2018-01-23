package pgxp.pto.dao;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import pgxp.pto.entity.Arquivo;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import static java.util.logging.Logger.getLogger;
import java.util.stream.Stream;
import javax.imageio.ImageIO;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.core.MultivaluedMap;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.lucene.search.Sort;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.graphics.PDXObject;
import org.apache.pdfbox.text.PDFTextStripper;
import org.demoiselle.jee.core.lifecycle.annotation.Startup;
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
import pgxp.pto.ia.AudioToText;
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
    private NLPtools nlp;

    @PersistenceContext(unitName = "ptoPU")
    protected EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public void salvarAnexo(MultipartFormDataInput input) {

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

                byte[] bytes = IOUtils.toByteArray(inputStream);

                if (bytes.length > 0) {
                    // Cria o objeto do arquivo da conta
                    Path file = Paths.get("/opt/appfiles/" + "livros/");
                    Files.createDirectories(file);
                    file = Paths.get("/opt/appfiles/" + "livros/" + fileName);
                    Files.write(file, bytes);
                    ler(fileName);
                }

            } catch (IOException e) {
                throw new InternalServerErrorException(
                        "Ocorreu um erro no envio de um dos arquivos, tente novamente mais tarde.");
            }

        }

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

    private void ler(String namefile) {
        LOG.log(Level.INFO, "{0} *********** Inicio ************", namefile);
        try {

            if (verifica(namefile)) {
                PDDocument pd;

                File input = new File("/opt/appfiles/" + "livros/" + namefile);  // The PDF file from where you would like to extract
                pd = PDDocument.load(input);

                PDDocumentInformation info = pd.getDocumentInformation();

                Arquivo arquivo = new Arquivo();
                arquivo.setDescription(namefile);
                arquivo.setPasta("/opt/appfiles/" + "doc/");

                if (info != null) {
                    arquivo.setAuthor(info.getAuthor());
//                    arquivo.setCreationDate(info.getCreationDate().toString());
                    arquivo.setCreator(info.getCreator());
                    arquivo.setKeywords(info.getKeywords());
//                    arquivo.setModificationDate(info.getModificationDate().toString());
                    arquivo.setProducer(info.getProducer());
                    arquivo.setSubject(info.getSubject());
                    arquivo.setTitle(info.getTitle());
                    arquivo.setTrapped(info.getTrapped());
                }

                arquivo = persist(arquivo);

                for (int i = 1; i <= pd.getNumberOfPages(); i++) {

                    Pagina pagina = new Pagina();
                    pagina.setArquivo(arquivo);
                    pagina.setDescription("Página " + i);
                    PDFTextStripper stripper = new PDFTextStripper();
                    stripper.setStartPage(i);
                    stripper.setEndPage(i);
                    String texto = stripper.getText(pd);

                    PDResources pdResources = pd.getPage(i).getResources();
                    for (COSName c : pdResources.getXObjectNames()) {
                        PDXObject o = pdResources.getXObject(c);
                        if (o instanceof org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject) {
                            File file = new File("/opt/appfiles/img/" + namefile + "-" + i + ".png");
                            ImageIO.write(((org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject) o).getImage(), "png", file);
                            texto = texto + " " + itt.syncRecognizeFile("/opt/appfiles/img/" + namefile + "-" + i + ".png");
                            FileUtils.write(new File("/opt/appfiles/img/" + namefile + "-" + i + ".txt"), texto);
                        }
                    }

                    pagina.setTexto(texto);
                    Entidades ents = new Entidades(nlp.persons(texto));
                    ents = entidadesDAO.persist(ents);
                    pagina.setEntidades(ents);
                    paginaDAO.persist(pagina);

                }
                pd.close();
                LOG.log(Level.INFO, "{0} *********** PROCESSADO ************", arquivo.getDescription());
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception ex) {
            Logger.getLogger(ArquivoDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
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
                    .andField("texto").boostedTo(2)
                    .sentence(nome).createQuery();
        } else {
            luceneQuery = qb.keyword()
                    .onField("description")
                    .andField("texto").boostedTo(2)
                    .matching(nome).createQuery();
        }

        FullTextQuery fullTextQuery = fullTextEm.createFullTextQuery(luceneQuery, Pagina.class);
        fullTextQuery.setProjection(FullTextQuery.SCORE, FullTextQuery.THIS);

        fullTextQuery.setSort(Sort.RELEVANCE).getResultList().forEach((object) -> {
            try {
                StringBuilder sb = new StringBuilder();
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
