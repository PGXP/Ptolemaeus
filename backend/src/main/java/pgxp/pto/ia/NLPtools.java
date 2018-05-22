/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pgxp.pto.ia;

import com.google.api.client.util.ArrayMap;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import static java.lang.Runtime.getRuntime;
import static java.lang.System.nanoTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import static java.util.concurrent.Executors.newFixedThreadPool;
import static java.util.concurrent.TimeUnit.MINUTES;
import java.util.logging.Level;
import static java.util.logging.Level.INFO;
import java.util.logging.Logger;
import static java.util.logging.Logger.getLogger;
import javax.inject.Inject;
import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.util.Span;
import pgxp.pto.AdminConfig;
import pgxp.pto.parallel.ProcessorNameFinder;

/**
 *
 * @author PauloGladson
 */
// http://www.linguateca.pt/Floresta/corpus.html
// baixar arquivo em http://opennlp.sourceforge.net/models-1.5/
public class NLPtools {

    private static final Logger LOG = getLogger(NLPtools.class.getName());

    @Inject
    private AdminConfig config;

    public Map<String, String> nameFinder(String texto) {

        long lStartTime = nanoTime();
        int MAX_THREADS = getRuntime().availableProcessors();
        ExecutorService executorGerador = newFixedThreadPool(MAX_THREADS);

        final Map<String, String> resultado = new ConcurrentHashMap<>();
        HashSet<String> sd = sentenceDetect(texto);
        String sentences[] = sd.toArray(new String[sd.size()]);
        File folder = new File(config.getPathia());
        File[] listOfFiles = folder.listFiles((File dir, String name) -> name.contains("-ner"));

        for (String sentence : sentences) {
            HashSet<String> tks = tokenizer(sentence);
            String tokens[] = tks.toArray(new String[tks.size()]);
            for (File listOfFile : listOfFiles) {
                if (listOfFile.isFile()) {
                    ProcessorNameFinder pnf = new ProcessorNameFinder();
                    pnf.setPath(config.getPathia() + File.separator + listOfFile.getName());
                    pnf.setTokens(tokens);
                    pnf.setResultado(resultado);
                    executorGerador.submit(pnf);
                }
            }
        }

        executorGerador.shutdown();

        try {
            executorGerador.awaitTermination(5, MINUTES);
        } catch (InterruptedException ie) {
            LOG.severe(ie.getLocalizedMessage());
        }

        long lEndTime = nanoTime();
        LOG.log(INFO, "Time: {0}s", (lEndTime - lStartTime) / 1_000_000);

        return resultado;
    }

    public HashSet<String> tokenizer(String texto) {
        HashSet<String> resultado = new HashSet<>();

        File folder = new File(config.getPathia());
        File[] listOfFiles = folder.listFiles((File dir, String name) -> name.contains("-token"));

        for (File listOfFile : listOfFiles) {
            if (listOfFile.isFile()) {

                try (InputStream modelToken = new FileInputStream(config.getPathia() + File.separator + listOfFile.getName())) {

                    TokenizerModel model = new TokenizerModel(modelToken);
                    TokenizerME tokenizer = new TokenizerME(model);
                    String token[] = tokenizer.tokenize(texto);
                    resultado.addAll(Arrays.asList(token));

                } catch (FileNotFoundException ex) {
                    Logger.getLogger(NLPtools.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(NLPtools.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

        }
        return resultado;
    }

    public HashSet<String> sentenceDetect(String text) {
        HashSet<String> resultado = new HashSet<>();

        File folder = new File(config.getPathia());
        File[] listOfFiles = folder.listFiles((File dir, String name) -> name.contains("-sent"));

        for (File listOfFile : listOfFiles) {
            if (listOfFile.isFile()) {

                try (InputStream modelSentence = new FileInputStream(config.getPathia() + File.separator + listOfFile.getName())) {

                    SentenceModel model = new SentenceModel(modelSentence);
                    SentenceDetectorME sdetector = new SentenceDetectorME(model);
                    String sentences[] = sdetector.sentDetect(text);
                    resultado.addAll(Arrays.asList(sentences));

                } catch (FileNotFoundException ex) {
                    Logger.getLogger(NLPtools.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(NLPtools.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return resultado;
    }

    public Map<String, String> POSTaggerMaxent(String texto) {
        Map<String, String> resultado = new ArrayMap<>();

        File folder = new File(config.getPathia());
        File[] listOfFiles = folder.listFiles((File dir, String name) -> name.contains("-pos-maxent"));

        for (File listOfFile : listOfFiles) {
            if (listOfFile.isFile()) {

                try (InputStream modelPOSmaxent = new FileInputStream(config.getPathia() + File.separator + listOfFile.getName())) {

                    String tokens[] = tokenizer(texto).toArray(new String[tokenizer(texto).size()]);
                    POSModel posModel = new POSModel(modelPOSmaxent);
                    POSTaggerME posTagger = new POSTaggerME(posModel);
                    String tags[] = posTagger.tag(tokens);

                    for (int i = 0; i < tokens.length; i++) {
                        resultado.put(tokens[i], tags[i]);
                    }
                } catch (IOException e) {
                    Logger.getLogger(NLPtools.class.getName()).log(Level.SEVERE, null, e);
                }
            }
        }
        return resultado;
    }

    public Map<String, String> POSTaggerPerceptron(String texto) {
        Map<String, String> resultado = new ArrayMap<>();

        File folder = new File(config.getPathia());
        File[] listOfFiles = folder.listFiles((File dir, String name) -> name.contains("-pos-perceptron"));

        for (File listOfFile : listOfFiles) {
            if (listOfFile.isFile()) {

                try (InputStream modelPOSperceptron = new FileInputStream(config.getPathia() + File.separator + listOfFile.getName())) {
                    String tokens[] = tokenizer(texto).toArray(new String[tokenizer(texto).size()]);
                    POSModel posModel = new POSModel(modelPOSperceptron);
                    POSTaggerME posTagger = new POSTaggerME(posModel);
                    String tags[] = posTagger.tag(tokens);

                    for (int i = 0; i < tokens.length; i++) {
                        resultado.put(tokens[i], tags[i]);
                    }

                } catch (IOException e) {
                    Logger.getLogger(NLPtools.class.getName()).log(Level.SEVERE, null, e);
                }
            }
        }
        return resultado;
    }

}
