package com.example.Evision_technical_assessment.Services.Similarity_Processing;

import com.example.Evision_technical_assessment.Models.SimilarityResult;
import com.example.Evision_technical_assessment.Services.Text_Processing.Text_Processing_Service;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@Service
public class Cosine_Similarity_Service {

    @Value("${fileAPath}")
    private String fileAPath;

    @Value("${poolDirectoryPath}")
    private String poolDirectoryPath;

    private int available_resources = Math.max(1, Runtime.getRuntime().availableProcessors());
    private final List<SimilarityResult> lastResults = new CopyOnWriteArrayList<>();

    @PostConstruct
    public void computeOnStartup() {
        try {
            computeSimilarities();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<SimilarityResult> getLastResults() {
        return Collections.unmodifiableList(lastResults);
    }

    public void computeSimilarities() throws IOException, InterruptedException {

        if (fileAPath == null || poolDirectoryPath == null) {
            throw new IllegalArgumentException("The paths for the Pool directory and File A should be defined ");
        }
        // build a map of every word and its freq from file A
        Map<String, Integer> mapA = new HashMap<>(1 << 16);
        Text_Processing_Service.countWords(Path.of(fileAPath), mapA);

        //after reading the content of the file we create a vector representation of file A
        double normA = computeVectorNorm(mapA);

        // build a list of files that exist in the pool directory
        List<Path> poolFiles;
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(Path.of(poolDirectoryPath))) {
            poolFiles = new ArrayList<>();
            for (Path p : stream) {
                if (Files.isRegularFile(p)) poolFiles.add(p);
            }
        }

        // we do a thread pool where every thread is responsible for parallel reading of the files in the directory
        int parallelism = Math.max(1, Math.min(available_resources, Math.max(1, poolFiles.size())));
        ExecutorService ex = Executors.newFixedThreadPool(parallelism);

        try {
            // assign a thread that will execute the compute_similarity function for every file in the path (p) in the pool dir against file A
            List<Callable<SimilarityResult>> tasks = poolFiles.stream()
                    .map(p -> (Callable<SimilarityResult>) () -> computeSimilarityForFile(p, mapA, normA))
                    .collect(Collectors.toList());

            List<Future<SimilarityResult>> futures = ex.invokeAll(tasks);

            List<SimilarityResult> results = new ArrayList<>(futures.size());
            for (Future<SimilarityResult> f : futures) {
                try {
                    results.add(f.get());
                } catch (ExecutionException ee) {
                    ee.getCause().printStackTrace();
                }
            }


            lastResults.clear();
            lastResults.addAll(results);

        } finally {
            ex.shutdown();
        }
    }

    private synchronized SimilarityResult computeSimilarityForFile(Path file, Map<String, Integer> mapA, double normA) throws IOException {
        Map<String, Integer> mapB = new HashMap<>(1 << 16);
        Text_Processing_Service.countWords(file, mapB);
        double score = 0.0 ;
        long dot = 0L;


        Set<String> intersectionKeys = new HashSet<>(mapA.keySet()); // Start with keys from map1
        intersectionKeys.retainAll(mapB.keySet());

        // if file b doesn't have all the words in file A then we will return 0
        if (mapA.size()!=intersectionKeys.size())
        {
            return new SimilarityResult(file.getFileName().toString(), 0);
        }
        long total_words = mapA.values().stream().mapToLong(Integer::longValue).sum();

        long matching_words=0L;
        // means that file b has more words or equal words to those in file a
        if (mapA.size() <= mapB.size()) {
            for (Map.Entry<String, Integer> e : mapA.entrySet()) {
                int freqA = e.getValue();
                int freqB = mapB.getOrDefault(e.getKey(), 0);
                matching_words+=Math.min(freqA,freqB);
                dot += (long) freqA * (long) freqB;
            }
        }

        double normB = computeVectorNorm(mapB);

        if (normA == 0.0 || normB == 0.0) {
            score = (normA == 0.0 && normB == 0.0) ? 100.0 : 0.0;
        } else {
            double cos = dot / (normA * normB);
            cos = Math.max(0.0, Math.min(1.0, cos));
            score = cos * 100.0;
        }
//        score = ((double) matching_words/total_words)*100;
//        synchronized (System.out) {
//            System.out.println("The number of matching words for file " +
//                    file.getFileName() + " = " + matching_words + " out of " + total_words);
//            System.out.println("---------------------------");
//            }
        return new SimilarityResult(file.getFileName().toString(), score);
    }

    private synchronized double computeVectorNorm(Map<String, Integer> map) {
        double s = 0.0;
        for (int v : map.values()) {
            s += (double) v * (double) v;
        }
        return Math.sqrt(s);
    }

}
