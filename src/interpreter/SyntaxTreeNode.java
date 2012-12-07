package interpreter;

import tokenizer.Token;

import java.util.ArrayList;
import java.util.Stack;

public class SyntaxTreeNode {

    Token token;
    ArrayList<SyntaxTreeNode> children;
    SyntaxTreeNode sibling;
    int lineNumber;
    String id;
    String value;
    String nodeType;

    public SyntaxTreeNode(Token nodeToken, int tokenLineNumber) {
        children = new ArrayList<SyntaxTreeNode>();
        token = nodeToken;
        lineNumber = tokenLineNumber;
        id = token.getId();
        value = token.getString();
    }

    public SyntaxTreeNode(String nodeType) {
        children = new ArrayList<SyntaxTreeNode>();
        this.nodeType = nodeType;
    }

    public void addChild(SyntaxTreeNode newChild) {
        children.add(newChild);
    }

    public void setSibling(SyntaxTreeNode newSibling) {
        sibling = newSibling;
    }

    /**
     * This will use level order printing.
     */
    public void printLevels() {
        Stack<SyntaxTreeNode> nextLevel = new Stack<SyntaxTreeNode>();
        nextLevel.add(this);
        int level = 0;
        while (!nextLevel.empty()) {
            System.out.println("=========LEVEL " + level + "==========");
            Stack<SyntaxTreeNode> nextLevelTemp = new Stack<SyntaxTreeNode>();
            while (!nextLevel.empty()) {
                SyntaxTreeNode node = nextLevel.pop();
                if (node.id != null)
                    System.out.println("Node: " + node.id + "," + node.value);
                else
                    System.out.println("Node: " + node.nodeType);
                if (node.children.isEmpty())
                    continue;

                for (int i = node.children.size()-1; i >= 0; i--) {
                    nextLevelTemp.push(node.children.get(i));
                }
            }
            level++;
            nextLevel = nextLevelTemp;
        }
    }

}
