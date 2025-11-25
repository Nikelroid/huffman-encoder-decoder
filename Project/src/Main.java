import java.io.*;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

class Main{
    static String text;
public static void main(String[]args) throws IOException, URISyntaxException {
    String OUTPUT = "encoded12.txt";
    String INPUT = "input12.txt";
    String DECODED = "decoded12.txt";
    ClassLoader classloader = Thread.currentThread().getContextClassLoader();

    System.out.print("Input lossy factor: ");
    Scanner scanner = new Scanner(System.in);
    int factor = scanner.nextInt();

    InputStreamReader streamReader = new InputStreamReader(classloader.getResourceAsStream(INPUT),
            StandardCharsets.UTF_8);
    BufferedReader br = new BufferedReader(streamReader);
    try {
        StringBuilder sb = new StringBuilder();
        String line = br.readLine();

        while (line != null) {
            sb.append(line);
            sb.append(System.lineSeparator());
            line = br.readLine();
        }
        text = sb.toString();
    } finally {
        br.close();
    }



    Encoding encoding = new Encoding();
    Decoding decoding = new Decoding();
    encoding.run(text,OUTPUT,factor);
    String decodedText = decoding.run(OUTPUT);

    BufferedWriter writer = new BufferedWriter(new FileWriter(DECODED));
    writer.write(decodedText);
    writer.close();


}



}