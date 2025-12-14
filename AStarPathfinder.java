import java.util.ArrayList;
import java.util.Comparator;

public class AStarPathfinder {
    Node[][] node;
    ArrayList<Node> openList = new ArrayList<>();
    public ArrayList<Node> checkedList = new ArrayList<>();
    
    Node startNode, goalNode, currentNode;
    public boolean goalReached = false;
    int maxCol, maxRow;

    public AStarPathfinder(Node[][] node, Node start, Node goal, int maxCol, int maxRow) {
        this.node = node;
        this.startNode = start;
        this.goalNode = goal;
        this.maxCol = maxCol;
        this.maxRow = maxRow;

        currentNode = startNode;
        getCost(startNode);
        openList.add(startNode);
    }

    private void getCost(Node node) {
        int xDist = Math.abs(node.col - startNode.col);
        int yDist = Math.abs(node.row - startNode.row);
        node.gCost = xDist + yDist;

        xDist = Math.abs(node.col - goalNode.col);
        yDist = Math.abs(node.row - goalNode.row);
        node.hCost = xDist + yDist;

        node.fCost = node.gCost + node.hCost;
    }

    public void step() {
        if (openList.isEmpty()) return;

        openList.sort(Comparator.comparingInt((Node n) -> n.fCost));
        currentNode = openList.get(0);
        openList.remove(0);

        currentNode.open = false;
        currentNode.checked = true;
        checkedList.add(currentNode);

        if (currentNode == goalNode) {
            goalReached = true;
            backtrackPath();
            return;
        }

        openNode(currentNode.col, currentNode.row - 1);
        openNode(currentNode.col, currentNode.row + 1);
        openNode(currentNode.col - 1, currentNode.row);
        openNode(currentNode.col + 1, currentNode.row);
    }

    private void openNode(int col, int row) {
        if (col >= 0 && col < maxCol && row >= 0 && row < maxRow) {
            Node neighbor = node[col][row];
            if (!neighbor.open && !neighbor.checked && !neighbor.solid) {
                neighbor.open = true;
                neighbor.parent = currentNode;
                getCost(neighbor);
                openList.add(neighbor);
            }
        }
    }

    private void backtrackPath() {
        Node current = goalNode;
        while (current != startNode) {
            current = current.parent;
            if (current != startNode) {
                current.setAsPath();
            }
        }
    }
}