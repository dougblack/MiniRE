package tokenizer;
// GetChar: fetch next char 
import java.io.*;
import java.nio.charset.Charset;
public class GetChar { 
   private Reader in; // internal file name for input stream
   private BufferedReader reader;
   private int index; // index of the previous character that was read from the file
    
   public GetChar (String filename) throws FileNotFoundException, IOException { 
	   File file = new File(filename);
	   reader=new BufferedReader(
	   new InputStreamReader(
		        new FileInputStream(file),
		        Charset.forName("UTF-8")));
       index = 0;
   }

   public char getNextChar() {
      char ch=(char)-1;//' '; // = ' ' to keep compiler happy
      try {
         ch = (char)reader.read();  
      } catch (IOException e) {
         System.out.println("char exception");
      }
      if (ch != '\r') {
        index++;
      }
      return ch;
   }

    /**
     * Returns the index of the previous character that was read from the file
     * 

     * @return The index of the previous character that was read from the file

     */
	public int getIndex() {
		return index;
	}
}
