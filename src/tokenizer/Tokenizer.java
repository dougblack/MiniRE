package tokenizer;
import java.util.ArrayList;
import java.util.HashMap;
import java.io.*;

/**
 * Generates and stores tokens from a program file using token definitions from
 * a grammar file
 */
public class Tokenizer {

	int current_token_index = 0;
	
    public static void main(String[] args) {
        Tokenizer d = new Tokenizer("src/spec2.txt", "src/input2.txt");
        d.generateTokens();
        ArrayList<Token> tokens = d.getTokens();

        System.out.println();
        for (int i = 0; i < tokens.size(); i++) {
            System.out.println(tokens.get(i).getId() + ": " +
                tokens.get(i).getString());
        }
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
    public Tokenizer(String grammarFile, String programFile) {
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
        try {
            while ((token = tw.nextToken()).getId() != "%% EOF") {
                if (token.getId().equals("%% ERROR")) {
                    error = true;
                    System.out.println("Unknown token " + token.getString() + " at index " + token.getIndex());
                    break;
                }
                if (token.getId().equals("$ID"))
                    token = checkForReservedWord(token);
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

    public Token checkForReservedWord(Token token) {
        if (token.getString().equals("begin")) {
            token.setId("$BEGIN");
        } else if (token.getString().equals("end")) {
            token.setId("$END");
        } else if (token.getString().equals("find")) {
            token.setId("$FIND");
        } else if (token.getString().equals("with")) {
            token.setId("$WITH");
        } else if (token.getString().equals("in")) {
            token.setId("$IN");
        } else if (token.getString().equals("print")) {
            token.setId("$PRINT");
        } else if (token.getString().equals("replace")) {
            token.setId("$REPLACE");
        } else if (token.getString().equals("recursivereplace")) {
            token.setId("$RECREP");
        } else if (token.getString().equals("inters")) {
            token.setId("$INTERS");
        } else if (token.getString().equals("union")) {
            token.setId("$UNION");
        } else if (token.getString().equals("diff")) {
            token.setId("$DIFF");
        } else if (token.getString().equals("maxfreqstring")) {
            token.setId("$MAXFREQ");
        }
        return token;
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
   
    /**
     * This method returns true if the token matches the next token in the array. 
     * It then "removes" this token by incrementing the current_token_index.
     * @param token
     * @return
     */
    public boolean matchToken(Token token) { 
    	boolean isMatch =  tokens.get(this.current_token_index).toString().equals(token.toString());
    	current_token_index++;
    	return isMatch;
    }

    public void consumeToken() {
        this.current_token_index++;
    }
   
    /**
     * This method returns the next token without incrementing the current_token_index.
     * @return
     */
    public Token peekToken() {
    	return tokens.get(this.current_token_index); 
    }
}
