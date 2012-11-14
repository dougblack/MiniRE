import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.TreeSet;
import java.util.Set;
import java.util.Stack;

/**
 * Converts an NFA to a DFA and allows stepping through the DFA one input at a
 * time. Has methods to test if the DFA accepts given strings and to display
 * the DFA's states and transitions.
 */
public class DFA {

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
        dfa4.testDFA("", "(dougblack)+", "(dougblack)", "dougblack",
            "dougblackdougblach", "dougblackdougblack");
		
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

    private SetAutomata sMata; // contains the actual states and transitions
    private String regex; // regular expression corresponding to this DFA
    private State stepState; // current state the DFA is in when stepping

    /**
     * Constructs a DFA based on the given NFA
     *
     * @param nfa An NFA
     */
	public DFA(NFA nfa) {
        sMata = new SetAutomata();

		if (nfa != null) {
            buildFromNFA(nfa);
            regex = nfa.thisRegex;
        }
        stepState = sMata.startState;
	}

    /**
     * Tests if this DFA accepts the given string
     *
     * @param string A string to feed to this DFA
     * @return true if the string was accepted, false otherwise
     */
    public boolean testString(String string) {
        State currentState = sMata.startState;
        char c;
        boolean result = false;
        for (int i = 0; i < string.length(); i++) {
            c = string.charAt(i);

            if (!currentState.transitionsOn(c)) {
                return false;
            }
            currentState = currentState.nextState(c);
        }
        if (sMata.acceptStates.contains(currentState)) {
            result = true;
        }
        return result;
    }
	
    /**
     * Tests each given string to see if this DFA acccepts it
     *
     * @param strings Any number of strings to feed to this DFA
     */
	public void testDFA(String ...strings) {
		System.out.println("Starting new DFA with regex: \"" + this.regex +
            "\""); // quotes are necessary to identify the empty string
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
	
    /**
     * Prints all state transitions, the start state, and the accept state for
     * this DFA
     */
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
                    " ----(" + c + ")----> State " +
                    states.get(nfaNodes).transitions.get(c).stateId);
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
	 * Applies the NFA -> DFA algorithm specified in the book, using epsilon
     * closures and the subset construction.
	 * 
	 * @param nfa An NFA
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
                nextNfaNodes =
                    computeTransitions(currentState, character).iterator();
                
                while (nextNfaNodes.hasNext()) {
                    // Get the nodes in the current node's epsilon closure; the
                    // epsilon closure is a state in the DFA
                    nextDfaNodes =
                        epsilonClosures.get(nextNfaNodes.next()).iterator();

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
        if (epsilonClosures.get(nfa.getStartNode()).contains(
            nfa.getAcceptingNode())) {

            sMata.setAccepting(epsilonClosures.get(nfa.getStartNode()));
        }
	}

    /**
     * Maps each node from the NFA to its epsilon closure
     *
     * @param nfa The NFA this DFA was derived from
     * @return A map of each node from the NFA to its epsilon closure
     */
    private HashMap<Node, HashSet<Node>> epsilonClosures(NFA nfa) {
        ArrayList<Node> oneEpsilonClosure;
        HashSet<Node> epsilonClosureSet;
        HashMap<Node, HashSet<Node>> epsilonClosures;

        // Determine the epsilon closures of all nodes in the nfa. The sets of
        // nodes in each epsilon closure will be combined later to form nodes in
        // the DFA
        epsilonClosures = new HashMap<Node, HashSet<Node>>();

        for (Node srcNode : nfa.getNodes()) {
            oneEpsilonClosure =
                nfa.traverseNullPaths(new ArrayList<Node>(), srcNode);

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

    /**
     * Computes the set of all nodes the nodes in inNodes could transition to on
     * the given character
     *
     * @param inNodes An epsilon closure from an NFA
     * @param c A character
     * @return All nodes the nodes in inNodes could transition to on c
     */
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

    /**
     * If the DFA's current state can transition on the given character, it
     * does, and the currentState is updated.
     *
     * @param c A character
     */
    public void step(char c) {
        if (stepState.transitionsOn(c)) {
            stepState = stepState.nextState(c);
        }
    }

    /**
     * Determines if the DFA's current state can transition on the given
     * character
     *
     * @param c A character
     * @return true if the DFA's current state can transition on c, false
     *          otherwise
     */
    public boolean transitionsOn(char c) {
        return stepState.transitionsOn(c);
    }

    /**
     * Determines if the DFA's current state is an accept state
     *
     * @return true if the DFA's current state is an accept state, false
     *          otherwise
     */
    public boolean inAcceptState() {
        return sMata.acceptStates.contains(stepState);
    }

    /**
     * Sets the current state of this DFA as the start state
     */
    public void reset() {
        stepState = sMata.startState;
    }
}

/**
 * Converts epsilon closures from an NFA into individual states in a DFA,
 * identifying the states by the set of nodes in the epsilon closure. Maintains
 * the single start state for this DFA and a set of all accept states.
 */
class SetAutomata {

    State startState; // start state of the DFA
    HashSet<State> acceptStates; // all accept states in the DFA
    HashMap<HashSet<Node>, State> states; // identifies the states by the set of
                                          // nodes they represent in the NFA

    /**
     * Creates a new automata with a single transitionless state as its start
     * state; does not assume the state is accepting
     */
    public SetAutomata() {
        acceptStates = new HashSet<State>();
        states = new HashMap<HashSet<Node>, State>();
        startState = new State(new HashSet<Node>());

        states.put(startState.nodes, startState);
    }

    /**
     * Creates a new automata with a single state that represents the given set
     * of nodes from an NFA as its start state; does not assume the state is
     * accepting, and does not record any transitions
     *
     * @param nodeSet An epsilon closure from an NFA
     */
    public SetAutomata(HashSet<Node> nodeSet) {
        startState = new State(nodeSet);
        acceptStates = new HashSet<State>();
        states = new HashMap<HashSet<Node>, State>();

        states.put(nodeSet, startState);
    }

    /**
     * Creates new states for the given sets of nodes if needed, or finds the
     * states that represent them; adds to the state representing src a
     * transition on the given character to the state representing dst
     *
     * @param src An epsilon closure from an NFA with a transition on c to dst
     * @param c A character
     * @param dst An epsilon closure from an NFA
     */
    public void addTransition(HashSet<Node> src, char c, HashSet<Node> dst) {
        State currentState = findState(src);
        State nextState = findState(dst);

        currentState.addTransition(c, nextState);   

        if (startState == null) {
            startState = currentState;
        }
    }

    /**
     * Returns either the state that represents the given set of nodes, or a new
     * state (if the set is new to this automata)
     *
     * @param nodeSet An epsilon closure from an NFA
     * @return The state representing the given set of nodes
     */
    public State findState(HashSet<Node> nodeSet) {
        State state;

        if (states.containsKey(nodeSet)) {
            return states.get(nodeSet);
        }
        states.put(nodeSet, state = new State(nodeSet));
        return state;
    }

    /**
     * Sets the state that represents the given set of nodes as accepting and
     * adds it to the list of accept states
     *
     * @param nodeSet An epsilon closure from an NFA
     */
    public void setAccepting(HashSet<Node> nodeSet) {
        State accepting = findState(nodeSet);
        acceptStates.add(accepting);
    }
}

/**
 * Represents an epsilon closure from an NFA; stores the set of nodes it
 * represents and maps transition characters to next states
 */
class State {
    static int _stateId = 0; // a way to quickly identify each state uniquely
    int stateId = _stateId++; // this state's unique number
    HashMap<Character, State> transitions; // all transitions from this state
    HashSet<Node> nodes; // epsilon closure from an NFA this state represents

    /**
     * Constructs a State to represent in a DFA the given epsilon closure of NFA
     * nodes
     * 
     * @param nodeSet An epsilon closure from an NFA
     */
    public State(HashSet<Node> nodeSet) {
        transitions = new HashMap<Character, State>();
        nodes = nodeSet;
    }

    /**
     * Stores a transition from this State to the given state on the given
     * character
     *
     * @param c A character that causes this state to transition to nextState
     * @param nextState The next state this state transitions to given c
     */
    public void addTransition(char c, State nextState) {
        transitions.put(c, nextState);
    }

    /**
     * Determines whether the given node is represented by this state
     *
     * @param node A node from an NFA
     * @return true if the node is in the set of nodes this state represents,
     *          false otherwise
     */
    public boolean contains(Node node) {
        return nodes.contains(node);
    }

    /**
     * Determines whether the given character can cause a transition at this
     * state
     *
     * @param c A character
     * @return true if this state transitions on c, false otherwise
     */
    public boolean transitionsOn(char c) {
        return transitions.containsKey(c);
    }

    /**
     * If this state transitions on the given character, it returns the next
     * state in the transition; otherwise it returns null
     *
     * @param c A character
     * @return either the next state after transitioning on c, or null if the
     *          state doesn't transition on c
     */
    public State nextState(char c) {
        if (transitions.containsKey(c)) {
            return transitions.get(c);
        }
        return null;
    }
}
