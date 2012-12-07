package interpreter;
import java.util.*;


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
}
