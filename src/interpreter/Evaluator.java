package interpreter;
import tokenizer.NFA;
import tokenizer.Token;
import tokenizer.Tokenizer;

import java.io.*;
import java.nio.channels.*;
import java.nio.charset.Charset;
import java.nio.*;
import java.util.*;

/**
 * 
 * Evaluates an AST Tree
 *
 */
public class Evaluator {

    HashMap<String, Object> symbolTable;

    public Evaluator() {
        symbolTable = new HashMap<String, Object>();
    }

    /**
     * Evaluates the AST.
     * @param head the head
     * @return the result of the evaluation
     */
    public Object eval(SyntaxTreeNode head) {

        String nodeType = head.nodeType;
        ArrayList<SyntaxTreeNode> children = head.children;

        if (nodeType == null) {
            return head.value;
        }
        if (nodeType.equals("MiniRE-program")) {
            eval(children.get(1));
        } else if (nodeType.equals("STATEMENT-LIST")) {
            eval(children.get(0));
            eval(children.get(1));
        } else if (nodeType.equals("STATEMENT-LIST-TAIL")) {
            for (SyntaxTreeNode child : children) {
                eval(child);
            }
        } else if (nodeType.equals("STATEMENT")) {
            if (children.get(0).id.equals("$REPLACE")) {
                String regex = children.get(1).value;
                String asciiStr = children.get(3).value;

                /* Remove the single quotes from regex and double from asciiStr */
                regex = regex.substring(1,regex.length()-1);
                asciiStr = asciiStr.substring(1,asciiStr.length()-1);

                ArrayList<String> fileNames = (ArrayList<String>) eval(children.get(5));
                return replace(regex, asciiStr, fileNames.get(0), fileNames.get(1));
            } else if (children.get(0).id.equals("$RECREP")) {
                String regex = children.get(1).value;
                String asciiStr = children.get(3).value;

                /* Remove the single quotes from regex and double from asciiStr */
                regex = regex.substring(1,regex.length()-1);
                asciiStr = asciiStr.substring(1,asciiStr.length()-1);

                ArrayList<String> fileNames = (ArrayList<String>) eval(children.get(5));
                recursivereplace(regex, asciiStr, fileNames.get(0), fileNames.get(1));
            } else if (children.get(0).id.equals("$ID")) {
                String idName = children.get(0).value;
                Object idValue = eval(children.get(2));
                symbolTable.put(idName, idValue);
            } else if (children.get(0).id.equals("$PRINT")) {
                SyntaxTreeNode exp_list = children.get(2);
                printExp(eval(exp_list.children.get(0)));
                SyntaxTreeNode exp_list_tail = exp_list.children.get(1);
                while (exp_list_tail.children.get(0).nodeType == null) {
                    printExp(eval(exp_list_tail.children.get(1)));
                    exp_list_tail = exp_list_tail.children.get(2);
                }
            }
        } else if (nodeType.equals("STATEMENT-RIGHTHAND")) {
            if (children.get(0).nodeType != null && children.get(0).nodeType.equals("EXP")) {
                return eval(children.get(0));
            } else if (children.get(0).id.equals("$HASH")) {
                return ((StringList) eval(children.get(1))).length();
            } else if (children.get(0).id.equals("$MAXFREQ")) {
                return ((StringList) symbolTable.get(children.get(2).value)).maxfreqstring();
            }
        } else if (nodeType.equals("FILE-NAMES")) {
            ArrayList<String> fileNames = new ArrayList<String>();
            fileNames.add((String) eval(children.get(0)));
            fileNames.add((String) eval(children.get(2)));
            return fileNames;
        } else if (nodeType.equals("SOURCE-FILE")) {
            String filename = (String) children.get(0).value;

            // Remove "" surrounding filename
            filename = filename.substring(1, filename.length()-1);
            return filename;
        } else if (nodeType.equals("DESTINATION-FILE")) {
            String filename = (String) children.get(0).value;

            // Remove "" surrounding filename
            filename = filename.substring(1, filename.length()-1);
            return filename;
        } else if (nodeType.equals("EXP-LIST-TAIL")) {
            if (children.get(0).nodeType.equals("EPSILON")) {
                return null;
            } else {
                return StringList.union((StringList) eval(children.get(1)), (StringList) eval(children.get(2)));
            }
        } else if (nodeType.equals("EXP")) {
            if (children.get(0).id != null && children.get(0).id.equals("$ID")) {
                if (symbolTable.containsKey(children.get(0).value)) {
                    return symbolTable.get(children.get(0).value);
                } else {
                    return children.get(0).value;
                }
            } else if (children.get(0).id != null && children.get(0).id.equals("$LPAREN")) {
                return eval(children.get(2));
            } else if (children.get(0).nodeType != null && children.get(0).nodeType.equals("TERM")) {
                if (children.get(1).children.get(0).nodeType.equals("EPSILON")) {
                    return eval(children.get(0));
                } else {
                    StringList result = (StringList) eval(children.get(0));
                    SyntaxTreeNode exp_list = children.get(1);
                    while (!exp_list.children.get(0).nodeType.equals("EPSILON")) {
                        StringList list = (StringList) eval(exp_list.children.get(1));
                        if (exp_list.children.get(0).children.get(0).id.equals("$DIFF")) {
                            result = StringList.diff(result, list);
                        } else if (exp_list.children.get(0).children.get(0).id.equals("$UNION")) {
                            result = StringList.union(result, list);
                        } else if (exp_list.children.get(0).children.get(0).id.equals("$INTERS")) {
                            result = StringList.inters(result, list);
                        }
                        exp_list = exp_list.children.get(2);
                    }
                    return result;
                }
            }
        } else if (nodeType.equals("TERM")) {
            String fileName = (String) eval(children.get(3));
            String regex = (String) eval(children.get(1));
            
            // Remove the single quotes from regex
            regex = regex.substring(1,regex.length()-1);

            return find(regex, fileName);
        } else if (nodeType.equals("FILE-NAME")) {
            String filename = (String) children.get(0).value;

            // Remove "" surrounding filename
            filename = filename.substring(1, filename.length()-1);
            return filename;
        }
        return null;
    }

	/**
     * Finds all strings in the given file that match the given regex, and
     * returns them in a StringList
     * 
     * ASSUMES REGEX HAS BEEN STRIPPED OF SURROUNDING QUOTES
     *
     * @param regex a Regular expression, as a string
     * @param filename a file to scan for strings matching regex
     * @param return a StringList containing all matching strings and their
     *          metadata
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
     * 
     * ASSUMES REGEX AND REPLACEMENT STRING HAVE BEEN STRIPPED OF SURROUNDING
     * QUOTES
     *
     * @param regex a Regular expression, as a string
     * @param replacement a string to replace everything that matches regex with
     * @param file1 a file to scan for strings matching regex
     * @param file2 a file identical to file1, but with the matches replaced
     * @return true if and only if strings matching regex were found in file1
     */
    public static boolean replace(String regex, String replacement,
        String file1, String file2) {

        String[] matches = find(regex, file1).strings();
		String fileText;

		if (matches.length < 1) {
			return false;
		}

        fileText = FileOperations.getFileText(file1);
		  
		for (int i=0; i < matches.length; i++){
			fileText = fileText.replace(matches[i], replacement);
		}
        return FileOperations.writeFile(fileText, file2);
	}
	
	/**
     * Will recursively replace all strings in the given file1 that match the
     * given regex with the replacement string and print the final output to
     * file2.
     * 
     * ASSUMES REGEX AND REPLACEMENT STRING HAVE BEEN STRIPPED OF SURROUNDING
     * QUOTES
     *
     * @param regex a Regular expression, as a string
     * @param replacement a string to recursively replace everything that
     *          matches regex with
     * @param file1 a file to scan for strings matching regex
     * @param file2 a file identical to file1, but with all matches replaced
     */
    public static void recursivereplace(String regex, String replacement,
        String filename1, String filename2) {
        
		// Recursively replace until no more changes can be made
        
        String tempFilename1 = FileOperations.createTempFile();
        Evaluator.replace(regex, replacement, filename1, filename2);

        while (Evaluator.replace(regex, replacement, filename2, tempFilename1)) {
            if (!Evaluator.replace(regex, replacement, tempFilename1, filename2)) {
                // Copy 1 to 2 and be done
                FileOperations.copy(tempFilename1, filename2);
                break;
            }
        }
	}

    /**
     * Determines expression type and prints out either
     * an Integer or StringList.
     * @param exp the expression
     */
    public void printExp(Object exp) {

        try {
            if (exp != null) {
                System.out.println(((StringList)exp).toString());
            } else {
                System.out.println("[]");
            }
        } catch (ClassCastException cce) {
            System.out.println((Integer) exp);
        }
    }
}
