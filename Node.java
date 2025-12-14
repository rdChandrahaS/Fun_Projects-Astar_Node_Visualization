public class Node {
    public int col, row;
    public int gCost, hCost, fCost;
    public Node parent;
    
    public boolean start;
    public boolean goal;
    public boolean solid;
    public boolean open;
    public boolean checked;
    public boolean isPath;

    public Node(int col, int row) {
        this.col = col;
        this.row = row;
    }

    public void setAsStart() { start = true; }
    public void setAsGoal() { goal = true; }
    public void setAsSolid() { solid = true; }
    public void setAsOpen() { open = true; }
    public void setAsChecked() { checked = true; }
    public void setAsPath() { isPath = true; }
}