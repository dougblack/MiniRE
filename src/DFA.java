import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.TreeSet;
import java.util.Set;
import java.util.Stack;

public class DFA {

    private SetAutomata sMata;

	public DFA(NFA nfa) {
        sMata = new SetAutomata();

		if (nfa != null) {
            buildFromNFA(nfa);
        }
	}

	public static void main(String args[]) {
		NFA nfa = new NFA(args[0]);
        DFA dfa = new DFA(nfa);
        dfa.printStructure();
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
        System.out.println("END");
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
	 * 
	 * @param nfa The input NFA.
	 */
	public void buildFromNFA(NFA nfa) {
        HashSet<Node> currentState, nextState;
        HashMap<Node, HashSet<Node>> epsilonClosures;
        Stack<HashSet<Node>> stack;
        HashSet<HashSet<Node>> visitedStates;
        TreeSet<Character> alphabet;
        Iterator<Node> nextNfaNodes, nextDfaNodes;
        Node node;
        boolean acceptState = false;
        //SetAutomata sMata;

		epsilonClosures = epsilonClosures(nfa);
        
        // Begin constructing the DFA by computing transitions for the set of
        // nodes that are part of the start node's epsilon closure
        currentState = epsilonClosures.get(nfa.getStartNode());
        stack = new Stack<HashSet<Node>>();
        visitedStates = new HashSet<HashSet<Node>>();

        stack.push(currentState);
        visitedStates.add(currentState);
        sMata = new SetAutomata(currentState);

        // Get all characters the NFA accepts
        alphabet = nfa.getAlphabet();

        while(!stack.empty()) {
            currentState = stack.pop();

            for (char character : alphabet) {
                nextState = new HashSet<Node>();

                // Determine where the nodes in currentState would transition to
                // in the NFA
                nextNfaNodes = computeTransitions(currentState, character).iterator();
                
                while (nextNfaNodes.hasNext()) {
                    // Get the nodes in the epsilon closure of the current NFA
                    // node; the epsilon closure is part of the next state in
                    // the DFA
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
                }
                acceptState = false;
            }
        }

        // Minimize the DFA
	}

    private HashMap<Node, HashSet<Node>> epsilonClosures(NFA nfa) {
        ArrayList<Node> oneEpsilonClosure;
        HashSet<Node> epsilonClosureSet;
        HashMap<Node, HashSet<Node>> epsilonClosures;

        // Determine the epsilon closures of all nodes in the nfa. The sets of
        // nodes in each epsilon closure will be combined later to form nodes in
        // the DFA
        epsilonClosures = new HashMap<Node, HashSet<Node>>();

        for (Node node : nfa.getNodes()) {
            oneEpsilonClosure = nfa.traverseNullPaths(new ArrayList<Node>(), node);

            // The epsilon closure is an ArrayList right now; it's more useful
            // to me as a set
            epsilonClosureSet = new HashSet<Node>();

            for (int i = 0; i < oneEpsilonClosure.size(); i++) {
                epsilonClosureSet.add(oneEpsilonClosure.get(i));
            }
            epsilonClosures.put(node, epsilonClosureSet);
        }

        return epsilonClosures;
    }

    private HashSet<Node> computeTransitions(HashSet<Node> inNodes, char c) {
        HashSet<Node> outNodes = new HashSet<Node>();
        Iterator<Node> listOfInNodes = inNodes.iterator();
        Iterator<Node> nextNodes;
        Node src, dst;

        while (listOfInNodes.hasNext()) {
            src = listOfInNodes.next();
            nextNodes = src.getNextNodesFor(c).iterator();

            while (nextNodes.hasNext()) {
                dst = nextNodes.next();
                outNodes.add(dst);
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
    HashSet<State> acceptingStates;
    HashMap<HashSet<Node>, State> states; // identifies the States by the set of NFA Nodes they represent

    public SetAutomata() {
        startState = null;
        acceptingStates = new HashSet<State>();
        states = new HashMap<HashSet<Node>, State>();
    }

    // Creates a new automata with a single State representing the given set of
    // nodes
    public SetAutomata(HashSet<Node> setOfNodes) {
        startState = new State(setOfNodes);
        acceptingStates = new HashSet<State>();
        states = new HashMap<HashSet<Node>, State>();

        states.put(setOfNodes, startState);
    }

    // Creates new states for the given sets of nodes if needed, or finds the
    // States that represent them, then adds a transition to the State for src
    // on the given character to the State for dst and adds an incoming path to
    // dst from src on char c
    public void addTransition(HashSet<Node> src, char c, HashSet<Node> dst) {
        State currentState = findState(src);
        State nextState = findState(dst);

        currentState.addTransition(c, nextState);
        nextState.addInTransition(nextState, c);     

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

    public void setAccepting(HashSet<Node> nodeSet) {
        State accepting = findState(nodeSet);
        acceptingStates.add(accepting);
    }
}

class State {
    static int _stateId = 0;
    int stateId = _stateId++;
    HashMap<Character, State> transitions;
    HashMap<State, Character> inTransitions;
    HashSet<Node> nodes;

    // Constructs a State to represent the given set of nodes in a DFA
    public State(HashSet<Node> setOfNodes) {
        transitions = new HashMap<Character, State>();
        inTransitions = new HashMap<State, Character>();
        nodes = setOfNodes;
    }

    // Stores a transition from this State to the given state on the given
    // character
    public void addTransition(char c, State nextState) {
        transitions.put(c, nextState);
    }

    // Stores a transition to this State from the given state on the given
    // character
    public void addInTransition(State nextState, char c) {
        inTransitions.put(nextState, c);
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


