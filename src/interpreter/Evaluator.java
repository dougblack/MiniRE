package interpreter;
import java.io.*;
import java.nio.channels.*;
import java.nio.charset.Charset;
import java.nio.*;
import java.util.*;
import java.util.regex.*;

import tokenizer.NFA;
import tokenizer.Token;
import tokenizer.Tokenizer;

/**
 * 
 * Currently performs find and replace operations on text files. Needs to be
 * expanded to walk the AST tree
 *
 */
public class Evaluator {
	
	/**
     * Finds all strings in the given file that match the given regex, and
     * returns them in a StringList
     */
    public static StringList find(String regex, String filename){
		Tokenizer d = new Tokenizer("id", regex, filename);
		d.generateTokens();

		return StringList.toStringList(d.getTokens());
	}
	
	/**
     * Replaces all strings in the given file1 that match the given regex with
     * the replacement string and prints the final output to file2. Returns true
     * if matches were found.
     */
    public static boolean replace(String regex, String replacement,
        String file1, String file2) throws IOException{

		String[] matches = find(regex, file1).strings();

		if (matches.length < 1) {
			return false;
		}

		String in = "";
		regex = regex.substring(1,regex.length()-1);
		FileInputStream stream = new FileInputStream(new File(file1));
		  try {
		    FileChannel fc = stream.getChannel();
		    MappedByteBuffer mbb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
		    in = Charset.defaultCharset().decode(mbb).toString();
		  }
		  finally {
		    stream.close();
		  }
		  
		for (int i=0; i < matches.length; i++){
			in = in.replace(matches[i], replacement);
		}
		
		FileWriter fstream = new FileWriter(file2);
		BufferedWriter out = new BufferedWriter(fstream);
		out.write(in);
		out.close();
        return true;
	}
	
	/**
     * Will recursively replace all strings in the given file1 that match the
     * given regex with the replacement string and print the final output to
     * file2.
     */
    public static void recursivereplace(String regex, String replacement,
        String file1, String file2) throws IOException{
        
        NFA nfa = new NFA(regex);
        String sub = replacement;
        
        while (sub.length() > 0) {
            if (nfa.testString(nfa.thisNFA, sub)) {
                // throw some kind of error - the recursion is infinite
                System.out.println("Error: infinite loop inevitable at:\n" +
                    "recursivereplace '" + regex + "' with \"" + replacement +
                    "\" in " + file1 + " >! " + file2);
                return;
            }
            sub = sub.substring(0, sub.length()-1);
        }

		// Now Recursively replace until no more changes can be made

	}
}
