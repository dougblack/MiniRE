import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.TreeSet;
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
		/*Automata nfa = new Automata();
		nfa.inNode = new Node();
		nfa.outNode = new Node();
		Path path1 = new Path();*/
		
		NFA nfa = new NFA("ab");
        DFA dfa = new DFA(nfa);
        dfa.printStructure();
        
	}
	
    public void printStructure() {
        HashMap<HashSet<Node>, State> states = sMata.states;
        Iterator<HashSet<Node>> keys = states.keySet().iterator();
        HashSet<Node> nfaNodes;
    
        System.out.println("The states in the DFA consist of the following sets of NFA nodes:");
        while (keys.hasNext()) {
            nfaNodes = keys.next();

            System.out.print("State: " + states.get(nfaNodes).stateId + "\tNFA nodes: {");

            for (Node node : nfaNodes) {
                System.out.print(node.nodeId + ", ");
            }
            System.out.println("}");
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
        Iterator<Node> nextNfaNodes, nextDfaNodes;
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
            nextState = new HashSet<Node>();

            for (char character : alphabet) {

                // Determine where the nodes in currentState would transition in
                // the NFA
                nextNfaNodes = computeTransitions(currentState, character).iterator();

                while (nextNfaNodes.hasNext()) {
                    // Get the nodes in the epsilon closure of the current NFA
                    // node; the epsilon closure is part of the next state in
                    // the DFA
                    nextDfaNodes = epsilonClosures.get(nextNfaNodes.next()).iterator();

                    while (nextDfaNodes.hasNext()) {
                        nextState.add(nextDfaNodes.next());
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
        HashSet<Node> epsilonClosureSet;
        HashMap<Node, HashSet<Node>> epsilonClosures;
        Node epsilonNode;

        // Determine the epsilon closures of all nodes in the nfa. The sets of
        // nodes in each epsilon closure will be combined later to form nodes in
        // the DFA
        epsilonClosures = new HashMap<Node, HashSet<Node>>();
        System.out.println("Computing epsilon closures for the nfa with regex \"" + nfa.thisRegex + "\"");

        for (Node node : nfa.getNodes()) {
            System.out.print("Node " + node.nodeId);
            oneEpsilonClosure = nfa.traverseNullPaths(new ArrayList<Node>(), node);
            System.out.print(" has the following epsilon closure: {");

            // The epsilon closure is an ArrayList right now; it's more useful
            // to me as a set
            epsilonClosureSet = new HashSet<Node>();

            for (int i = 0; i < oneEpsilonClosure.size(); i++) {
                epsilonNode = oneEpsilonClosure.get(i);
                System.out.print(epsilonNode.nodeId + ", ");
                epsilonClosureSet.add(epsilonNode);
            }
            System.out.println("}");
            epsilonClosures.put(node, epsilonClosureSet);

            System.out.print("It transitions to: ");
            for (Path path : node.paths) { 
                System.out.print(path.destination.nodeId + " on ");
                if (path instanceof ConverseRangePath) {
                    System.out.print("anything not in the range " +
                        ((ConverseRangePath)path).start + " to " + ((ConverseRangePath)path).end);
                } else if (path instanceof RangePath) {
                    System.out.print("anything in the range " + ((RangePath)path).start + " to " + ((RangePath)path).end);
                } else if (path instanceof AnythingPath) {
                    System.out.print("anything but epsilon");
                } else if (path instanceof ConversePath) {
                    System.out.print("anything not in the string " + ((ConversePath)path).notAccepted);
                } else if (path instanceof CharacterPath) {
                    System.out.print("anything in the string " + ((CharacterPath)path).accepted);
                } else {
                    System.out.print("epsilon");
                }
            }
            System.out.println(".");
        }

        return epsilonClosures;
    }

    private HashSet<Node> computeTransitions(HashSet<Node> inNodes, char c) {
        HashSet<Node> outNodes = new HashSet<Node>();
        Iterator<Node> listOfOutNodes = inNodes.iterator();
        Iterator<Node> nextNodes;
        Node node;

        while (listOfOutNodes.hasNext()) {
            nextNodes = listOfOutNodes.next().getNextNodesFor(c).iterator();

            while (nextNodes.hasNext()) {
                outNodes.add(nextNodes.next());
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
        states = new HashMap<HashSet<Node>, State>();

        states.put(setOfNodes, startState);
    }

    // Creates new states for the given sets of nodes if needed, or finds the
    // States that represent them, then adds a transition to the State for src
    // on the given character to the State for dst
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
}

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


