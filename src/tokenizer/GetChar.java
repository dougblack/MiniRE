package tokenizer;
import java.io.*;
import java.nio.charset.Charset;

/**
 * Used with a particular file to return characters sequentially until the end
 * of the file has been reached
 */
public class GetChar { 
   private Reader in; // internal file name for input stream
   private BufferedReader reader;
    
    /**
     * Constructs a new GetChar for processing the given file
     *
     * @param filename Name of the file this GetChar will process
     */
   public GetChar (String filename) throws FileNotFoundException, IOException { 
	   File file = new File(filename);
	   reader=new BufferedReader(
	   new InputStreamReader(
		        new FileInputStream(file),
		        Charset.forName("UTF-8")));
   }

    /**
     * Returns the next sequential character from the file, or (char) -1 if the
     * end of the file has been reached
     *
     * @return The next sequential character from the file, or (char) -1 if the
     *          end of the file has been reached
     */
   public char getNextChar() {
      char ch=(char)-1;//' '; // = ' ' to keep compiler happy
      try {
         ch = (char)reader.read();  
      } catch (IOException e) {
         System.out.println("char exception");
      }
      return ch;
   }
}
