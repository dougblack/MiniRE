import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Treeset;
import java.util.Stack;

public class DFA {

    private Automata mata;

	public DFA(NFA nfa) {
        mata = new Automata();

		if (nfa != null) {
            buildFromNFA(nfa);
        }
	}

	public static void main(String args[]) {
		/*Automata nfa = new Automata();
		nfa.inNode = new Node();
		nfa.outNode = new Node();
		Path path1 = new Path();*/
		
		NFA nfa = new NFA("a+(b|c)");
        DFA dfa = new DFA(nfa);
        //dfa.printStructure();
        
	}
	
	/**
	 * This method will apply the NFA -> DFA algorithm specified in the book.
	 * An Automata (which is a pretty terrible name, I admit) is just an NFA.
	 * It contains a start Node and an end Node. Nodes are connected by Paths.
	 * There are a few types of Paths (RangePath, AnythingPath, ConversePath,
	 * CharacterPath). I'm assuming the logic for converting each of these will
	 * have to be specifically laid out.
	 *	
	 * Worst case scenario, I'm going to have to create an NFA simulator to test my
	 * NFA implementation, and we can implement the "table-walker" from that. But
	 * we should try our hardest to get the DFA conversion implemented ASAP.
	 *
	 * Until the NFA constructor is done, it would be fine for you to create a sample NFA
	 * and try to work through converting it to a DFA.
	 * 
	 * @param nfa The input NFA.
	 */
	public void buildFromNFA(NFA nfa) {
        HashSet<Node> currentState, nextState;
        HashMap<Node, HashSet<Node>> epsilonClosures;
        Stack<HashSet<Node>> stack;
        HashSet<HashSet<Node>> visitedStates;
        TreeSet<Character> alphabet;
        int alphabetSize;
        Character character;
        Iterator<Node> nextNodesInNfa, nextNodesInDfa;
        SetAutomata sMata;

		epsilonClosures = epsilonClosures(nfa);
        
        // Begin constructing the DFA by computing transitions for the set of
        // nodes that are part of the start node's epsilon closure
        startState = epsilonClosures.get(nfa.getStartNode()); // -------------------------------------------------------------------!!
        stack = new Stack<HashSet<Node>>();
        visitedStates = new HashSet<HashSet<Node>>();

        stack.push(currentState);
        visitedStates.add(currentState);
        sMata = new SetAutomata(currentState);

        // Get all characters the NFA accepts
        alphabet = nfa.getAlphabet(); // ------------------------------------------------------------------------!!
        alphabetSize = alphabet.size();

        while(!stack.empty()) {
            currentState = stack.pop();

            for (int i = 0; i < alphabetSize; i++) {
                character = alphabet(i);

                // Determine where the nodes in currentState would transition in
                // the NFA
                nextNodesInNfa = computeTransitions(currentState, character).iterator();

                while (nextNodesInNfa.hasNext()) {
                    // Get the nodes in the epsilon closure of the current NFA
                    // node; the epsilon closure is part of the next state in
                    // the DFA
                    nextNodesInDfa = epsilonClosures.get(nextNfaNodes.next()).iterator();

                    while (nextNodesInDfa.hasNext()) {
                        nextState.add(iter.next());
                    }
                }

                if (!visitedStates.contains(nextState)) {
                    stack.push(nextState);
                    visitedStates.add(nextState);
                }
                sMata.addTransition(currentState, character, nextState);
            }
        }

        // Minimize the DFA
        
	}

    private HashMap<Node, HashSet<Node>> epsilonClosures(NFA nfa) {
        ArrayList<Node> oneEpsilonClosure;
        HashSet<Node> epsClosureSet;
        HashMap<Node, HashSet<Node>> epsilonClosures;

        // Determine the epsilon closures of all nodes in the nfa. The sets of
        // nodes in each epsilon closure will be combined later to form nodes in
        // the DFA
        epsilonClosures = new HashMap<Node, HashSet<Node>>();

        for (Node node : nfa.getNodes()) { // ------------------------------------------------------------------------!!
            oneEpsilonClosure = nfa.traverseNullPaths(new ArrayList<Node>(), node);

            // The epsilon closure is an ArrayList right now; it's more useful
            // to me as a set
            epsilonClosureSet = new HashSet<Node>();

            for (int i = 0; i < oneEpsilonClosure.size(); i++) {
                epsilonClosureSet.add(oneEpsilonClosure.get(i));
            }
            epsilonClosures.add(node, epsilonClosureSet);
        }

        return epsilonClosures;
    }

    private HashSet<Node> computeTransitions(HashSet<Node> inNodes, Character c) {
        HashSet<Node> outNodes = new HashSet<Node>();
        Iterator<Node> listOfOutNodes = inNodes.iterator();
        Iterator<Node> nextNodes;
        Node node;

        while (listOfOutNodes.hasNext()) {
            nextNodes = listOfOutNodes.next().getNextNodesFor(c).iterator();

            while (nextNodes.hasNext()) {
                outNodes.add(nextNodes.next()); // ------------------------------------------------!!
            }
        }
        return outNodes;
    }
}

// Converts sets of nodes from an NFA into states in a DFA, storing the states
// in a Hashmap with the set of nodes as the key. Also converts transitions
// between sets of nodes to state transitions.
class SetAutomata {

    State startState;
    HashMap<HashSet<Node>, State> states; // identifies the States by the set of NFA Nodes they represent

    public SetAutomata() {
        startState = null;
        states = new HashMap<HashSet<Node>, State>();
    }

    // Creates a new automata with a single State representing the given set of
    // nodes
    public SetAutomata(HashSet<Node> setOfNodes) {
        startState = new State(setOfNodes);
        states = new HashMap<ArrayList<Node>, State>();

        states.add(setOfNodes, startState);
    }

    // Creates new states for the given sets of nodes if needed, or finds the
    // States that represent them, then adds a transition to the State for src
    // on the given character to the State for dst
    public void addTransition(HashSet<Node> src, Character c, HashSet<Node> dst) {
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

    // Returns the mapping from sets of NFA nodes to DFA States maintained by
    // the automata
    public HashMap<HashSet<Node>, State> getStates() {
        return states;
    }
}

class State {
    HashMap<Character, State> transitions;
    HashSet<Node> nodes;

    // Constructs a State to represent the given set of nodes in a DFA
    public State(HashSet<Node> setOfNodes) {
        transitions = new HashMap<Character, State>();
        nodes = setOfNodes;
    }

    // Stores a transition from this State to the given state on the given
    // character
    public void addTransition(Character c, State nextState) {
        transitions.put(c, nextState);
    }

    // Returns whether the given node is represented by this State
    public boolean contains(Node node) {
        return nodes.contains(node);
    }

    // Returns whether the given character is a valid transition from this State
    public boolean accepts(Character c) {
        return transitions.containsKey(c);
    }

    // If this State accepts the given character, it returns the next State in
    // the transition; otherwise it returns null
    public State nextState(Character c) {
        if (transitions.containsKey(c)) {
            return transitions.get(c);
        }
        return null;
    }
}


