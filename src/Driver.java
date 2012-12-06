import java.util.ArrayList;
import java.util.HashMap;
import java.io.*;

/**
 * Generates and stores tokens from a program file using token definitions from
 * a grammar file
 */
public class Driver {

    public static void main(String[] args) {
        String grammarFile, programFile;

        if (args.length != 2) {
            System.out.println("Invalid args");
            return;
        }
        grammarFile = args[0];
        programFile = args[1];

        Driver d = new Driver(grammarFile, programFile);
        d.generateTokens();
    }

    private TableWalker tw; // generates tokens
    private ArrayList<Token> tokens; // stores tokens

    /**
     * Accepts a grammar file containing token definitions and a program file
     * to scan for tokens. Parses the grammar file and constructs DFAs and a
     * table walker to scan the program file.
     * 
     * @param grammarFile A file containing token definitions
     * @param programFile A file to scan for tokens
     */
    public Driver(String grammarFile, String programFile) {
        SpecParser sp = new SpecParser();
        HashMap<String, DFA> dfas = sp.parseFile(grammarFile);
        tokens = new ArrayList<Token>();
        tw = new TableWalker(programFile, dfas);
    }

    /**
     * Repeatedly calls the table walker for the next token in the program file.
     * Stores each token until the end of the file is reached or an unknown
     * token is encountered.
     */
    public void generateTokens() {
        boolean error = false;
        Token token;
        System.out.println("\nPROCESSING TOKENS.");
        try {
            while ((token = tw.nextToken()).getId() != "%% EOF") {
                if (token.getId() == "%% ERROR") {
                    System.out.println("Unknown token: " + token.getString());
                    continue;
                } else {
                    System.out.println(token.getId() + ": " +
                        token.getString());
                }
                tokens.add(token);
            }
            
        } catch (IOException e) {
            System.out.println("ERROR: IOEXCEPTION WHILE PROCESSING TOKENS.");
            error = true;
        }
        if (!error) {
            System.out.println("FINISHED PROCESSING TOKENS.");
        }
    }

    /**
     * Returns all scanned tokens up to the end of the file or the first unknown
     * token, in the order they were scanned
     *
     * @return A list of all scanned tokens, in the order they were scanned
     */
    public ArrayList<Token> getTokens() {
        return tokens;
    }
}
