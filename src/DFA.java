import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.TreeSet;
import java.util.Set;
import java.util.Stack;

public class DFA {

    private SetAutomata sMata;
    private String regex;

	public DFA(NFA nfa) {
        sMata = new SetAutomata();

		if (nfa != null) {
            buildFromNFA(nfa);
            regex = nfa.thisRegex;
        }
	}

	public static void main(String args[]) {

		NFA nfa1 = new NFA("(a|b)*\\.(b|c)*");
        DFA dfa1 = new DFA(nfa1);
        //dfa1.printStructure();
        dfa1.testDFA("", ".", "ab.bc", "aa.ba", ".bb", "abaabab.cbcbc");
		
		NFA nfa2 = new NFA(".");
        DFA dfa2 = new DFA(nfa2);
        //dfa2.printStructure();
        dfa2.testDFA("", "ab.", "..", ".", "9", "S", ".bb", "abaabab.cbcbc");
		
		NFA nfa3 = new NFA("(ab)*");
        DFA dfa3 = new DFA(nfa3);
        //dfa3.printStructure();
        dfa3.testDFA("", "ab.bc", "aaba", "ababab", "aba");
		
		NFA nfa4 = new NFA("(dougblack)+");
        DFA dfa4 = new DFA(nfa4);
        //dfa4.printStructure();
        dfa4.testDFA("", "(dougblack)+", "(dougblack)", "dougblack", "dougblackdougblach", "dougblackdougblack");
		
		NFA nfa5 = new NFA("[0-9]*");
        DFA dfa5 = new DFA(nfa5);
        //dfa5.printStructure();
        dfa5.testDFA("", "0", "4", "9", "00009", "00", "11", "9999S9999");
		
		NFA nfa6 = new NFA("[^a]*");
        DFA dfa6 = new DFA(nfa6);
        //dfa6.printStructure();
        dfa6.testDFA("", "aa", "a", "b", "A", "asd a ", " a");
		
		NFA nfa7 = new NFA("");
        DFA dfa7 = new DFA(nfa7);
        //dfa7.printStructure();
        dfa7.testDFA("ab", "", " ", "azebra", "almanac");
		
		NFA nfa8 = new NFA("a(|z)");
        DFA dfa8 = new DFA(nfa8);
        //dfa8.printStructure();
        dfa8.testDFA("a", "az", "", " ", "z", "b4", "9");
	}

    public boolean testString(String string) {
        State currentState = sMata.startState;
        char c;
        boolean result = false;
        for (int i = 0; i < string.length(); i++) {
            c = string.charAt(i);

            if (!currentState.accepts(c)) {
                return false;
            }
            currentState = currentState.nextState(c);
        }
        if (sMata.acceptStates.contains(currentState)) {
            result = true;
        }
        return result;
    }
	
	public void testDFA(String ...strings) {
		System.out.println("Starting new DFA with regex: \"" + this.regex + "\""); // quotes are necessary to identify the empty string
		boolean matches = false;
		String result = "";
		for (int i = 0; i < strings.length; i++) {
			matches = testString(strings[i]);
			
			result = "Test: \"" + strings[i] + "\" was";
			if (!matches) {
				result += " not";
            }
			result += " accepted.";
			
			System.out.println(result);
		}
	}
	
    public void printStructure() {
        HashMap<HashSet<Node>, State> states = sMata.states;
        Iterator<HashSet<Node>> nodeSets = states.keySet().iterator();
        Set<Character> chars;
        HashSet<Node> nfaNodes;
    
        while (nodeSets.hasNext()) {
            nfaNodes = nodeSets.next();
            
            chars = states.get(nfaNodes).transitions.keySet();
            for (char c : chars) {
                System.out.println("State " + states.get(nfaNodes).stateId +
                    " ----(" + c + ")----> State " + states.get(nfaNodes).transitions.get(c).stateId);
            }
        }
        System.out.println("Start state: " + sMata.startState.stateId);
        System.out.print("Accept state(s): ");
        for (State state : sMata.acceptStates) {
            System.out.print(state.stateId + "...");
        }
        System.out.println("\nEND");
    }

	/**
	 * This method will apply the NFA -> DFA algorithm specified in the book.
	 * An Automata (which is a pretty terrible name, I admit) is just an NFA.
	 * It contains a start Node and an end Node. Nodes are connected by Paths.
	 * There are a few types of Paths (RangePath, AnythingPath, ConversePath,
	 * CharacterPath). I'm assuming the logic for converting each of these will
	 * have to be specifically laid out.
	 * 
	 * @param nfa The input NFA.
	 */
	public void buildFromNFA(NFA nfa) {
        HashSet<Node> currentState, nextState;
        HashMap<Node, HashSet<Node>> epsilonClosures;
        Stack<HashSet<Node>> stack;
        HashSet<HashSet<Node>> visitedStates, possibleAcceptStates;
        TreeSet<Character> alphabet;
        Iterator<Node> nextNfaNodes, nextDfaNodes;
        Node node;
        boolean acceptState = false;

		epsilonClosures = epsilonClosures(nfa);
        
        // Begin constructing the DFA by computing transitions for the set of
        // nodes that are in the start node's epsilon closure
        currentState = epsilonClosures.get(nfa.getStartNode());
        stack = new Stack<HashSet<Node>>();
        visitedStates = new HashSet<HashSet<Node>>();
        possibleAcceptStates = new HashSet<HashSet<Node>>();

        stack.push(currentState);
        visitedStates.add(currentState);
        sMata = new SetAutomata(currentState);

        // Get all characters in the NFA's alphabet
        alphabet = nfa.getAlphabet();

        while(!stack.empty()) {
            currentState = stack.pop();

            for (char character : alphabet) {
                nextState = new HashSet<Node>();

                // Determine where the nodes in currentState would transition to
                // in the NFA
                nextNfaNodes = computeTransitions(currentState, character).iterator();
                
                while (nextNfaNodes.hasNext()) {
                    // Get the nodes in the current node's epsilon closure; the
                    // epsilon closure is a state in the DFA
                    nextDfaNodes = epsilonClosures.get(nextNfaNodes.next()).iterator();

                    while (nextDfaNodes.hasNext()) {
                        node = nextDfaNodes.next();
                        nextState.add(node);
                        if (node.equals(nfa.getAcceptingNode())) {
                            acceptState = true;
                        }
                    }
                }
                if (nextState.size() < 1) {
                    continue;
                }
                if (!visitedStates.contains(nextState)) {
                    stack.push(nextState);
                    visitedStates.add(nextState);
                }
                sMata.addTransition(currentState, character, nextState);
                if (acceptState) {
                    sMata.setAccepting(nextState);
                    acceptState = false;
                }
            }
        }
        // Ensure empty string is accepted
        if (epsilonClosures.get(nfa.getStartNode()).contains(nfa.getAcceptingNode())) {
            sMata.setAccepting(epsilonClosures.get(nfa.getStartNode()));
        }
	}

    // Returns a mapping of nodes to the epsilon set they belong to
    private HashMap<Node, HashSet<Node>> epsilonClosures(NFA nfa) {
        ArrayList<Node> oneEpsilonClosure;
        HashSet<Node> epsilonClosureSet;
        HashMap<Node, HashSet<Node>> epsilonClosures;

        // Determine the epsilon closures of all nodes in the nfa. The sets of
        // nodes in each epsilon closure will be combined later to form nodes in
        // the DFA
        epsilonClosures = new HashMap<Node, HashSet<Node>>();

        for (Node srcNode : nfa.getNodes()) {
            oneEpsilonClosure = nfa.traverseNullPaths(new ArrayList<Node>(), srcNode);

            // The epsilon closure is an ArrayList right now; it's more useful
            // to me as a set
            epsilonClosureSet = new HashSet<Node>();

            for (Node epsNode : oneEpsilonClosure) {
                epsilonClosureSet.add(epsNode);
            }
            epsilonClosures.put(srcNode, epsilonClosureSet);
        }
        return epsilonClosures;
    }

    // Computes the set of all nodes the nodes in epsilon set inNodes could
    // transition to on the given char
    private HashSet<Node> computeTransitions(HashSet<Node> inNodes, char c) {
        HashSet<Node> outNodes = new HashSet<Node>();
        Iterator<Node> listOfInNodes = inNodes.iterator();
        Iterator<Node> nextNodes;

        while (listOfInNodes.hasNext()) {
            // Get all nodes a node can transition to on the given char
            nextNodes = listOfInNodes.next().getNextNodesFor(c).iterator();
            // Each such node should be returned
            while (nextNodes.hasNext()) {
                outNodes.add(nextNodes.next());
            }
        }
        return outNodes;
    }
}

// Converts sets of nodes from an NFA into states in a DFA, storing the states
// in a Hashmap with the set of nodes as the key.
class SetAutomata {

    State startState;
    HashSet<State> acceptStates;
    HashMap<HashSet<Node>, State> states; // identifies the States by the set of NFA Nodes they represent

    public SetAutomata() {
        acceptStates = new HashSet<State>();
        states = new HashMap<HashSet<Node>, State>();
    }

    // Creates a new automata with a single State representing the given set of
    // nodes; does not assume the state is accepting
    public SetAutomata(HashSet<Node> setOfNodes) {
        startState = new State(setOfNodes);
        acceptStates = new HashSet<State>();
        states = new HashMap<HashSet<Node>, State>();

        states.put(setOfNodes, startState);
    }

    // Creates new states for the given sets of nodes if needed, or finds the
    // States that represent them, then adds a transition to the State for src
    // to the State for dst on the given char
    public void addTransition(HashSet<Node> src, char c, HashSet<Node> dst) {
        State currentState = findState(src);
        State nextState = findState(dst);

        currentState.addTransition(c, nextState);   

        if (startState == null) {
            startState = currentState;
        }
    }

    // Returns either the State that represents the given set of nodes or a new
    // State if the set is new to this automata.
    public State findState(HashSet<Node> set) {
        State state;

        if (states.containsKey(set)) {
            return states.get(set);
        }
        states.put(set, state = new State(set));
        return state;
    }

    // Sets the State that represents the given set of nodes as accepting and
    // adds it to the list of accept states
    public void setAccepting(HashSet<Node> nodeSet) {
        State accepting = findState(nodeSet);
        acceptStates.add(accepting);
    }
}

// Represents nodes from an NFA; has a unique int id, a unique set of nodes it
// represents, and a map of input characters to next states
class State {
    static int _stateId = 0;
    int stateId = _stateId++;
    HashMap<Character, State> transitions;
    HashSet<Node> nodes;

    // Constructs a State to represent the given set of nodes in a DFA
    public State(HashSet<Node> setOfNodes) {
        transitions = new HashMap<Character, State>();
        nodes = setOfNodes;
    }

    // Stores a transition from this State to the given state on the given
    // character
    public void addTransition(char c, State nextState) {
        transitions.put(c, nextState);
    }

    // Returns whether the given node is represented by this State
    public boolean contains(Node node) {
        return nodes.contains(node);
    }

    // Returns whether the given character is a valid transition from this State
    public boolean accepts(char c) {
        return transitions.containsKey(c);
    }

    // If this State accepts the given character, it returns the next State in
    // the transition; otherwise it returns null
    public State nextState(char c) {
        if (transitions.containsKey(c)) {
            return transitions.get(c);
        }
        return null;
    }
}


