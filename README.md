MiniRE
=====

Intro
-----

This is a fully implemented interpreter for the MiniRE 'toy' language. You can think of MiniRE as a mini Awk or Perl.

MiniRE deals primarily with the combination and manipulation of `string-lists`.

<pre>
begin

pups = find 'puppy' in 'animals.txt';
num_of_pups = #pups;
kitties = find 'kitten' in 'animals.txt';
num_of_kitties = #kitties;

pups_and_kitts = find 'puppy in 'animals.txt' union find 'kitten' in 'animals.txt;

print (pups,num_of_pups, kitties, num_of_kitties);
end
</pre>

The above code outputs all occurrences of the words "puppy" and 'kitten', as well as the total number of occurrences of each.

It contains a built-from-scratch:
- `Regex Engine` (automata-based)
- `Lexer`
- `Scanner`
- `Parser`
- `Evaluator`

The regex engine, lexer, and scanner were designed to be extensible, so they contain no MiniRE-specific functionality. They can be reused with any regular language grammar to produce consumable tokens for the production of an Abstract Syntax Tree.

The parser and evaluator are, however, specific to MiniRE scripts.

Process
-------

This is what happens when a MiniRE script is run.

1. The MiniRE lexical spec is read by the `Lexer` (using the `Regex Engine` and a series of `Nondeterministic Finite State Automata` or `NFAs` are built and combined together.
2. For efficiency in later parsing, these `NFAs` are converted to `Deterministic Finite State Automata` or `DFAs`.
3. The MiniRE input script is read character by character and the `DFAs` are used to produce `Tokens` (meaningful groups of character e.g `find` and `begin`).
4. The resultant token stream is parsed by the (Recursive Descent) `Parser` and an `Abstract Syntax Tree` or `AST` is constructed.
5. The `Evaluator` begins at the root of the `AST` and evaluates all the nodes in the tree, producing output as necessary.
6. Once there are no more nodes to evaluate, the interpeter quits.

Each step has built in error handling so errors are caught before the propogate down the pipeline.
