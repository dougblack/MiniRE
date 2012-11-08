import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.Stack;

public class NFA {

    public static void main(String args[]) {

        String regex1 = "(a|b)*[0-9]";
        String regex2 = "a";
        String regex3 = "[a-zA-Z][0-9]";

        Automata regex1Automata = regexToAutomata(regex1);
        System.out.println("============");
        traverseAutomata(regex1Automata);
        Automata regex2Automata = regexToAutomata(regex2);
        System.out.println("============");
        traverseAutomata(regex2Automata);
        Automata regex3Automata = regexToAutomata(regex3);
        System.out.println("============");
        traverseAutomata(regex3Automata);
    }

    public static Automata regexToAutomata(String regex) {
        return regexToAutomataHelper(regex, 0, regex.length());
    }

    public static Automata regexToAutomataHelper(String regex, int start, int end) {

        boolean escaped = false;
        Automata root = new Automata();
        root.inNode.connect(root.outNode);
        Stack<Automata> automataStack = new Stack<Automata>();
        automataStack.push(root);


        for(int i = start; i < end; i++) {

            char currentChar = regex.charAt(i);

            if (currentChar == '\\' && !escaped) {
                escaped = true;
                continue;
            }
            if (escaped) {
                Automata next = new Automata();
                next.setIncomingPath(new CharacterPath("" + currentChar));
                Automata last = automataStack.peek();
                last.connectToAutomata(next);
            } else if (currentChar == '.') {
                Automata next = new Automata();
                next.setIncomingPath(new AnythingPath());
                Automata last = automataStack.peek();
                last.connectToAutomata(next);
            } else if (currentChar == '*') {
                Automata last = automataStack.peek();
                last.inNode.connect(last.outNode);
                last.outNode.connect(last.inNode);
            } else if (currentChar == '+') {
                Automata last = automataStack.peek();
                last.outNode.connect(last.inNode);
            } else if (currentChar == '|') {
                Automata optionA = automataStack.peek();
                Automata optionB = regexToAutomataHelper(regex, i+1, end);

                automataStack.clear();

                Automata automata = new Automata();

                automata.inNode.connect(optionB.inNode);
                automata.inNode.connect(optionA.inNode);
                optionB.outNode.connect(automata.outNode);
                optionA.outNode.connect(automata.inNode);

                automataStack.push(automata);
                break;

            } else if (currentChar == '(') {
                int closingIndex = indexOfClosing(regex, i+1, end, '(');
                /*if (closingIndex == -1) THROW ERROR */
                Automata insideAutomata = regexToAutomataHelper(regex, i+1, closingIndex);
                Automata last = automataStack.peek();
                last.connectToAutomata(insideAutomata);
                automataStack.push(last);
                i = closingIndex;
            } else if (currentChar == '[') {
                int closingIndex = indexOfClosing(regex, i+1, end, '[');    
                Automata rangeAutomata = regexToRangeAutomata(regex, i+1, closingIndex);
                Automata last = automataStack.peek();
                last.connectToAutomata(rangeAutomata);
                automataStack.push(last);
                i = closingIndex;
            } else {
                Automata next = new Automata();
                next.setIncomingPath(new CharacterPath("" + currentChar));
                Automata last = automataStack.peek();
                last.connectToAutomata(next);
                automataStack.push(next);
            }

            escaped = false;

        }

        return new Automata(automataStack.firstElement().inNode, automataStack.peek().outNode);

    }

    public static int indexOfClosing(String regex, int start, int end, char openBrace) {
        char closingBrace;
        if (openBrace == '(') closingBrace = ')';
        else closingBrace = ']';
        for (int i = start; i < end; i++) {
            if (regex.charAt(i) == closingBrace)
                return i;
        }
        return -1;
    }


    //Automata rangeAutomata = regexToRangeAutomata(regex, i+1, closingIndex);
    public static Automata regexToRangeAutomata(String regex, int start, int end) {
        regex.substring(start, end);
        /* TODO */
        return null;
    }
    public static void traverseAutomata(Automata automata) {
        Node start = automata.inNode;
        Stack<Node> destinationNodes = new Stack<Node>();
        while(true) {
            System.out.println("Node " + start.nodeId + " paths:");
            int i = 1;
            for (Path p : start.paths) {
                System.out.println("Path " + i + ": connects to " + p.destination.nodeId + ". Type: "+p.toString());
                destinationNodes.push(p.destination);
            }
            try {
                start = destinationNodes.pop();
            } catch (EmptyStackException ese) {
                break;
            }
        }
    }

}

class Node {

    static int _nodeId = 0;
    int nodeId;
    ArrayList<Path> paths;

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
}

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

class AnythingPath extends Path{
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

class RangePath extends Path {
    char start, end;

    public RangePath (char start, char end) {
        this.nullPath = false;
    }

    public boolean matches (char c) {
        return (c >= start && c <= end);
    }

    public String toString() {
        return "RangePath accepting: " + start + "-" + end;
    }
}

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

    public void setIncomingPath(Path path) {
        path.destination = this.outNode;
        this.inNode.addPath(path);
    }

    public void connectToAutomata(Automata outAutomata) {
        this.outNode.connect(outAutomata.inNode);
    }

}
