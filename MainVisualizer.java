import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Stack;
import java.util.Random;

public class MainVisualizer extends JPanel {

    // --- Complex & Thin Settings ---
    final int nodeSize = 15; 
    final int maxCol = 61; 
    final int maxRow = 41; 
    final int screenWidth = nodeSize * maxCol;
    final int screenHeight = nodeSize * maxRow;

    // --- Colors (Professional Navy & Neon) ---
    final Color COLOR_BG = new Color(10, 15, 30);       
    final Color COLOR_WALL = new Color(20, 25, 45);     
    final Color COLOR_DOT_GRID = new Color(50, 60, 80); 
    final Color COLOR_CHECKED = new Color(60, 80, 200); 
    final Color COLOR_PATH = new Color(0, 255, 255);    
    
    Node[][] grid = new Node[maxCol][maxRow];
    AStarPathfinder pathfinder;
    Timer timer;

    public MainVisualizer() {
        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setBackground(COLOR_BG);
        this.setDoubleBuffered(true);
        this.setFocusable(true); // Allow panel to listen for keys

        // --- Press SPACE to Restart ---
        this.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                    startSimulation();
                }
            }
        });

        startSimulation();
    }

    // Initialize/Reset the entire simulation
    private void startSimulation() {
        if (timer != null) timer.stop();

        setupGrid();     // 1. Clear Grid
        generateMaze();  // 2. Create Random Maze
        setRandomStartAndGoal(); // 3. Pick Random Points
        
        // 4. Create Pathfinder
        // Find the start and goal nodes we just marked
        Node start = null, goal = null;
        for(int c=0; c<maxCol; c++) {
            for(int r=0; r<maxRow; r++) {
                if(grid[c][r].start) start = grid[c][r];
                if(grid[c][r].goal) goal = grid[c][r];
            }
        }

        pathfinder = new AStarPathfinder(grid, start, goal, maxCol, maxRow);

        // 5. Start Animation Loop
        timer = new Timer(10, e -> {
            if (pathfinder != null && !pathfinder.goalReached) {
                for(int i=0; i<3; i++) {
                    if(!pathfinder.goalReached) pathfinder.step();
                }
                repaint();
            } else {
                timer.stop();
            }
        });
        timer.start();
        repaint();
    }

    private void setupGrid() {
        for (int col = 0; col < maxCol; col++) {
            for (int row = 0; row < maxRow; row++) {
                grid[col][row] = new Node(col, row);
                grid[col][row].setAsSolid(); // Start full of walls
            }
        }
    }

    private void setRandomStartAndGoal() {
        Random rand = new Random();
        ArrayList<Node> openNodes = new ArrayList<>();

        // Collect all walkable nodes (not walls)
        for (int c = 0; c < maxCol; c++) {
            for (int r = 0; r < maxRow; r++) {
                if (!grid[c][r].solid) {
                    openNodes.add(grid[c][r]);
                }
            }
        }

        // Pick random Start
        Node start = openNodes.get(rand.nextInt(openNodes.size()));
        start.setAsStart();
        
        // Pick random Goal (ensure it's not the same as start)
        Node goal = start;
        while (goal == start) {
            goal = openNodes.get(rand.nextInt(openNodes.size()));
        }
        goal.setAsGoal();
    }

    // Recursive Backtracker Maze
    private void generateMaze() {
        Stack<Node> stack = new Stack<>();
        // Start maze generation slightly offset so we have borders
        Node current = grid[1][1]; 
        current.solid = false;
        stack.push(current);

        Random rand = new Random();

        while (!stack.isEmpty()) {
            current = stack.peek();
            Node next = getUnvisitedNeighbor(current, rand);

            if (next != null) {
                next.solid = false;
                
                // Remove wall between
                int midCol = (current.col + next.col) / 2;
                int midRow = (current.row + next.row) / 2;
                grid[midCol][midRow].solid = false;

                stack.push(next);
            } else {
                stack.pop();
            }
        }
        
        // Add random loops for complexity
        for(int i=0; i<150; i++) {
            int c = rand.nextInt(maxCol-2) + 1;
            int r = rand.nextInt(maxRow-2) + 1;
            grid[c][r].solid = false;
        }
    }

    private Node getUnvisitedNeighbor(Node n, Random rand) {
        int[][] dirs = {{0, -2}, {0, 2}, {-2, 0}, {2, 0}};
        
        // Shuffle directions
        for (int i = 0; i < dirs.length; i++) {
            int randomIndex = rand.nextInt(dirs.length);
            int[] temp = dirs[i];
            dirs[i] = dirs[randomIndex];
            dirs[randomIndex] = temp;
        }

        for (int[] dir : dirs) {
            int c = n.col + dir[0];
            int r = n.row + dir[1];

            if (c > 0 && c < maxCol - 1 && r > 0 && r < maxRow - 1) {
                if (grid[c][r].solid) {
                    return grid[c][r];
                }
            }
        }
        return null;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw Instructions
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("SansSerif", Font.PLAIN, 10));
        g2.drawString("Press SPACE to Generate New Maze", 10, 10);

        for (int col = 0; col < maxCol; col++) {
            for (int row = 0; row < maxRow; row++) {
                Node n = grid[col][row];
                int x = n.col * nodeSize;
                int y = n.row * nodeSize;

                if (n.solid) {
                    g2.setColor(COLOR_WALL);
                    g2.fillRect(x, y, nodeSize, nodeSize);
                } 
                else {
                    // Background Dot
                    g2.setColor(COLOR_DOT_GRID);
                    g2.fillOval(x + nodeSize/2 - 1, y + nodeSize/2 - 1, 2, 2);

                    if (n.start) {
                        g2.setColor(Color.GREEN);
                        g2.fillOval(x + 2, y + 2, nodeSize - 4, nodeSize - 4);
                    }
                    else if (n.goal) {
                        g2.setColor(Color.RED);
                        g2.fillOval(x + 2, y + 2, nodeSize - 4, nodeSize - 4);
                    }
                    else if (n.isPath) {
                        // Pen Dot Style - Cyan with Glow
                        g2.setColor(COLOR_PATH);
                        g2.fillOval(x + 4, y + 4, nodeSize - 8, nodeSize - 8);
                        g2.setColor(Color.WHITE);
                        g2.fillOval(x + 6, y + 6, nodeSize - 12, nodeSize - 12);
                    }
                    else if (n.checked) {
                        // Visited - Blue Dot
                        g2.setColor(COLOR_CHECKED);
                        g2.fillOval(x + 5, y + 5, nodeSize - 10, nodeSize - 10);
                    }
                }
            }
        }
    }

    public static void main(String[] args) {
        JFrame window = new JFrame();
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setResizable(false);
        window.setTitle("Professional A* Visualization");

        MainVisualizer panel = new MainVisualizer();
        window.add(panel);

        window.pack();
        window.setLocationRelativeTo(null);
        window.setVisible(true);
    }
}