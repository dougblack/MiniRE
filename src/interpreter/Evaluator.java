package interpreter;
import java.io.*;
import java.nio.channels.*;
import java.nio.charset.Charset;
import java.nio.*;
import java.util.*;
import java.util.regex.*;

import tokenizer.Token;
import tokenizer.Tokenizer;

/**
 * 
 * carries out final string operations on matched tokens
 *
 */
public class Evaluator {

	/**
	 * evaluator for #
	 * @param string
	 * @return 
	 */
	public static String getStringLength(String string){
		
		return string.length()+"";
	}
	
	/**
	 * string to string list adaptor (space-separation=set)
	 * @param a
	 * @param b
	 * @return 
	 */
	public static String inters(String a, String b){
		
		return inters(Arrays.asList(a.split(" ")), Arrays.asList(b.split(" ")));
	}
	
	/**
	 * string to string list adaptor
	 * @param a
	 * @param b
	 * @return
	 */
	public static String union(String a, String b){
		
		return union(Arrays.asList(a.split(" ")), Arrays.asList(b.split(" ")));
	}
	
	/**
	 * evaluator intersection of string lists
	 * @param list1
	 * @param list2
	 * @return
	 */
	public static String inters(List<String> list1, List<String> list2){
		
		List<String> list = new ArrayList<String>();

        for (String t : list1) {
            if(list2.contains(t)) {
                list.add(t);
            }
        }

        return list.toString();
        
	}
	
	/**
	 * evaluator for union of string lists
	 * @param list1
	 * @param list2
	 * @return
	 */
	public static String union(List<String> list1, List<String> list2){
		
		  Set<String> set = new HashSet<String>();

	        set.addAll(list1);
	        set.addAll(list2);

	        ArrayList<String> list = new ArrayList<String>(set);
	        return list.toString();
	        
	}
	
	/**
	 * string to string list adaptor
	 * @param string
	 * @return
	 */
	public static String maxfreqstring(String string){
		return maxfreqstring(string.split(" "));
	}
	
	/**
	 * evaluator for maximum frequency of a string in a list
	 * @param string
	 * @return
	 */
	public static String maxfreqstring(String string[]){
		
		String freqstring="";
		int freq = 0;
		HashMap<String,Integer> map = new HashMap<String,Integer>();
		for (int x=0; x<string.length; x++)
		{
			if(map.containsKey(string[x])){
				int this_freq = (map.get(string[x])).intValue()+1;
				map.put(string[x], this_freq);
				if(freq<this_freq && this_freq>1){
					freq = this_freq;
					freqstring = string[x];
				}
			}
			else{
				map.put(string[x], 1);
			}
		}
		
		return freqstring;
	}
	
	
	/**
	 * evaluator for finding regexp in a file
	 * @param regex
	 * @param filename
	 * @return
	 */
	public static String find(String regex, String filename){
		
		if(regex.toCharArray()[0] == '\''){ // this is a regexp
			
			String matches="";
			regex = regex.substring(1,regex.length()-1);
			Tokenizer d = new Tokenizer("id", regex, filename);
			d.generateTokens();
	        ArrayList<Token> tokens = d.getTokens();
	        
	        for (int i = 0; i < tokens.size(); i++) {
	        	matches += tokens.get(i).getString()+" ";
	        }
			return matches;
		}
		return "";
	}
	
	/**
	 * evaluator for finding and replacing regexp matches in filename1 and writing to filename2
	 * @param regex
	 * @param with
	 * @param filename1
	 * @param filename2
	 * @return
	 * @throws IOException
	 */
	public static String replace(String regex, String with, String filename1, String filename2) throws IOException{
		
		String matched = "";
		String i_to_replace[] = find(regex, filename1).split(" ");
		if(i_to_replace.length==0)
			return -1+"";
		
		String in = "";
		regex = regex.substring(1,regex.length()-1);
		FileInputStream stream = new FileInputStream(new File(filename1));
		  try {
		    FileChannel fc = stream.getChannel();
		    MappedByteBuffer mbb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
		    in = Charset.defaultCharset().decode(mbb).toString();
		  }
		  finally {
		    stream.close();
		  }
		  
		for(int x=0; x<i_to_replace.length; x++){
			in = in.replace(i_to_replace[x], with);
		}
		
		FileWriter fstream = new FileWriter(filename2);
		BufferedWriter out = new BufferedWriter(fstream);
		out.write(in);
		out.close();
		return in;
	}
	
	
	/**
	 * evaluator for recursive replacing of regexps in output files
	 * @param regex
	 * @param with
	 * @param filename1
	 * @param filename2
	 * @return
	 * @throws IOException
	 */
	public static String recursivereplace(String regex, String with, String filename1, String filename2) throws IOException{
		
		String rep_stat = replace(regex, with, filename1, filename2);
		while(rep_stat.compareTo("-1")!=0){
			rep_stat = replace(regex,with,filename2,filename2);
		}
		return "0";
	}
	
	/**
	 * evaluator for printing a string
	 * @param string
	 * @return
	 */
	public static String print(String string){
		
		System.out.println(string);
		return string;
	}
	
}
