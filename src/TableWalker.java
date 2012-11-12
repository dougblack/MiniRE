import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
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
		for (String test : testStrings) {
			System.out.println("TESTING STRING: " + test);
			for (Map.Entry<String, NFA> specNFA : tw.specNFAs.entrySet()) {
				String entry = specNFA.getKey();
				NFA nfa = specNFA.getValue();
				System.out.println("Testing for: " + entry);
				nfa.testNFA(test);
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