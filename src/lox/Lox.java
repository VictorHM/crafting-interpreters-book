package lox;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
//import java.util.Scanner;

import lox.Token;
import lox.TokenType;
import lox.Scanner;

public class Lox {
    static boolean handError = false;
    public static void main(String[] args) throws IOException {
        if (args.length > 1) {
            System.out.println("Usage: jlox [script]:");
            System.exit(64);
        } else if (args.length == 1) {
            runFile(args[0]);
        } else {
            runPrompt();
        }
    }

private static void runFile(String path) throws IOException {
    byte[] bytes = Files.readAllBytes(Paths.get(path));
    run(new String(bytes, Charset.defaultCharset()));

    // Indicate if an error exists in the code
    if (handError) System.exit(65);
}

private static void runPrompt() throws IOException {
    InputStreamReader input = new InputStreamReader(System.in);
    BufferedReader reader = new BufferedReader(input);

    for (;;) {
        System.out.printf("> ");
        String line = reader.readLine();
        if (line == null) break;
        run(line);
        handError = false;
    }
}

private static void run(String source) {
    Scanner scanner = new Scanner(source);
    List<Token> tokens = scanner.scanTokens();

    // For now, just print the tokens
    for (Token token : tokens) {
        System.out.println(token);
    }
}

static void error(int line, String message) {
    report(line, "", message);
}

// TODO dar mensaje mas detallado sobre la columna donde se encuentra el error, no solo la linea.
private static void report(int line, String where, String message) {
    System.err.println(
        "[line " + line + "] Error" + where + ": " + message);
    handError = true;
}
}