import java.util.ArrayList;
import java.util.HashMap;
import java.io.*;

public class Driver {

    private TableWalker tw;
    private ArrayList<Token> tokens;

    public static void main(String[] args) {
        Driver d = new Driver("spec2.txt", "input2.txt");
        d.generateTokens();
        ArrayList<Token> tokens = d.getTokens();

        for (int i = 0; i < tokens.size(); i++) {
            System.out.println(tokens.get(i).getId() + ": " + tokens.get(i).getString());
        }
    }

    public Driver(String grammarFile, String programFile) {
        SpecParser sp = new SpecParser();
        HashMap<String, DFA> dfas = sp.parseFile(grammarFile);
        HashMap<String, String> defs = sp.specDefinitions;
        tokens = new ArrayList<Token>();
        tw = new TableWalker(programFile, dfas, defs);
    }

    public void generateTokens() {
        boolean error = false;
        Token token;
        try {
            while ((token = tw.nextToken()).getId() != "%% EOF") {
                if (token.getId() == "%% ERROR") {
                    error = true;
                    System.out.println("Unknown token " + token.getString());
                    break;
                }
                tokens.add(token);
            }
            
        } catch (IOException e) {
            System.out.println("Error: IOException while processing tokens");
            error = true;
        }
        if (!error) {
            System.out.println("Finished processing tokens");
        }
    }

    public ArrayList<Token> getTokens() {
        return tokens;
    }
}
