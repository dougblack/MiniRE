import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.Stack;

public class NFA {

	public static void main(String args[]) {

		String regex1 = "(a|b)*\\.(b|c)*";
		String regex2 = ".";
		String regex3 = "a*";

		Automata regex1Automata = regexToAutomata(regex1);
		regex1Automata.outNode.setEnd();
		System.out.println("1 match :" + testString(regex1Automata, "abababaaab"));
		System.out.println("1 match :" + testString(regex1Automata, "ababaajklbccb"));
		System.out.println("============");
		Automata regex2Automata = regexToAutomata(regex2);
		regex2Automata.outNode.setEnd();
		System.out.println("2 match :" + testString(regex2Automata, "a"));
		System.out.println("============");
		Automata regex3Automata = regexToAutomata(regex3);
		regex3Automata.outNode.setEnd();
		System.out.println("3 match :" + testString(regex3Automata, "aaaaaa"));
		System.out.println("3 match :" + testString(regex3Automata, "aba"));
		System.out.println("============");
	}

	public static Automata regexToAutomata(String regex) {
		return regexToAutomataHelper(regex, 0, regex.length());
	}

	/**
	 * This method constructs an NFA for the given regex section.
	 * @param regex
	 * @param start
	 * @param end
	 * @return
	 */
	public static Automata regexToAutomataHelper(String regex, int start, int end) {

		boolean escaped = false;
		Automata root = new Automata();
		root.inNode.connect(root.outNode);
		Stack<Automata> automataStack = new Stack<Automata>();
		automataStack.push(root);

		for (int i = start; i < end; i++) {

			char currentChar = regex.charAt(i);

			if (currentChar == '\\' && !escaped) { // Escape character. Set escaped flag for next character. escaped = true;
				continue;
			}
			if (escaped) { // Character is escaped. So add a CharacterPath accepting just that character.
				Automata next = new Automata();
				next.setInteriorPath(new CharacterPath("" + currentChar));
				Automata last = automataStack.peek();
				last.connectToAutomata(next);
			} else if (currentChar == '.') { // Wildcard character. Add AnythingPath.
				System.out.println("Wildcard. Adding anything path.");
				Automata next = new Automata();
				next.setInteriorPath(new AnythingPath());
				Automata last = automataStack.peek();
				last.connectToAutomata(next);
				automataStack.push(next);
			} 
			/*	 Star character. Connect beginning and end of last automata to make two-way loop.*/
			else if (currentChar == '*' && automataStack.size() > 0) { 
				System.out.println("Star. Connect beginning and end of last automata to make two-way loop.");
				Automata last = automataStack.peek();
				last.inNode.connect(last.outNode);
				last.outNode.connect(last.inNode);
			} 
			 /* Repeat character. Connect end to beginning to make loop */
			else if (currentChar == '+') {
				Automata last = automataStack.peek();
				last.outNode.connect(last.inNode);
			} 
			/* Or character. Separate options and make null edges to both. */
			else if (currentChar == '|') { 
				Automata optionA = new Automata(automataStack.firstElement().inNode, automataStack.peek().outNode);
				Automata optionB = regexToAutomataHelper(regex, i + 1, end);

				automataStack.clear();

				Automata automata = new Automata();

				automata.inNode.connect(optionB.inNode);
				automata.inNode.connect(optionA.inNode);
				optionB.outNode.connect(automata.outNode);
				optionA.outNode.connect(automata.outNode);

				automataStack.push(automata);
				break;

			} 
			/* Parenthesis. Create Automata for inside. So recurse over inside regex.*/
			else if (currentChar == '(') { 
				int closingIndex = indexOfClosing(regex, i + 1, end, '(');
				/* if (closingIndex == -1) THROW ERROR */
				System.out.println("Parenthesis. Building automata for " + regex.substring(i, closingIndex + 1));
				Automata insideAutomata = regexToAutomataHelper(regex, i + 1, closingIndex);
				Automata last = automataStack.peek();
				last.connectToAutomata(insideAutomata);
				automataStack.push(insideAutomata);
				i = closingIndex;
			} else if (currentChar == '[') { // Character class begins.
				int closingIndex = indexOfClosing(regex, i + 1, end, '[');
				Automata rangeAutomata = regexToRangeAutomata(regex, i + 1, closingIndex);
				Automata last = automataStack.peek();
				last.connectToAutomata(rangeAutomata);
				automataStack.push(last);
				i = closingIndex;
			} else { // Just a random character. Accept it.
				System.out.println("Random character. Adding CharacterPath for: " + currentChar);
				Automata next = new Automata();
				next.setInteriorPath(new CharacterPath("" + currentChar));
				Automata last = automataStack.peek();
				last.connectToAutomata(next);
				automataStack.push(next);
			}

			escaped = false;

		}
		return new Automata(automataStack.firstElement().inNode, automataStack.peek().outNode);
	}

	/**
	 * Just finds a matching closing parenthesis/bracket.
	 * @param regex
	 * @param start
	 * @param end
	 * @param openBrace
	 * @return
	 */
	public static int indexOfClosing(String regex, int start, int end, char openBrace) {
		char closingBrace;
		if (openBrace == '(')
			closingBrace = ')';
		else
			closingBrace = ']';
		for (int i = start; i < end; i++) {
			if (regex.charAt(i) == closingBrace)
				return i;
		}
		return -1;
	}

	
	/**
	 * This method DOES NOT WORK YET. Once finished, it will process a character class, including a range.
	 * Yes, it's poorly named.
	 * @param regex
	 * @param start
	 * @param end
	 * @return
	 */
	public static Automata regexToRangeAutomata(String regex, int start, int end) {
		String target = regex.substring(start, end);
		System.out.println("Processing brackets: " + target);
		int dashes = 0;
		for (int i = 0; i < target.length(); i++) {
			if (target.charAt(i) == '-') {
				char startChar = target.charAt(i - 1);
				char endChar = target.charAt(i + 1);
				dashes++;
				System.out.println("Dash found.\nFirst char: " + startChar + ". End char: " + endChar);

				break;
			}
		}
		return null;
	}
	
	/**
	 * This method tests if the NFA accepts the given string. It does the extra
	 * credit NFA traversal.
	 * @param automata
	 * @param string
	 * @return
	 */
	public static boolean testString(Automata automata, String string) {

		ArrayList<Node> occupiedNodeSet = new ArrayList<Node>();
		Node start = automata.inNode;
		Node end = automata.outNode;
		occupiedNodeSet = traverseNullPaths(occupiedNodeSet, start);

		for (char currentChar : string.toCharArray()) {
			occupiedNodeSet = step(occupiedNodeSet, currentChar);
		}

		return occupiedNodeSet.contains(end);
	}

	/**
	 * This method "steps" through the automata by following the rules of NFA.
	 * (i.e. it follows blank paths and simulates "copying" itself)
	 * @param occupiedNodeSet
	 * @param c
	 * @return
	 */
	public static ArrayList<Node> step(ArrayList<Node> occupiedNodeSet, char c) {
		
		/* TODO - count traversals and "clones" */
		
		ArrayList<Node> nextOccupied = new ArrayList<Node>();

		for (Node node : occupiedNodeSet) {
			for (Path path : node.paths) {
				if (path.matches(c)) {
					
					nextOccupied.add(path.destination);
				}
			}
		}

		ArrayList<Node> blankSpan = new ArrayList<Node>();

		for (Node node : nextOccupied) {
			for (Path path : node.paths) {
				if (path.nullPath) {
					blankSpan = traverseNullPaths(blankSpan, path.destination);
				}
			}
		}

		nextOccupied.addAll(blankSpan);

		return nextOccupied;
	}

	/**
	 * This method traverses (with a depth-first-search) all the null
	 *  (episilon) paths and returns the resultant set of nodes.
	 * @param blankSpan
	 * @param node
	 * @return
	 */
	public static ArrayList<Node> traverseNullPaths(ArrayList<Node> blankSpan, Node node) {

		Stack<Node> nodes = new Stack<Node>();
		nodes.push(node);

		while (!nodes.isEmpty()) {
			Node currNode = nodes.pop();
			blankSpan.add(currNode);

			for (Path path : currNode.paths) {
				if (path.nullPath && !blankSpan.contains(path.destination)) {
					nodes.push(path.destination);
				}
			}
		}
		return blankSpan;
	}

}

/**
 * This represents a given state. It contains a list of outgoing
 * paths and a nodeId. Which is not really used...yet.
 * @author Doug
 *
 */
class Node {

	static int _nodeId = 0;
	int nodeId;
	ArrayList<Path> paths;
	boolean end = false;
	boolean visited = false;

	public Node() {
		this.paths = new ArrayList<Path>();
		this.nodeId = _nodeId++;
	}

	public void addPath(Path path) {
		paths.add(path);
	}

	public void connect(Node destinationNode) {
		Path connector = new Path();
		connector.destination = destinationNode;
		this.addPath(connector);
	}

	public void setEnd() {
		end = true;
	}
}

/**
 * The parent Path object. It is initalized to a null path.
 * i.e. just like the "episilon" paths from the book.
 * @author Doug
 *
 */
class Path {
	Node destination;
	boolean nullPath;

	public Path() {
		this.nullPath = true;
	}

	// True if path matches input.
	public boolean matches(char c) {
		return false;
	}

	public String toString() {
		return "Parent Path";
	}

}

/**
 * This path accepts any characters in the given input string.
 * i.e. if constructed with string: "b", it will accept "b".
 * @author Doug
 *
 */
class CharacterPath extends Path {
	String accepted;

	public CharacterPath(String accepted) {
		this.accepted = accepted;
		this.nullPath = false;
	}

	public boolean matches(char c) {
		return accepted.contains(Character.toString(c));
	}

	public String toString() {
		return "CharacterPath accepting: " + accepted;
	}
}

/**
 * This path accetps the converse of a given string.
 * i.e. if constructed with string: "a", it will accept
 * every other string.
 * @author Doug
 *
 */
class ConversePath extends Path {
	String notAccepted;

	public ConversePath(String notAccepted) {
		this.notAccepted = notAccepted;
		this.nullPath = false;
	}

	public boolean matches(char c) {
		return !notAccepted.contains(Character.toString(c));
	}

	public String toString() {
		return "ConversePath accepting not in: " + notAccepted;
	}
}

/**
 * This path accepts anything. 
 * @author Doug
 *
 */
class AnythingPath extends Path {
	public AnythingPath() {
		this.nullPath = false;
	}

	public boolean matches(char c) {
		return true;
	}

	public String toString() {
		return "ConversePath accepting anything";
	}
}

/**
 * This path accepts a range of values between two chars.
 * @author Doug
 *
 */
class RangePath extends Path {
	char start, end;

	public RangePath(char start, char end) {
		this.nullPath = false;
	}

	public boolean matches(char c) {
		return (c >= start && c <= end);
	}

	public String toString() {
		return "RangePath accepting: " + start + "-" + end;
	}
}

/**
 * This class is a section of a finite state automata. It's basically
 * a wrapper for a series of nodes and paths. It just houses and start
 * and end of a given section. All it stores is a start node and an end node. 
 * When construction, paths and other automata are added between it's inNode
 * and outNode.
 * @author Doug
 *
 */
class Automata {
	Node inNode;
	Node outNode;

	public Automata() {
		this.inNode = new Node();
		this.outNode = new Node();
	}

	public Automata(Node inNode, Node outNode) {
		this.inNode = inNode;
		this.outNode = outNode;
	}

	/**
	 * Adds a path between in and out nodes.
	 * @param path
	 */
	public void setInteriorPath(Path path) {
		path.destination = this.outNode;
		this.inNode.addPath(path);
	}

	
	/**
	 * Connects this automata to the given one by simply adding a null path
	 * between it's outNode and the given Automata's inNode.
	 * @param outAutomata
	 */
	public void connectToAutomata(Automata outAutomata) {
		this.outNode.connect(outAutomata.inNode);
	}

}
