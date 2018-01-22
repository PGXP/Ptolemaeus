package pgxp.pto.ia;

import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.api.gax.longrunning.OperationFuture;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.speech.v1.LongRunningRecognizeMetadata;
import com.google.cloud.speech.v1.LongRunningRecognizeResponse;
import com.google.cloud.speech.v1.RecognitionAudio;
import com.google.cloud.speech.v1.RecognitionConfig;
import com.google.cloud.speech.v1.RecognitionConfig.AudioEncoding;
import com.google.cloud.speech.v1.RecognizeResponse;
import com.google.cloud.speech.v1.SpeechClient;
import com.google.cloud.speech.v1.SpeechRecognitionAlternative;
import com.google.cloud.speech.v1.SpeechRecognitionResult;
import com.google.cloud.speech.v1.SpeechSettings;
import com.google.protobuf.ByteString;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AudioToText {

    /**
     * Performs speech recognition on raw PCM audio and prints the
     * transcription.
     *
     * @param fileName the path to a PCM audio file to transcribe.
     */
    public Map<Float, String> syncRecognizeFile(String fileName) throws Exception, IOException {

        Map<Float, String> resultado = new HashMap<>();
        InputStream credentialsStream = new FileInputStream("/opt/demoiselle/google.json");
        GoogleCredentials credentials = GoogleCredentials.fromStream(credentialsStream);
        FixedCredentialsProvider credentialsProvider = FixedCredentialsProvider.create(credentials);

        SpeechSettings speechSettings
                = SpeechSettings.newBuilder()
                        .setCredentialsProvider(credentialsProvider)
                        .build();

        SpeechClient speech = SpeechClient.create(speechSettings);

        Path path = Paths.get(fileName);
        byte[] data = Files.readAllBytes(path);
        ByteString audioBytes = ByteString.copyFrom(data);

        // Configure request with local raw PCM audio
        RecognitionConfig config = RecognitionConfig.newBuilder()
                .setEncoding(AudioEncoding.FLAC)
                .setLanguageCode("pt-BR")
                .setSampleRateHertz(16000)
                .build();
        RecognitionAudio audio = RecognitionAudio.newBuilder()
                .setContent(audioBytes)
                .build();

        RecognizeResponse response = speech.recognize(config, audio);
        List<SpeechRecognitionResult> results = response.getResultsList();

        for (SpeechRecognitionResult result : results) {
            SpeechRecognitionAlternative alternative = result.getAlternativesList().get(0);
            resultado.put(alternative.getConfidence(), alternative.getTranscript());
        }

        speech.close();

        return resultado;
    }

//    public Map<Float, String> asyncRecognizeFile(String fileName) throws Exception, IOException {
//        // Instantiates a client with GOOGLE_APPLICATION_CREDENTIALS
//        Map<Float, String> resultado = new HashMap<>();
//        InputStream credentialsStream = new FileInputStream("/opt/demoiselle/google.json");
//        GoogleCredentials credentials = GoogleCredentials.fromStream(credentialsStream);
//        FixedCredentialsProvider credentialsProvider = FixedCredentialsProvider.create(credentials);
//
//        SpeechSettings speechSettings
//                = SpeechSettings.newBuilder()
//                        .setCredentialsProvider(credentialsProvider)
//                        .build();
//
//        SpeechClient speech = SpeechClient.create(speechSettings);
//
//        Path path = Paths.get(fileName);
//        byte[] data = Files.readAllBytes(path);
//        ByteString audioBytes = ByteString.copyFrom(data);
//
//        // Configure request with local raw PCM audio
//        RecognitionConfig config = RecognitionConfig.newBuilder()
//                .setEncoding(AudioEncoding.FLAC)
//                .setLanguageCode("pt-BR")
//                .setSampleRateHertz(16000)
//                .build();
//        RecognitionAudio audio = RecognitionAudio.newBuilder()
//                .setContent(audioBytes)
//                .build();
//
//        // Use non-blocking call for getting file transcription
//        OperationFuture<LongRunningRecognizeResponse, LongRunningRecognizeMetadata> response
//                = speech.longRunningRecognizeAsync(config, audio);
//
//        while (!response.isDone()) {
//            System.out.println("Waiting for response...");
//            Thread.sleep(10000);
//        }
//
//        List<SpeechRecognitionResult> results = response.get().getResultsList();
//
//        for (SpeechRecognitionResult result : results) {
//            SpeechRecognitionAlternative alternative = result.getAlternativesList().get(0);
//            resultado.put(alternative.getConfidence(), alternative.getTranscript());
//        }
//
//        speech.close();
//
//        return resultado;
//    }

//    /**
//     * Performs sync recognize and prints word time offsets.
//     *
//     * @param fileName the path to a PCM audio file to transcribe get offsets
//     * on.
//     */
//    public static void syncRecognizeWords(String fileName) throws Exception, IOException {
//        SpeechClient speech = SpeechClient.create();
//
//        Path path = Paths.get(fileName);
//        byte[] data = Files.readAllBytes(path);
//        ByteString audioBytes = ByteString.copyFrom(data);
//
//        // Configure request with local raw PCM audio
//        RecognitionConfig config = RecognitionConfig.newBuilder()
//                .setEncoding(AudioEncoding.LINEAR16)
//                .setLanguageCode("en-US")
//                .setSampleRateHertz(16000)
//                .setEnableWordTimeOffsets(true)
//                .build();
//        RecognitionAudio audio = RecognitionAudio.newBuilder()
//                .setContent(audioBytes)
//                .build();
//
//        // Use blocking call to get audio transcript
//        RecognizeResponse response = speech.recognize(config, audio);
//        List<SpeechRecognitionResult> results = response.getResultsList();
//
//        for (SpeechRecognitionResult result : results) {
//            // There can be several alternative transcripts for a given chunk of speech. Just use the
//            // first (most likely) one here.
//            SpeechRecognitionAlternative alternative = result.getAlternativesList().get(0);
//            System.out.printf("Transcription: %s%n", alternative.getTranscript());
//            for (WordInfo wordInfo : alternative.getWordsList()) {
//                System.out.println(wordInfo.getWord());
//                System.out.printf("\t%s.%s sec - %s.%s sec\n",
//                        wordInfo.getStartTime().getSeconds(),
//                        wordInfo.getStartTime().getNanos() / 100000000,
//                        wordInfo.getEndTime().getSeconds(),
//                        wordInfo.getEndTime().getNanos() / 100000000);
//            }
//        }
//        speech.close();
//    }
//
//    /**
//     * Performs speech recognition on remote FLAC file and prints the
//     * transcription.
//     *
//     * @param gcsUri the path to the remote FLAC audio file to transcribe.
//     */
//    public static void syncRecognizeGcs(String gcsUri) throws Exception, IOException {
//        // Instantiates a client with GOOGLE_APPLICATION_CREDENTIALS
//        SpeechClient speech = SpeechClient.create();
//
//        // Builds the request for remote FLAC file
//        RecognitionConfig config = RecognitionConfig.newBuilder()
//                .setEncoding(AudioEncoding.FLAC)
//                .setLanguageCode("en-US")
//                .setSampleRateHertz(16000)
//                .build();
//        RecognitionAudio audio = RecognitionAudio.newBuilder()
//                .setUri(gcsUri)
//                .build();
//
//        // Use blocking call for getting audio transcript
//        RecognizeResponse response = speech.recognize(config, audio);
//        List<SpeechRecognitionResult> results = response.getResultsList();
//
//        for (SpeechRecognitionResult result : results) {
//            // There can be several alternative transcripts for a given chunk of speech. Just use the
//            // first (most likely) one here.
//            SpeechRecognitionAlternative alternative = result.getAlternativesList().get(0);
//            System.out.printf("Transcription: %s%n", alternative.getTranscript());
//        }
//        speech.close();
//    }
//
//    /*
//  /**
//   * Performs non-blocking speech recognition on raw PCM audio and prints
//   * the transcription. Note that transcription is limited to 60 seconds audio.
//   *
//   * @param fileName the path to a PCM audio file to transcribe.
//     */
//
//    /**
//     * Performs non-blocking speech recognition on remote FLAC file and prints
//     * the transcription as well as word time offsets.
//     *
//     * @param gcsUri the path to the remote LINEAR16 audio file to transcribe.
//     */
//    public static void asyncRecognizeWords(String gcsUri) throws Exception, IOException {
//        // Instantiates a client with GOOGLE_APPLICATION_CREDENTIALS
//        SpeechClient speech = SpeechClient.create();
//
//        // Configure remote file request for Linear16
//        RecognitionConfig config = RecognitionConfig.newBuilder()
//                .setEncoding(AudioEncoding.FLAC)
//                .setLanguageCode("en-US")
//                .setSampleRateHertz(16000)
//                .setEnableWordTimeOffsets(true)
//                .build();
//        RecognitionAudio audio = RecognitionAudio.newBuilder()
//                .setUri(gcsUri)
//                .build();
//
//        // Use non-blocking call for getting file transcription
//        OperationFuture<LongRunningRecognizeResponse, LongRunningRecognizeMetadata> response
//                = speech.longRunningRecognizeAsync(config, audio);
//        while (!response.isDone()) {
//            System.out.println("Waiting for response...");
//            Thread.sleep(10000);
//        }
//
//        List<SpeechRecognitionResult> results = response.get().getResultsList();
//
//        for (SpeechRecognitionResult result : results) {
//            // There can be several alternative transcripts for a given chunk of speech. Just use the
//            // first (most likely) one here.
//            SpeechRecognitionAlternative alternative = result.getAlternativesList().get(0);
//            System.out.printf("Transcription: %s\n", alternative.getTranscript());
//            for (WordInfo wordInfo : alternative.getWordsList()) {
//                System.out.println(wordInfo.getWord());
//                System.out.printf("\t%s.%s sec - %s.%s sec\n",
//                        wordInfo.getStartTime().getSeconds(),
//                        wordInfo.getStartTime().getNanos() / 100000000,
//                        wordInfo.getEndTime().getSeconds(),
//                        wordInfo.getEndTime().getNanos() / 100000000);
//            }
//        }
//        speech.close();
//    }
//
//    /**
//     * Performs non-blocking speech recognition on remote FLAC file and prints
//     * the transcription.
//     *
//     * @param gcsUri the path to the remote LINEAR16 audio file to transcribe.
//     */
//    public static void asyncRecognizeGcs(String gcsUri) throws Exception, IOException {
//        // Instantiates a client with GOOGLE_APPLICATION_CREDENTIALS
//        SpeechClient speech = SpeechClient.create();
//
//        // Configure remote file request for Linear16
//        RecognitionConfig config = RecognitionConfig.newBuilder()
//                .setEncoding(AudioEncoding.FLAC)
//                .setLanguageCode("en-US")
//                .setSampleRateHertz(16000)
//                .build();
//        RecognitionAudio audio = RecognitionAudio.newBuilder()
//                .setUri(gcsUri)
//                .build();
//
//        // Use non-blocking call for getting file transcription
//        OperationFuture<LongRunningRecognizeResponse, LongRunningRecognizeMetadata> response
//                = speech.longRunningRecognizeAsync(config, audio);
//        while (!response.isDone()) {
//            System.out.println("Waiting for response...");
//            Thread.sleep(10000);
//        }
//
//        List<SpeechRecognitionResult> results = response.get().getResultsList();
//
//        for (SpeechRecognitionResult result : results) {
//            // There can be several alternative transcripts for a given chunk of speech. Just use the
//            // first (most likely) one here.
//            SpeechRecognitionAlternative alternative = result.getAlternativesList().get(0);
//            System.out.printf("Transcription: %s\n", alternative.getTranscript());
//        }
//        speech.close();
//    }
//
//    /**
//     * Performs streaming speech recognition on raw PCM audio data.
//     *
//     * @param fileName the path to a PCM audio file to transcribe.
//     */
//    public static void streamingRecognizeFile(String fileName) throws Exception, IOException {
//        Path path = Paths.get(fileName);
//        byte[] data = Files.readAllBytes(path);
//
//        // Instantiates a client with GOOGLE_APPLICATION_CREDENTIALS
//        SpeechClient speech = SpeechClient.create();
//
//        // Configure request with local raw PCM audio
//        RecognitionConfig recConfig = RecognitionConfig.newBuilder()
//                .setEncoding(AudioEncoding.LINEAR16)
//                .setLanguageCode("en-US")
//                .setSampleRateHertz(16000)
//                .build();
//        StreamingRecognitionConfig config = StreamingRecognitionConfig.newBuilder()
//                .setConfig(recConfig)
//                .build();
//
//        class ResponseApiStreamingObserver<T> implements ApiStreamObserver<T> {
//
//            private final SettableFuture<List<T>> future = SettableFuture.create();
//            private final List<T> messages = new java.util.ArrayList<T>();
//
//            @Override
//            public void onNext(T message) {
//                messages.add(message);
//            }
//
//            @Override
//            public void onError(Throwable t) {
//                future.setException(t);
//            }
//
//            @Override
//            public void onCompleted() {
//                future.set(messages);
//            }
//
//            // Returns the SettableFuture object to get received messages / exceptions.
//            public SettableFuture<List<T>> future() {
//                return future;
//            }
//        }
//
//        ResponseApiStreamingObserver<StreamingRecognizeResponse> responseObserver
//                = new ResponseApiStreamingObserver<StreamingRecognizeResponse>();
//
//        BidiStreamingCallable<StreamingRecognizeRequest, StreamingRecognizeResponse> callable
//                = speech.streamingRecognizeCallable();
//
//        ApiStreamObserver<StreamingRecognizeRequest> requestObserver
//                = callable.bidiStreamingCall(responseObserver);
//
//        // The first request must **only** contain the audio configuration:
//        requestObserver.onNext(StreamingRecognizeRequest.newBuilder()
//                .setStreamingConfig(config)
//                .build());
//
//        // Subsequent requests must **only** contain the audio data.
//        requestObserver.onNext(StreamingRecognizeRequest.newBuilder()
//                .setAudioContent(ByteString.copyFrom(data))
//                .build());
//
//        // Mark transmission as completed after sending the data.
//        requestObserver.onCompleted();
//
//        List<StreamingRecognizeResponse> responses = responseObserver.future().get();
//
//        for (StreamingRecognizeResponse response : responses) {
//            // For streaming recognize, the results list has one is_final result (if available) followed
//            // by a number of in-progress results (if iterim_results is true) for subsequent utterances.
//            // Just print the first result here.
//            StreamingRecognitionResult result = response.getResultsList().get(0);
//            // There can be several alternative transcripts for a given chunk of speech. Just use the
//            // first (most likely) one here.
//            SpeechRecognitionAlternative alternative = result.getAlternativesList().get(0);
//            System.out.println(alternative.getTranscript());
//        }
//        speech.close();
//    }
}
