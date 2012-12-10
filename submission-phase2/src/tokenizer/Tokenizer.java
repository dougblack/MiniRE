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
    private boolean acceptsRegexAsFile;
	
    public static void main(String[] args) {
        //Tokenizer d = new Tokenizer("token_spec.txt", "input2.txt");
        Tokenizer d = new Tokenizer("ment", "([A-Za-z])*ment([A-Za-z])*", "input2.txt");
        d.generateTokens();
        ArrayList<Token> tokens = d.getTokens();

        System.out.println();
        for (int i = 0; i < tokens.size(); i++) {
            System.out.println(tokens.get(i).getId() + ": " +
                tokens.get(i).getString() + " at index " +
                tokens.get(i).getIndex() + " in file " +
                tokens.get(i).getFile());
        }
    }

    private GraphWalker gw; // generates tokens
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
        acceptsRegexAsFile = true;
        SpecParser sp = new SpecParser();
        HashMap<String, DFA> dfas = sp.parseFile(grammarFile);
        tokens = new ArrayList<Token>();
        gw = new GraphWalker(programFile, dfas);
    }

    /**
     * Accepts a name/id for a regex, a regex itself, and a program file to
     * scan for tokens matching that regex. Constructs a DFA from the regex,
     * then uses a table walker to scan the program file.
     * 
     * @param regexId A string identifying the given regex (a name/id for it)
     * @param regex A string representing a regex
     * @param programFile A file to scan for tokens using matching regex
     */
    public Tokenizer(String regexId, String regex, String programFile) {
        acceptsRegexAsFile = false;
        HashMap<String, DFA> dfas = new HashMap<String, DFA>();
        dfas.put(regexId, new DFA(new NFA(regex), 1));
        tokens = new ArrayList<Token>();
        gw = new GraphWalker(programFile, dfas);
    }

    /**
     * Repeatedly calls the table walker for the next token in the program file.
     * Stores each token until the end of the file is reached or an unknown
     * token is encountered.
     *
     * @return All tokens scanned from the file
     */
    public ArrayList<Token> generateTokens() {
        if (!acceptsRegexAsFile) {
            Token token;
            try {
                while ((token = gw.nextToken()).getId() != "%% EOF") {
                    if (!token.getId().equals("%% ERROR")) {
                        tokens.add(token);
                    }
                }
            } catch (IOException e) {
                System.out.println("Error: IOException while processing tokens");
            }
        } else {
            Token token;
            try {
                while ((token = gw.nextToken()).getId() != "%% EOF") {
                    if (token.getId().equals("%% ERROR")) {
                        System.out.println("Unknown token " + token.getString() +
                            " at index " + token.getIndex() + " in file " +
                            token.getFile());
                        break;
                    }
                    tokens.add(token);
                }
                
            } catch (IOException e) {
                System.out.println("Error: IOException while processing tokens");
            }
        }
        return tokens;
    }

    /**
     * Returns all scanned tokens up to the end of the file or the first unknown
     * token, in the order they were scanned
     *
     * @return A list of all scanned tokens, in the order they were scanned
     */
    public ArrayList<Token> getTokens() {
//        for (int i = 0; i < tokens.size(); i++) {
//            System.out.println(tokens.get(i).getId() + ": " +
//                    tokens.get(i).getString() + " at index " +
//                    tokens.get(i).getIndex() + " of file " +
//                    tokens.get(i).getFile());
//        }
        return tokens;
    }
   
    /**
     * This method returns true if the token matches the next token in the array. 
     * It then "removes" this token by incrementing the current_token_index.
     *
     * @param token a Token to compare against the next token in the array
     * @return true if and only if the token matches the next token in the array
     */
    public boolean matchToken(Token token) { 
    	boolean isMatch =  tokens.get(this.current_token_index).toString().equals(token.toString());
    	current_token_index++;
    	return isMatch;
    }

    /**
     * Increments the current token index
     */
    public void consumeToken() {
        this.current_token_index++;
    }
   
    /**
     * Returns the next token without incrementing the current_token_index.
     * @return the next token
     */
    public Token peekToken() {
    	return tokens.get(this.current_token_index); 
    }
}
