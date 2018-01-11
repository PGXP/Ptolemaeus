package pgxp.pto.dao;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
import org.apache.commons.io.IOUtils;
import org.apache.lucene.search.Sort;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageTree;
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
import pgxp.pto.entity.Pagina;

public class ArquivoDAO extends AbstractDAO< Arquivo, UUID> {

    private static final Logger LOG = getLogger(ArquivoDAO.class.getName());

    @Inject
    private PaginaDAO paginaDAO;

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
                    Path file = Paths.get("/opt/appfiles/" + "ata/");
                    Files.createDirectories(file);
                    file = Paths.get("/opt/appfiles/" + "ata/" + fileName);
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

        try {

            if (verifica(namefile)) {
                PDDocument pd;

                File input = new File("/opt/appfiles/" + "ata/" + namefile);  // The PDF file from where you would like to extract
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
                    pagina.setTexto(stripper.getText(pd));
                    if (pagina.getTexto().isEmpty()) {
                        PDResources pdResources = pd.getPage(i).getResources();
                        for (COSName c : pdResources.getXObjectNames()) {
                            PDXObject o = pdResources.getXObject(c);
                            if (o instanceof org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject) {
                                File file = new File("/opt/appfiles/img/" + arquivo + "-" + i + ".png");
                                ImageIO.write(((org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject) o).getImage(), "png", file);
                            }
                        }
                    }
                    paginaDAO.persist(pagina);
                }
                pd.close();
                LOG.info(arquivo.getDescription() + " *********** PROCESSADO ************");
            }

        } catch (Exception e) {
            e.printStackTrace();
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
                    .andField("arquivo.description")
                    .sentence(nome).createQuery();
        } else {
            luceneQuery = qb.keyword()
                    .onField("description")
                    .andField("texto").boostedTo(2)
                    .andField("arquivo.description")
                    .matching(nome).createQuery();
        }

        FullTextQuery fullTextQuery = fullTextEm.createFullTextQuery(luceneQuery, Pagina.class);
        fullTextQuery.setProjection(FullTextQuery.SCORE, FullTextQuery.THIS);

        fullTextQuery.setSort(Sort.RELEVANCE).getResultList().forEach((object) -> {
            try {
                StringBuilder sb = new StringBuilder();
                Float score = (Float) ((Object[]) object)[0];
                Pagina pagina = (Pagina) ((Object[]) object)[1];
                ResponseFTS fts = new ResponseFTS();
                fts.setIdOrigem(pagina.getArquivo().getId().toString());
                fts.setOrigem(pagina.getArquivo().getDescription());
                fts.setNome(pagina.getDescription());
                fts.setOcorrencias(score);

                fts.setTexto(pagina.getTexto());

                lista.add(fts);
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

//    private void addContent(Document document, InputStream is, String documentLocation) throws IOException
//	    {
//	        try (PDDocument pdfDocument = PDDocument.load(is))
//	        {
//	            // create a writer where to append the text content.
//	            StringWriter writer = new StringWriter();
//	            if (stripper == null)
//	            {
//	                stripper = new PDFTextStripper();
//	            }
//	            stripper.writeText(pdfDocument, writer);
//	
//	            // Note: the buffer to string operation is costless;
//	            // the char array value of the writer buffer and the content string
//	            // is shared as long as the buffer content is not modified, which will
//	            // not occur here.
//	            String contents = writer.getBuffer().toString();
//	
//	            StringReader reader = new StringReader(contents);
//	
//	            // Add the tag-stripped contents as a Reader-valued Text field so it will
//	            // get tokenized and indexed.
//	            addTextField(document, "contents", reader);
//	
//	            PDDocumentInformation info = pdfDocument.getDocumentInformation();
//	            if (info != null)
//	            {
//	                addTextField(document, "Author", info.getAuthor());
//	                addTextField(document, "CreationDate", info.getCreationDate());
//	                addTextField(document, "Creator", info.getCreator());
//	                addTextField(document, "Keywords", info.getKeywords());
//	                addTextField(document, "ModificationDate", info.getModificationDate());
//	                addTextField(document, "Producer", info.getProducer());
//	                addTextField(document, "Subject", info.getSubject());
//	                addTextField(document, "Title", info.getTitle());
//	                addTextField(document, "Trapped", info.getTrapped());
//	            }
//	            int summarySize = Math.min(contents.length(), 500);
//	            String summary = contents.substring(0, summarySize);
//	            // Add the summary as an UnIndexed field, so that it is stored and returned
//	            // with hit documents for display.
//	            addUnindexedField(document, "summary", summary);
//	        }
//	        catch (InvalidPasswordException e)
//	        {
//	            // they didn't suppply a password and the default of "" was wrong.
//	            throw new IOException("Error: The document(" + documentLocation + ") is encrypted and will not be indexed.", e);
//	        }
//	    }

