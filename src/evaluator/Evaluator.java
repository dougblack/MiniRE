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
        
		/*String intersection = "";
		for(int x=0; x<a.length; x++)
			for(int y=0; y<b.length; y++){
				
				if (a[x].compareTo(b[y]) == 0)
					intersection+=b[y];
			}
		
		return intersection;*/	
	}
	
	public static String union(List<String> list1, List<String> list2){
		
		  Set<String> set = new HashSet<String>();

	        set.addAll(list1);
	        set.addAll(list2);

	        ArrayList<String> list = new ArrayList<String>(set);
	        return list.toString();
	        
		/*String union = "";
		String intersection = inters(a,b);
		String i_arr[] = intersection.split(" ");
		for (int x=0; x<a.length; x++){
			if(Arrays.asList(i_arr).contains(a[x]))
				continue;
			union+=a[x];
		}*/
		
		
	}
}
