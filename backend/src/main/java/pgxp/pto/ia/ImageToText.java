/*
 * Copyright 2016 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package pgxp.pto.ia;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.vision.v1.Vision;
import com.google.api.services.vision.v1.VisionScopes;
import com.google.api.services.vision.v1.model.AnnotateImageRequest;
import com.google.api.services.vision.v1.model.AnnotateImageResponse;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesResponse;
import com.google.api.services.vision.v1.model.EntityAnnotation;
import com.google.api.services.vision.v1.model.Feature;
import com.google.api.services.vision.v1.model.Image;
import com.google.api.services.vision.v1.model.Status;
import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import java.io.Console;

import opennlp.tools.stemmer.Stemmer;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerModel;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.GeneralSecurityException;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import pgxp.pto.constants.ImageText;
import pgxp.pto.constants.Index;
import pgxp.pto.constants.Word;

public class ImageToText {

    private static final int MAX_RESULTS = 6;
    private static final int BATCH_SIZE = 10;

    /**
     * Be sure to specify the name of your application. If the application name
     * is {@code null} or blank, the application will log a warning. Suggested
     * format is "MyCompany-ProductName/1.0".
     */
    private static final String APPLICATION_NAME = "Google-VisionTextSample/1.0";

    /**
     * Connects to the Vision API using Application Default Credentials.
     */
    public Vision getVisionService() throws GeneralSecurityException  {
        try {
            GoogleCredential credential
                    = GoogleCredential.getApplicationDefault().createScoped(VisionScopes.all());
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
            return new Vision.Builder(GoogleNetHttpTransport.newTrustedTransport(), jsonFactory, credential)
                    .setApplicationName(APPLICATION_NAME)
                    .build();
        } catch (IOException ex) {
            Logger.getLogger(ImageToText.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private final Vision vision;
    private final Index index;

    /**
     * Constructs a {@code TextApp} using the {@link Vision} service.
     */
    public ImageToText(Vision vision, Index index) {
        this.vision = vision;
        this.index = index;
    }

    /**
     * Looks up words in the index that the user enters into the console.
     */
    public void lookupWords() {
        System.out.println("Entering word lookup mode.");
        System.out
                .println("To index a directory, add an input path argument when you run this command.");
        System.out.println();

        Console console = System.console();
        if (console == null) {
            System.err.println("No console.");
            System.exit(1);
        }

        while (true) {
            String words
                    = console.readLine("Enter word(s) (comma-separated, leave blank to exit): ").trim();
            if (words.equals("")) {
                break;
            }
            //index.printLookup(Splitter.on(',').split(words));
        }
    }

    /**
     * Indexes all the images in the {@code inputPath} directory for text.
     */
    public void indexDirectory(Path inputPath) throws IOException {
        List<Path> unprocessedImages
                = Files.walk(inputPath)
                        .filter(Files::isRegularFile)
                        .collect(Collectors.toList());
        Lists.<Path>partition(unprocessedImages, BATCH_SIZE)
                .stream()
                .map(this::detectText)
                .flatMap(l -> l.stream())
                .filter(this::successfullyDetectedText)
                .map(this::extractDescriptions)
                .forEach(index::addDocument);
    }

    /**
     * Gets up to {@code maxResults} text annotations for images stored at
     * {@code paths}.
     */
    public ImmutableList<ImageText> detectText(List<Path> paths) {
        ImmutableList.Builder<AnnotateImageRequest> requests = ImmutableList.builder();
        try {
            for (Path path : paths) {
                byte[] data;
                data = Files.readAllBytes(path);
                requests.add(
                        new AnnotateImageRequest()
                                .setImage(new Image().encodeContent(data))
                                .setFeatures(ImmutableList.of(
                                        new Feature()
                                                .setType("TEXT_DETECTION")
                                                .setMaxResults(MAX_RESULTS))));
            }

            Vision.Images.Annotate annotate
                    = vision.images()
                            .annotate(new BatchAnnotateImagesRequest().setRequests(requests.build()));
            // Due to a bug: requests to Vision API containing large images fail when GZipped.
            annotate.setDisableGZipContent(true);
            BatchAnnotateImagesResponse batchResponse = annotate.execute();
            assert batchResponse.getResponses().size() == paths.size();

            ImmutableList.Builder<ImageText> output = ImmutableList.builder();
            for (int i = 0; i < paths.size(); i++) {
                Path path = paths.get(i);
                AnnotateImageResponse response = batchResponse.getResponses().get(i);
                output.add(
                        ImageText.builder()
                                .path(path)
                                .textAnnotations(
                                        MoreObjects.firstNonNull(
                                                response.getTextAnnotations(),
                                                ImmutableList.<EntityAnnotation>of()))
                                .error(response.getError())
                                .build());
            }
            return output.build();
        } catch (IOException ex) {
            // Got an exception, which means the whole batch had an error.
            ImmutableList.Builder<ImageText> output = ImmutableList.builder();
            for (Path path : paths) {
                output.add(
                        ImageText.builder()
                                .path(path)
                                .textAnnotations(ImmutableList.<EntityAnnotation>of())
                                .error(new Status().setMessage(ex.getMessage()))
                                .build());
            }
            return output.build();
        }
    }

    /**
     * Checks that there was not an error processing an {@code image}.
     */
    public boolean successfullyDetectedText(ImageText image) {
        if (image.error() != null) {
            System.out.printf("Error reading %s:\n%s\n", image.path(), image.error().getMessage());
            return false;
        }
        return true;
    }

    /**
     * Extracts as a combinded string, all the descriptions from text
     * annotations on an {@code image}.
     */
    public Word extractDescriptions(ImageText image) {
        String document = "";
        for (EntityAnnotation text : image.textAnnotations()) {
            document += text.getDescription();
        }
        if (document.equals("")) {
            System.out.printf("%s had no discernible text.\n", image.path());
        }
        // Output a progress indicator.
        System.out.print('.');
        System.out.flush();
        return Word.builder().path(image.path()).word(document).build();
    }
}
