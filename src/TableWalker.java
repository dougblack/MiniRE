import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;



public class TableWalker {

	String grammarFile;
	String programFile;
	DFA grammarDFA;
	HashMap<String, NFA> specNFAs;

	/**
	 * This "table-walker" program might need to break out into multiple 
	 * separate file, but for now we can visualize it's functionality as below.
	 * @param inFile
	 * @param grammarDFA
	 */
	public TableWalker(String grammarFile, String programFile) {
		SpecParser sp = new SpecParser();
		this.grammarFile = programFile;
		specNFAs = sp.parseFile(grammarFile);
	}
	
	public static void main (String args[]) {
		TableWalker tw = new TableWalker("src/spec.txt", "src/program.txt");
		
		String[] testStrings = {"a", "1", "1.1", "abac", "+", "-", "*", "=", "PRINT"};
		
		
		// Loop over test strings
		for (String test : testStrings) {
			
			/* Clear the state of the NFAs. This resets the "accepted" and "accepting"
			 * values, as well as the current state of the machine. */
			for (Map.Entry<String, NFA> specNFA : tw.specNFAs.entrySet()) {
				specNFA.getValue().reset();
			}
		
			/* Keep track of which NFAs have been accepted for each test string. */
			@SuppressWarnings("unchecked")
			HashMap<String, NFA> acceptingNFAs = (HashMap<String, NFA>) tw.specNFAs.clone(); 
			for (int i = 0; i < test.length(); i++) {
				char c = test.charAt(i);
				
				Iterator<Map.Entry<String, NFA>> entries = acceptingNFAs.entrySet().iterator();
				while (entries.hasNext()) {
					Map.Entry<String, NFA> specNFA = entries.next();
					
					String entry = specNFA.getKey();
					NFA nfa = specNFA.getValue();
					nfa.step(c);
					if (!nfa.accepting) {
						entries.remove();
					}
				}
				if (acceptingNFAs.size() == 1) {
					for (Map.Entry<String, NFA> entry : acceptingNFAs.entrySet()) {
						System.out.println(test + " was last accepted by: " + entry.getKey());
					}
					break;
				}
			}
		}
		
	}


	private void parseTokens(String strLine) {
		// check current input stream against
		// DFA to find tokens
	}
	
	private void gotToken(Token token) {
		System.out.println("Got token! Of type: " + token.getType());
	}

}