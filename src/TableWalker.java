import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Reader;



public class TableWalker {

	File file;
	String inFile;
	DFA grammarDFA;

	/**
	 * This "table-walker" program might need to break out into multiple 
	 * separate file, but for now we can visualize it's functionality as below.
	 * @param inFile
	 * @param grammarDFA
	 */
	public TableWalker(String inFile, DFA grammarDFA) {
		this.file = new File(inFile);
		this.grammarDFA = grammarDFA;
	}

	public void parseFile() {
		try{
			// Open the file that is the first 
			// command line parameter
			FileInputStream fstream = new FileInputStream("textfile.txt");
			// Get the object of DataInputStream
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String strLine;
			//Read File Line By Line
			while ((strLine = br.readLine()) != null)   {
				parseTokens(strLine);
			}
			//Close the input stream
			in.close();
		}catch (Exception e){//Catch exception if any
			System.err.println("Error: " + e.getMessage());
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