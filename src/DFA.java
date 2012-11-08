

public class DFA {

	public DFA() {
		
	}

	public static void main(String args[]) {
		Automata nfa = new Automata();
		nfa.inNode = new Node();
		nfa.outNode = new Node();
		Path path1 = new Path();
		
		// create a sample NFA here. If you don't 
		// understand how Automata and Node's are set up
		// feel free to ask.
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
	 * @param dfa The input DFA.
	 */
	public void buildFromNFA(Automata dfa) {
		/* TODO */
	}
	
}