import java.io.*;
import java.util.HashMap;

public class TableWalker {

    private static final int SPACE = 32; // ASCII code for space; lowest code for a printable char
    private static final int DELETE = 127; // ASCII code for delete; first non-printable char after the printable chars
    private static final char EOF = (char)-1; // signals end of programFile
    private GetChar gc;
	private HashMap<String, DFA> dfas;
    private HashMap<String, String> defs;
    private HashMap<DFA, String> viableDFAs;
    private boolean locked, noToken, finishedStepping;
    private String buffer, tokenId, tokenString, lastAcceptedString;

	/**
	 * Create a TableWalker to generate tokens from programFile according to the
     * given dfas.
	 * @param programFile
	 * @param dfa
	 */
	public TableWalker(String programFile, HashMap<String, DFA> dfas,
        HashMap<String, String> defs) {
        locked = false;

        try {
		    gc = new GetChar(programFile);
        } catch (FileNotFoundException e) {
            // Signal an error somehow
            System.out.println("File not found");
            locked = true;
        } catch (IOException e) {
            // Signal an error somehow
            System.out.println("IOException");
            locked = true;
        }
        this.dfas = dfas;
        this.defs = defs;
        buffer = "";
	}

    // Get next char from either buffer or input
    // Check if it's EOF
    // Check if it's non-printable
    //
    // step through each DFA until one match is left
    // buffer the extra chars (don't buffer EOF)
    // return a token
    public Token nextToken() throws IOException {
        if (locked) {
            System.out.println("Error creating TableWalker. Scanner locked.");
            return new Token("%% ERROR", "");
        }
        noToken = true;
        finishedStepping = false;
        lastAcceptedString = "";
        tokenString = "";
        tokenId = "%% ERROR";
        char c;
        if (buffer.length() > 0) {
            c = buffer.charAt(0);
            buffer = buffer.substring(1);
        } else {
            c = advanceToToken();
        }

        if (c == EOF) {
            return new Token("%% EOF", "");
        }
            
        viableDFAs = new HashMap<DFA, String>();
        for (String s : dfas.keySet()) {
            viableDFAs.put(dfas.get(s), s);
            dfas.get(s).reset();
        }
        do {
            //System.out.println(c);
            step(c);
            tokenString = tokenString + c;
            if (finishedStepping) {
                //System.out.println("breaking while loop");
                break;
            }
        } while (((c = nextChar()) > SPACE) && (c < DELETE));
        if (noToken) {
            //System.out.println("no Token");
            if (c == EOF) {
                return new Token("%% EOF", "");
            } else {
                return new Token("%% ERROR", tokenString);
            }
        } else {
            //System.out.println("returning token " + tokenId + ": " + lastAcceptedString);
            buffer(lastAcceptedString, tokenString);
            return new Token(tokenId, lastAcceptedString);
        }
    }

    private void step(char c) {
        for (DFA d : viableDFAs.keySet().toArray(new DFA[0])) {
            //System.out.print("\n" + viableDFAs.get(d));
            if (d.currentlyRecognizes(c)) {
                //System.out.print(" recognizes " + c);
                d.step(c);
                if (d.isAccepting()) {
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
            //System.out.println("Done Stepping");
            finishedStepping = true;
            
        }
    }

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

    private void buffer(String str, String diff) {
        if (diff.contains(str)) {
            diff = diff.substring(str.length());
            buffer = diff + buffer;
        }
    }

    /*
     * Scans the file until either an ASCII-printable character or EOF is found.
     * 
     * @return The ASCII-printable character found, or -1 if EOF was reached
     * @throws IOException
     */
    private char advanceToToken() {
        char currentChar;
        
        while (((currentChar = gc.getNextChar()) != EOF)
                && ((currentChar <= SPACE) || (currentChar >= DELETE)));
        return currentChar;
    }
}
