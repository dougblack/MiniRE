import java.io.DataInputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Buffer {
    
    private String line = "";
    private int column = 0;
    private int lineNo = 0;
    private BufferedReader in;

    public Buffer (DataInputStream in) {
        this.in = new BufferedReader(new InputStreamReader(in));
    }


    public char get ( ) {
        column++;
        if (column >= line.length()) {
            try {
                line = in.readLine();
            } 
            catch (Exception e) {
                System.out.println("Buffer error");
                e.printStackTrace();
                System.exit(1);
            }
            if (line == null) {
                System.exit(0);
            }
            column = 0;
            lineNo++;
            System.out.println(line);
            line = line + "\n";
        } 
        return line.charAt(column);
    } 
} 