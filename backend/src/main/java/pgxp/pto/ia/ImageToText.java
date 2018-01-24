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

import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.vision.v1.AnnotateImageRequest;
import com.google.cloud.vision.v1.AnnotateImageResponse;
import com.google.cloud.vision.v1.BatchAnnotateImagesResponse;
import com.google.cloud.vision.v1.EntityAnnotation;
import com.google.cloud.vision.v1.Feature;
import com.google.cloud.vision.v1.Feature.Type;
import com.google.cloud.vision.v1.Image;
import com.google.cloud.vision.v1.ImageAnnotatorClient;
import com.google.cloud.vision.v1.ImageAnnotatorSettings;
import com.google.protobuf.ByteString;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class ImageToText {

    public String syncRecognizeFile(byte[] data) throws Exception, IOException {

        StringBuilder resultado = new StringBuilder();
        InputStream credentialsStream = new FileInputStream("/opt/demoiselle/google.json");
        GoogleCredentials credentials = GoogleCredentials.fromStream(credentialsStream);
        FixedCredentialsProvider credentialsProvider = FixedCredentialsProvider.create(credentials);

        ImageAnnotatorSettings annotatorSettings
                = ImageAnnotatorSettings.newBuilder()
                        .setCredentialsProvider(credentialsProvider)
                        .build();

        try (ImageAnnotatorClient vision = ImageAnnotatorClient.create(annotatorSettings)) {

            // The path to the image file to annotate
            //String fileName = "./resources/wakeupcat.jpg";
            // Reads the image file into memory
            //Path path = Paths.get(fileName);
            //byte[] data = Files.readAllBytes(path);
            ByteString imgBytes = ByteString.copyFrom(data);

            // Builds the image annotation request
            List<AnnotateImageRequest> requests = new ArrayList<>();
            Image img = Image.newBuilder().setContent(imgBytes).build();
            Feature feat = Feature.newBuilder().setType(Type.TEXT_DETECTION).build();
            AnnotateImageRequest request = AnnotateImageRequest.newBuilder()
                    .addFeatures(feat)
                    .setImage(img)
                    .build();
            requests.add(request);

            // Performs label detection on the image file
            BatchAnnotateImagesResponse response = vision.batchAnnotateImages(requests);
            List<AnnotateImageResponse> responses = response.getResponsesList();

            for (AnnotateImageResponse res : responses) {
                resultado.append(res.getFullTextAnnotation().getText()).append(" \n ");
            }
        }

        return resultado.toString();
    }

}
