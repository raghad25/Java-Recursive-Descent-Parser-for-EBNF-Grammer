package application;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.StringTokenizer;

public class ScannerFile {

    public static StringBuilder tokenList;
    public static String FileName;
    private static int lineNumber = 1; 
    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        System.out.print("Enter the file name that contains the code to compile: ");
        FileName = scan.nextLine();
        scan.close();

        processFile();
        RrcursiveParsing main = new RrcursiveParsing(tokenList);
        main.mycode();

    }


    public static void processFile() {
        File file = new File(FileName);

        try {
            Scanner scanner = new Scanner(file);
            tokenList = new StringBuilder();
            lineNumber = 1; // Initialize line number

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim(); // Remove leading/trailing spaces

                if (!line.isEmpty()) { // Skip empty lines
                    StringTokenizer tokenizer = new StringTokenizer(line, "(<+-,#*/=;>)", true);

                    while (tokenizer.hasMoreTokens()) {
                        String token = tokenizer.nextToken();
                        tokenList.append(token).append(" ");
                        tokenList.append(new Token(token, lineNumber).toString()).append(" "); // Append line number to token
                    }
                    tokenList.append("\n"); // Add newline after each line
                }
                lineNumber++; // Increment line number for each line
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
