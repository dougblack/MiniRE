package interpreter;
import java.io.*;
import java.nio.channels.*;
import java.nio.charset.Charset;
import java.nio.*;
import java.util.*;
import java.util.regex.*;

import tokenizer.Token;
import tokenizer.Tokenizer;


public class Evaluator {

	public static String getStringLength(String string){
		
		return string.length()+"";
	}
	
	public static String inters(String a, String b){
		
		return inters(Arrays.asList(a.split(" ")), Arrays.asList(b.split(" ")));
	}
	
	public static String union(String a, String b){
		
		return union(Arrays.asList(a.split(" ")), Arrays.asList(b.split(" ")));
	}
	
	public static String inters(List<String> list1, List<String> list2){
		
		List<String> list = new ArrayList<String>();

        for (String t : list1) {
            if(list2.contains(t)) {
                list.add(t);
            }
        }

        return list.toString();
        
	}
	
	public static String union(List<String> list1, List<String> list2){
		
		  Set<String> set = new HashSet<String>();

	        set.addAll(list1);
	        set.addAll(list2);

	        ArrayList<String> list = new ArrayList<String>(set);
	        return list.toString();
	        
	}
	
	public static String maxfreqstring(String string){
		return maxfreqstring(string.split(" "));
	}
	
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
	
	
	public static String replace(String regex, String with, String filename1, String filename2) throws IOException{
		
		
		String matched = "";
		String i_to_replace[] = find(regex, filename1).split(" ");
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
		
		return in;
	}
	
	public static String print(String string){
		
		System.out.println(string);
		return string;
	}
	
}
