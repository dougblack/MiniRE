package interpreter;
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
		
		String matches="";
		Tokenizer d = new Tokenizer(filename, "/home/alazar/git/MiniRE/src/interpreter/test.txt");
		d.generateTokens();
        ArrayList<Token> tokens = d.getTokens();
        //
        for (int i = 0; i < tokens.size(); i++) {
            System.out.println(tokens.get(i).getId() + ": " +
                tokens.get(i).getString());
            matches+=tokens.get(i);
        }
		return matches;
	}
	
	
	public static String replace(String regex, String with, String filename1, String filename2){
		
		
		String matched = "";
		if(regex.toCharArray()[0] == '\''){ // this is a regexp
			
			regex = regex.substring(1,regex.length()-1);
			regex.toCharArray()[0] = ' ';
			regex.toCharArray()[regex.length()-1] = ' ';
			regex = regex.trim();
			
			Pattern pattern = Pattern.compile(regex);
			Matcher matcher = pattern.matcher(regex);
			while(matcher.find()){
				matched+=matcher.group(0);
			}
			if(matched.length()>0)
				return regex.replace(matched, with);
			return "";
		}
		
		return matched;
	}
	
	public static String print(String string){
		
		System.out.println(string);
		return string;
	}
	
}
