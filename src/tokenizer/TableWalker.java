package tokenizer;
import java.io.*;
import java.util.HashMap;

/**
 * Scans a given program file to construct tokens using the given DFAs
 */
public class TableWalker {

    private static final int SPACE = 32; // lowest printable ASCII character
    private static final int DELETE = 127; // first non-printable ASCII
                                             // character after all printables
    private static final char EOF = (char) -1; // signals end of programFile
    private GetChar gc; // gets the next character from the program file
	private HashMap<String, DFA> dfas; // DFAs from each token definition
    private HashMap<DFA, String> viableDFAs; // DFAs that may accept the string
                                              // currently being processed
    private boolean noToken; // signal to return either an error or EOF 
    private boolean finishedStepping; // true only when a longest match token
                                        // string has been generated
    private String buffer; // stores previously-read, untokenized characters
    private String tokenId; // identifier for the token being generated
    private String file; // a file to scan for tokens

    private int row;
    private int start;
    private int end;
    private int temp;
    
    private int spacecounter;
    /* tokenString is the longest string currently being accepted by a DFA, a
       potential string for the current token. If tokenString ends up not being
       accepted by the last DFA, the table walker attempts to return a token
       with lastAcceptedString as its string; lastAcceptedString is tokenString
       as it was the last time a DFA accepted it */
    private String tokenString;
    private String lastAcceptedString;

	/**
	 * Constructs a table walker to generate tokens from a given program file
     * using the given DFAs
     *
	 * @param programFile A file to scan for tokens
	 * @param dfas All DFAs mapped to their corresponding token identifiers
	 */
	public TableWalker(String programFile, HashMap<String, DFA> dfas) {
		row = 1;
		start = 1;
		end = 1;
		temp = 1;
        programFile = programFile.replaceAll("\"", "");
        System.out.println("File: " + programFile);
		//spacecounter = 0;
        try {
		    gc = new GetChar(programFile);
        } catch (FileNotFoundException e) {
            // Signal an error somehow
            System.out.println("File not found");
        } catch (IOException e) {
            // Signal an error somehow
            System.out.println("IOException");
        }
        this.dfas = dfas;
        file = programFile;
        buffer = "";
	}

    
    /**
     * Scans the program file for the next longest-match token
     * 
     * @return A longest-match token from the program file if found, EOF if the
     *          end of the file was reached, or an error if an unknown token
     *          was encountered or some error occurred
     */
    public Token nextToken() throws IOException {
        noToken = true;
        finishedStepping = false;
        lastAcceptedString = tokenString = "";
        tokenId = "%% ERROR";
        //start = end;
        char c;

        start = temp;
        // Get next char from either buffer or input
        if (buffer.length() > 0) {
            c = buffer.charAt(0);
            buffer = buffer.substring(1);
        } else {
            c = advanceToToken();
        }

        // step through each viable DFA until a longest match is left
        viableDFAs = new HashMap<DFA, String>();
        for (String s : dfas.keySet()) {
            viableDFAs.put(dfas.get(s), s);
            dfas.get(s).reset();
        }
        do {
            //System.out.println(c);
            step(c);
            tokenString = tokenString + c;
            //System.out.println("Tokenc: " + tokenString);
            //end++;
            if (finishedStepping) {
                //System.out.println("breaking while loop");
                break;
            }
        } while (((c = nextChar()) > SPACE) && (c < DELETE));
        if(c == SPACE || c == DELETE) {
        	spacecounter++;
        }
        if (noToken) {
            //System.out.println("no Token");

            if (c == EOF) {
                return new Token("%% EOF", "", file, start, end, row);
            } else {
                return new Token("%% ERROR", file, tokenString, start, end, row);
            }
        } else {
            //System.out.println("returning token " + tokenId + ": " +
            //    lastAcceptedString);
            // buffer any chars that were read but not returned
            buffer(lastAcceptedString, tokenString);

            end = start + lastAcceptedString.length();
            temp = end + spacecounter;
            
            //System.out.println("Start: " + start + " End: " + end + " Row: " + row + " Token: " + lastAcceptedString + " spacecounter: " + spacecounter);
            spacecounter = 0;
            return new Token(tokenId, lastAcceptedString, file, start, end, row);
        }
    }

    /**
     * If any viable DFA's current state can transition on the given character,
     * it does. Otherwise the DFA is removed from the list of viable DFAs that
     * could possibly generate a token for the current string.
     *
     * @param c A character
     */
    private void step(char c) {
        for (DFA d : viableDFAs.keySet().toArray(new DFA[0])) {
            //System.out.print("\n" + viableDFAs.get(d));
            if (d.transitionsOn(c)) {
                //System.out.print(" recognizes " + c);
                d.step(c);
                if (d.inAcceptState()) {
                    //System.out.print(" accepts " + c);
                    noToken = false;
                    tokenId = viableDFAs.get(d);
                    lastAcceptedString = tokenString + c;
                }
            } else {
                //System.out.print(" doesn't recognize " + c);
                viableDFAs.remove(d);
            }
        }
        //System.out.println("\nStepped once");
        if (viableDFAs.size() < 1) {
            //System.out.println("Finished Stepping");
            finishedStepping = true;
            
        }
    }

    /**
     * If the buffer is not empty, debuffers and returns the first buffered
     * character. Otherwise returns the next character in the program file
     * (printable or not)
     * 
     * @return The next available character to consider as part of the token
     *          string
     */
    private char nextChar() {
        char c;
        if (buffer.length() > 0) {
            c = buffer.charAt(0);
            buffer = buffer.substring(1);
        } else {
            c = gc.getNextChar();
        }
        return c;
    }

    /**
     * Buffers the characters that were considered but not used in a token
     * string.
     * 
     * @param str The characters used in the current token being returned
     * @param diff All characters considered for the current token string.
     */
    private void buffer(String str, String diff) {
        if (diff.contains(str)) {
            diff = diff.substring(str.length());
            buffer = diff + buffer;
        }
    }

    /**
     * Scans the file until either an ASCII-printable character or EOF is found.
     * 
     * @return The character found, or (char) -1 if EOF was reached
     */
    private char advanceToToken() {
        char currentChar;
        
        while (((currentChar = gc.getNextChar()) != EOF) && ((currentChar <= SPACE) || (currentChar >= DELETE))) {
            //System.out.println(currentChar);
        	if(currentChar == SPACE) {
        		start++;
        	}
        	if(currentChar == '\n') {
        		row++;
        		start = 1;
        		temp = 1;
        		end = 1;
        	}
        }

        return currentChar;
    }
}
