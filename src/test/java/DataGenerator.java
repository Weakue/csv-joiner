import org.junit.Test;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Random;
import java.util.UUID;

/**
 * Created by Alexey Puks on 16.11.16.
 */
public class DataGenerator {
    @Test
    public void generate(){
        Random r = new Random();
        String file1 = "./first.csv";
        String file2 = "./second.csv";
       // System.out.println("Writing to file: " + file);
        // Files.newBufferedWriter() uses UTF-8 encoding by default
        fillFile(r, file1);
        fillFile(r, file2);
    }

    private void fillFile(Random r, String file) {
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(file))) {
            for (int i = 0; i < 10000000; i++) {
                writer.write(String.format("%09d,%s \n", r.nextInt(99999999), UUID.randomUUID().toString()));
            }
        } // the file will be automatically closed
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
