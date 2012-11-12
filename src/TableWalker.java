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
		
		/* The set of test strings */
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
				
				/* We have to use an iterator or we'll run into ConcurrentModificationException */
				Iterator<Map.Entry<String, NFA>> entries = acceptingNFAs.entrySet().iterator();
				while (entries.hasNext()) {
					Map.Entry<String, NFA> specNFA = entries.next();
					
					String entry = specNFA.getKey();
					NFA nfa = specNFA.getValue();
					
					/* Give this NFA this character */
					nfa.step(c);
				
					/* If it's not accepting, kick it out of our acceptingNFAs set */
					if (!nfa.accepting) {
						entries.remove();
					}
				}
				
				/* This is bad logic. It pretends that the last remaining NFA has accepted this string. */
				if (acceptingNFAs.size() == 1) {
					for (Map.Entry<String, NFA> entry : acceptingNFAs.entrySet()) {
						System.out.println(test + " was last accepted by: " + entry.getKey());
					}
					break;
				}
			}
		}
		
	}

}