package tokenizer;
import java.io.*;
import java.nio.charset.Charset;

/**
 * Used with a particular file to return characters sequentially until the end
 * of the file has been reached
 */
public class GetChar { 
    private static final int SPACE = 32; // lowest printable ASCII character
    private static final int DELETE = 127; // first non-printable ASCII
                                             // character after all printables
    private static final char EOF = (char) -1; // signals end of programFile

    private Reader in; // internal file name for input stream
    private BufferedReader reader;
    private long index; // index of the previous character read from the file
    
    /**
     * Constructs a new GetChar for processing the given file
     *
     * @param filename Name of the file this GetChar will process
     */
    public GetChar (String filename) throws FileNotFoundException, IOException { 
        File file = new File(filename);
        reader=new BufferedReader(
        new InputStreamReader(new FileInputStream(file), Charset.forName("UTF-8")));
        index = 0;
    }

    /**
     * Returns the next sequential character from the file, or (char) -1 if the
     * end of the file has been reached
     *
     * @return The next sequential character from the file, or (char) -1 if the
     *          end of the file has been reached
     */
    public char getNextChar() {
        char ch = EOF;//' '; // = ' ' to keep compiler happy
        try {
            ch = (char)reader.read();  
        } catch (IOException e) {
            System.out.println("char exception");
        }
        
        if ((ch >= SPACE) || (ch < DELETE)) {
            index++;
        }
        return ch;
    }

    /**
     * Returns the index of the previous character that was read from the file
     * 
     * @return The index of the previous character that was read from the file
     */
    public long getIndex() {
        return index;
    }
}
