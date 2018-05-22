/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pgxp.pto.parallel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import static java.util.logging.Logger.getLogger;
import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.util.Span;
import pgxp.pto.ia.NLPtools;

/**
 *
 * @author 70744416353
 */
public class ProcessorNameFinder implements Runnable {

    private static final Logger LOG = getLogger(ProcessorNameFinder.class.getName());

    private String path;
    private String tokens[];
    private Map<String, String> resultado;

    @Override
    public void run() {
        try (InputStream modelPerson = new FileInputStream(path)) {

            TokenNameFinderModel model = new TokenNameFinderModel(modelPerson);
            NameFinderME finder = new NameFinderME(model);
            Span[] nameSpans = finder.find(tokens);

            for (Span nameSpan : nameSpans) {
                String nome = "";
                for (int index = nameSpan.getStart(); index < nameSpan.getEnd(); index++) {
                    nome = tokens[index] + " " + nome;
                }
                resultado.putIfAbsent(nome, nameSpan.toString());
            }

        } catch (FileNotFoundException ex) {
            LOG.log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, null, ex);
        }
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String[] getTokens() {
        return tokens;
    }

    public void setTokens(String[] tokens) {
        this.tokens = tokens;
    }

    public Map<String, String> getResultado() {
        return resultado;
    }

    public void setResultado(Map<String, String> resultado) {
        this.resultado = resultado;
    }

}
