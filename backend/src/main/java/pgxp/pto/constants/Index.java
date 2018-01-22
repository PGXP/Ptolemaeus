/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pgxp.pto.constants;

/**
 *
 * @author 70744416353
 */

import com.google.common.collect.ImmutableSet;

import opennlp.tools.stemmer.Stemmer;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerModel;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.stream.Stream;

/**
 * An inverted index using Redis.
 *
 * <p>The {@code Index} indexes the files in which each keyword stem was found and supports queries
 * on the index.
 */
public class Index {
  private static final int TOKEN_DB = 0;
  private static final int DOCS_DB = 1;

  /**
   * Parses tokenizer data and creates a tokenizer.
   */
  public static TokenizerModel getEnglishTokenizerMeModel() throws IOException {
    try (InputStream modelIn = new FileInputStream("en-token.bin")) {
      return new TokenizerModel(modelIn);
    }
  }

 

  private final Tokenizer tokenizer;
  private final Stemmer stemmer;

  /**
   * Constructs a connection to the index.
   */
  public Index(Tokenizer tokenizer, Stemmer stemmer) {
    this.tokenizer = tokenizer;
    this.stemmer = stemmer;
  }

  /**
   * Prints {@code words} information from the index.
   */
  public void printLookup(Iterable<String> words) {
    ImmutableSet<String> hits = lookup(words);
    if (hits.size() == 0) {
      System.out.print("No hits found.\n\n");
    }
    
  }

  /**
   * Looks up the set of documents containing each word. Returns the intersection of these.
   */
  public ImmutableSet<String> lookup(Iterable<String> words) {
    HashSet<String> documents = null;
    
    
    if (documents == null) {
      return ImmutableSet.<String>of();
    }
    return ImmutableSet.<String>copyOf(documents);
  }


  /**
   * Extracts all tokens from a {@code document} as a stream.
   */
  public Stream<Word> extractTokens(Word document) {
    Stream.Builder<Word> output = Stream.builder();
    String[] words = tokenizer.tokenize(document.word());
    // Ensure we track empty documents throughout so that they are not reprocessed.
    if (words.length == 0) {
      output.add(Word.builder().path(document.path()).word("").build());
      return output.build();
    }
    for (int i = 0; i < words.length; i++) {
      output.add(Word.builder().path(document.path()).word(words[i]).build());
    }
    return output.build();
  }

  /**
   * Extracts the stem from a {@code word}.
   */
  public Word stem(Word word) {
    return Word.builder().path(word.path()).word(stemmer.stem(word.word()).toString()).build();
  }

  /**
   * Adds a {@code document} to the index.
   */
  public void addDocument(Word document) {
    
    extractTokens(document)
        .map(this::stem)
        .forEach(this::add);
  }

  /**
   * Adds a {@code word} to the index.
   */
  public void add(Word word) {
    
  }
}