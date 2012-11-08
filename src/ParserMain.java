import java.util.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;

public class ParserMain {
	
	public static void main(String[] args) throws FileNotFoundException, IOException {

		GetChar _char;
		System.out.println("Starting...: ");
		try{
		_char = new GetChar("/home/alazar/workspace/cs3240-p1/src/test.txt");
		
		char ch;
		while((ch = _char.getNextChar())!=(char)-1){
			
			System.out.print(ch);
		}
		}
		catch(IOException e){
			
			e.printStackTrace();
			
		}
		
		
		
	}
}

