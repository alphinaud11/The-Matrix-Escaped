package code;

public abstract class Node {

    public String state;
    public Node parentNode;
    public String operator;
    public int depth;
    public int pathCost;

    public Node(String state, Node parentNode, String operator, int depth, int pathCost) {
        this.state = state;
        this.parentNode = parentNode;
        this.operator = operator;
        this.depth = depth;
        this.pathCost = pathCost;
    }

}
