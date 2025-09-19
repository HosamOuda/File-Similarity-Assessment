package com.example.Evision_technical_assessment.Services.Text_Processing;
import org.springframework.stereotype.Service;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Map;
import java.nio.file.Path;

@Service
public class Text_Processing_Service {

    private static final int BUFFER_CHARS = 100 * 1024;

    // here we read the content of the file in terms of chunks each of which with a size of 100K to maximize efficiency
    //then we store the resulted chunks in outMap
     public static void countWords(Path file, Map<String, Integer> outMap) throws IOException {
         try (BufferedInputStream bis = new BufferedInputStream(Files.newInputStream(file), 1 << 20);
              Reader r = new InputStreamReader(bis, StandardCharsets.UTF_8)) {

                    char[] buf = new char[BUFFER_CHARS];
                    StringBuilder sb = new StringBuilder(32);
                    int read;
                    while ((read = r.read(buf)) != -1) {
                        for (int i = 0; i < read; i++) {
                            char c = buf[i];
                            if (Character.isLetter(c)) {
                                sb.append(Character.toLowerCase(c));
                            } else {
                                if (sb.length() > 0) {
                                    String word = sb.toString();
                                    outMap.merge(word, 1, Integer::sum);
                                    sb.setLength(0);
                                }
                            }
                        }
                    }
                    if (sb.length() > 0) {
                        String word = sb.toString();
                        outMap.merge(word, 1, Integer::sum);
                    }
                }
            }


}