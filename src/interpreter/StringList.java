package interpreter;
import tokenizer.Token;

import java.util.ArrayList;
import java.util.TreeSet;
import java.util.HashMap;
import java.util.Iterator;

/**
 * A List of strings and their metadata - the files and locations in those files
 * where they were found.
 */
public class StringList {

    /**
     * Converts the given arraylist of tokens to a StringList
     */
    public static StringList toStringList(ArrayList<Token> aList) {
        StringList sl = new StringList();

        if (aList != null) {
            while (aList.size() > 0) {
                sl.add(aList.remove(0));
            }
        }
        return sl;
    }

    /**
     * Performs the union operation on the 2 given StringLists
     */
    public static StringList union(StringList a, StringList b) {
        if (b == null)
            return a;

        String[] bStrings = b.strings();

        for (int i=0; i < bStrings.length; i++) {
            a.put(bStrings[i], b.get(bStrings[i]));
        }
        return a;
    }

    /**
     * Performs the inters operation on the 2 given StringLists
     */
    public static StringList inters(StringList a, StringList b) {
        StringList sl = new StringList();
        String[] aStrings = a.strings();
            
        for (int i=0; i < aStrings.length; i++) {
            if (b.contains(aStrings[i])) {
                sl.put(aStrings[i], a.get(aStrings[i]));
                sl.put(aStrings[i], b.get(aStrings[i]));
            }
        }
        System.out.println(sl.length());
        return sl;
    }

    /**
     * Performs the diff operation on the 2 given StringLists, as a diff b
     */
    public static StringList diff(StringList a, StringList b) {
        String[] bStrings = b.strings();
                
        for (int i=0; i < bStrings.length; i++) {
            if (a.contains(bStrings[i])) {
                a.remove(bStrings[i]);
            }
        }
        return a;
    }

    /**
     * Returns the number of strings in the given StringList
     */
    public static int length(StringList a) {
        return a.length();
    }

    /**
     * Returns the string that appears most often in the given StringList
     */
    public static String maxfreqstring(StringList a) {
        return a.maxfreqstring();
    }


    /**
     * Maps tokens to files to the locations of the tokens in the files.
     */
    private HashMap<String, HashMap<String, TreeSet<Location>>> list;

    /**
     * Constructs a new StringList to represent a string-match list
     */
    public StringList() {
        list = new HashMap<String, HashMap<String, TreeSet<Location>>>();
    }

    /**
     * Adds the string and metadata in the given token to this StringList
     */
    public void add(Token t) {
        String literal = t.getString();
        String file = t.getFile();
        Location index = new Location(t.getRow(), t.getStart(), t.getEnd());
        HashMap<String, TreeSet<Location>> fileList;
        TreeSet<Location> indices;

        if (literal == null || file == null || index == null) {
            return;
        }
        if (list.containsKey(literal)) {
            // The token's string has been found before
            fileList = list.get(literal);
            if (fileList.containsKey(file)) {
                // The token's string has been found in this file before
                indices = fileList.get(file);
                indices.add(index);
                fileList.put(file, indices);
            } else {
                // The token's string has not been found in this file before
                indices = new TreeSet<Location>();
                indices.add(index);
                fileList.put(file, indices);
            }
        } else {
            // The token's string has not been found before
            indices = new TreeSet<Location>();
            indices.add(index);
            fileList = new HashMap<String, TreeSet<Location>>();
            fileList.put(file, indices);
        }
        list.put(literal, fileList);
    }

    /**
     * Adds the given string and its metadata in the hashmap to this StringList
     */
    public void put(String s, HashMap<String, TreeSet<Location>> hm) {
        HashMap<String, TreeSet<Location>> fileList;
        TreeSet<Location> oldIndices, newIndices;
        String[] files;

        if (s == null || hm == null || hm.size() < 1) {
            // do nothing
        } else {
            if (list.containsKey(s)) {
                files = hm.keySet().toArray(new String[0]);
                fileList = list.get(s);
                
                for (int i=0; i<files.length; i++) {
                    if (fileList.containsKey(files[i])) {
                        newIndices = fileList.get(files[i]);
                        oldIndices = hm.get(files[i]);

                        while (oldIndices.size() > 0) {
                            newIndices.add(oldIndices.pollFirst());
                        }
                        fileList.put(files[i], newIndices);
                    } else {
                        fileList.put(files[i], hm.get(files[i]));
                    }
                }
                list.put(s, fileList);
            } else {
                list.put(s, hm);
            }
        }
    }

    /**
     * Removes the given string and all its metadata from this StringList
     */
    public HashMap<String, TreeSet<Location>> remove(String s) {
        return list.remove(s);
    }

    /**
     * Returns true if and only if the given string appears in this StringList
     */
    public boolean contains(String s) {
        return list.containsKey(s);
    }

    /**
     * Returns the metadata for the given string from this StringList
     */
    public HashMap<String, TreeSet<Location>> get(String s) {
        return list.get(s);
    }

    /**
     * Returns the list of strings in this StringList
     */
    public String[] strings() {
        return list.keySet().toArray(new String[0]);
    }

    /**
     * Returns the number of strings in this StringList
     */
    public int length() {
        return list.size();
    }

    /**
     * Returns the most frequently occurring string in this StringList
     */
    public String maxfreqstring() {
        String maxString = "\"\"";
        int max = -1, count;

        if (list.size() > 0) {
            for (String string : list.keySet()) {
                count = 0;
                for (String file : list.get(string).keySet()) {
                    count += list.get(string).get(file).size();
                }
                if (count >= max) {
                    max = count;
                    maxString = "\"" + string + "\"";
                }
            }
        }
        return maxString;
    }

    /**
     * Overrides the generic toString method. Displays the string and all its
     * metadata
     */
    public String toString() {
        HashMap<String, TreeSet<Location>> files;
        Iterator<Location> it;
        String out = "";

        for (String string : list.keySet()) {
            out += "\"" + string;
            files = list.get(string);

            for (String file : files.keySet()) {
                out += "\" <'" + file + "'";
                it = files.get(file).iterator();

                while (it.hasNext()) {
                    out += "; " + it.next();
                }
                out += ">";
            }
            out += "\n";
        }
        return out;
    }

    public ArrayList<String> print() {
        ArrayList<String> strings = new ArrayList<java.lang.String>();
        for (String string : list.keySet()) {
            strings.add(string);
        }
        return strings;
    }
}


/**
 * Represents the line, start index, and end index of a string in a file; All 3
 * are 1-indexed.
 */
class Location implements Comparable<Location> {

    private int line, start, end;

    /**
     * Constructs a new Location; note it has no direct ties to any strings
     */
    public Location(int line, int start, int end) {
        this.line = line;
        this.start = start;
        this.end = end;
    }

    /**
     * Allows Locations to be sorted. Greater Locations occur later on in files
     */
    public int compareTo(Location that) {
        int comp;

        if (this.line == that.getLine()) {
            if (this.start == that.getStart()) {
                comp = this.end - that.getEnd();
            } else {
                comp = this.start - that.getStart();
            }
        } else {
            comp = this.line - that.getLine();
        }
        return comp;
    }

    /**
     * Overrides the generic toString method. Displays all information contained
     * in this Location object
     */
    public String toString() {
        return "Ln " + line + ", Col " + start + "-" + end;
    }

    /**
     * Returns this Location's line
     */
    public int getLine() {
        return line;
    }

    /**
     * Returns this Location's start index within its line
     */
    public int getStart() {
        return start;
    }

    /**
     * Returns this Location's end index
     */
    public int getEnd() {
        return end;
    }
}






