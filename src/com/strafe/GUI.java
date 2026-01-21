package com.strafe;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Objects;
import java.util.Queue;
import java.util.Stack;

public class GUI implements ActionListener {

    private static final int BORDER = 1;
    public static BoulderButtons[][] grid;
    public static int[][] gridToSolve = new int[8][7];
    private final JButton solveButton = new JButton("Solve");
    private final JButton hintsButton = new JButton("Hints");
    private final JButton historyButton = new JButton("History");
    private final JButton resetButton = new JButton("Reset");
    private final JButton trackerButton = new JButton("I Solved It!");
    private final JButton backButton = new JButton("Back");
    private final JLabel l1 = new JLabel("Steps: ");
    private final JLabel l2 = new JLabel("Steps: ");
    private final JLabel l3 = new JLabel("Steps: ");
    private final JLabel l4 = new JLabel("Steps: ");
    private final JLabel averageLabel = new JLabel();
    private final JPanel hp1 = new JPanel();
    private final JPanel hp2 = new JPanel();
    private final JPanel hp3 = new JPanel();
    private final JPanel hp4 = new JPanel();
    private final JPanel buttonGrid = new JPanel();
    private final JFrame main = new JFrame("Boulder Solver");
    private final JFrame history = new JFrame("History");
    private final static BoulderButtons[][] grid1 = new BoulderButtons[7][7];
    private final static BoulderButtons[][] grid2 = new BoulderButtons[7][7];
    private final static BoulderButtons[][] grid3 = new BoulderButtons[7][7];
    private final static BoulderButtons[][] grid4 = new BoulderButtons[7][7];
    private static int ROWS;
    private static int COL;
    private Stack<Solver.Move> steps;
    private int hintsUsed;
    private static int stepsNum = 0;
    private static boolean allHintsDisplayed = false;

    public GUI(int r, int c) {

        buttonGrid.setLayout(new GridLayout(r, c, BORDER, BORDER)); // initialize variables
        buttonGrid.setBackground(Color.WHITE);
        ROWS = r;
        COL = c;

        grid = new BoulderButtons[ROWS][COL]; // allocate grid

        // Makes look and feel the same for all platforms
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        for (int cc = 0; cc < COL; cc++) {
            for (int rr = 0; rr < ROWS; rr++) {
                grid[rr][cc] = new BoulderButtons(rr, cc);
                buttonGrid.add(grid[rr][cc]); // add button to panel
            }
        }
        this.initBoard();
    }

    public void initBoard() {
        main.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Initiate GUI Elements
        main.setLayout(new GridBagLayout());
        main.add(buttonGrid);
        main.setSize(900, 600);
        main.setResizable(false);

        solveButton.setBounds(150, 10, 400, 80);
        hintsButton.setBounds(150, 10, 400, 80);
        historyButton.setBounds(150, 10, 400, 80);
        resetButton.setBounds(150, 10, 400, 80);
        trackerButton.setBounds(720, 250, 500, 80);

        JPanel buttons = new JPanel();
        buttons.setLayout(new BorderLayout());

        buttons.add(solveButton, BorderLayout.PAGE_START);
        buttons.add(hintsButton, BorderLayout.CENTER);
        buttons.add(historyButton, BorderLayout.LINE_END);
        buttons.add(resetButton, BorderLayout.LINE_START);
        buttons.add(trackerButton, BorderLayout.PAGE_END);
        main.add(buttons);


        solveButton.addActionListener(this);
        hintsButton.addActionListener(this);
        historyButton.addActionListener(this);
        resetButton.addActionListener(this);
        trackerButton.addActionListener(this);

        main.setVisible(true);
        history.setVisible(false);
        historyFrame();
    }

    public void historyFrame() {
        history.setLayout(new GridBagLayout()); // Initialize GUI Elements for History Section
        history.setResizable(false);
        history.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        history.setSize(900, 600);

        backButton.setBounds(620, 400, 100, 40);
        backButton.addActionListener(this);

        l1.setBounds(300, 10, 400, 40);
        l2.setBounds(300, 250, 400, 40);
        l3.setBounds(650, 10, 400, 40);
        l4.setBounds(650, 250, 400, 40);

        hp1.setLayout(new GridLayout(7, 7, BORDER, BORDER));
        hp2.setLayout(new GridLayout(7, 7, BORDER, BORDER));
        hp3.setLayout(new GridLayout(7, 7, BORDER, BORDER));
        hp4.setLayout(new GridLayout(7, 7, BORDER, BORDER));

        history.add(averageLabel);

        for (int cc = 0; cc < 7; cc++) {
            for (int rr = 0; rr < 7; rr++) {
                grid1[rr][cc] = new BoulderButtons(rr, cc); // Create the buttons
                grid2[rr][cc] = new BoulderButtons(rr, cc);
                grid3[rr][cc] = new BoulderButtons(rr, cc);
                grid4[rr][cc] = new BoulderButtons(rr, cc);

                grid1[rr][cc].historyBoardConfig(10, false); // Setup config for the BoulderButtons
                grid2[rr][cc].historyBoardConfig(10, false);
                grid3[rr][cc].historyBoardConfig(10, false);
                grid4[rr][cc].historyBoardConfig(10, false);

                hp1.add(grid1[rr][cc]); // Add elements to JPanel
                hp2.add(grid2[rr][cc]);
                hp3.add(grid3[rr][cc]);
                hp4.add(grid4[rr][cc]);
            }
        }


        GridBagConstraints gbc = new GridBagConstraints(); // GUI location elements
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.gridx = 0;
        gbc.gridy = 0;
        history.add(hp1, gbc);
        gbc.gridx = 1;
        gbc.gridy = 0;
        history.add(l1, gbc);
        gbc.gridx = 0;
        gbc.gridy = 1;
        history.add(hp2, gbc);
        gbc.gridx = 1;
        gbc.gridy = 1;
        history.add(l2, gbc);
        gbc.gridx = 2;
        gbc.gridy = 0;
        history.add(hp3, gbc);
        gbc.gridx = 3;
        gbc.gridy = 0;
        history.add(l3, gbc);
        gbc.gridx = 2;
        gbc.gridy = 1;
        history.add(hp4, gbc);
        gbc.gridx = 3;
        gbc.gridy = 1;
        history.add(l4, gbc);
        gbc.gridx = 0;
        gbc.gridy = 3;
        history.add(averageLabel,gbc);
        gbc.gridx = 3;
        gbc.gridy = 2;
        gbc.weighty = 0.5;
        gbc.gridheight = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        history.add(backButton, gbc); // Add back button to JPanel
    }


    public void updateHistoryFrame() {
        HistoricGrid[] g = SaveLoad.load(); // Retrieve latest four saved grids
        boolean gb0 = true;
        boolean gb1 = true;
        boolean gb2 = true;
        boolean gb3 = true;

        int total = 0;
        int divider = 0;

        if (g[0] == null) gb0 = false; // Flags to prevent crashes when historic grids do not exist
        if (g[1] == null) gb1 = false;
        if (g[2] == null) gb2 = false;
        if (g[3] == null) gb3 = false;

        if (gb0 && g[0].getSteps() == -1) { // Check for solver usage
            l1.setText("Used Solver");
        } else if (gb0) {
            l1.setText("Steps: " + g[0].getSteps()); // Display steps
            total = total + g[0].getSteps();
            divider++;
        } else {
            l1.setText("Empty"); // Displays empty if no history
        }

        if (gb1 && g[1].getSteps() == -1) {
            l2.setText("Used Solver");
        } else if (gb1) {
            l2.setText("Steps: " + g[1].getSteps());
            total = total + g[1].getSteps();
            divider++;
        } else {
            l2.setText("Empty");
        }

        if (gb2 && g[2].getSteps() == -1) {
            l3.setText("Used Solver");
        } else if (gb2) {
            l3.setText("Steps: " + g[2].getSteps());
            total = total + g[2].getSteps();
            divider++;
        } else {
            l3.setText("Empty");
        }

        if (gb3 && g[3].getSteps() == -1) {
            l4.setText("Used Solver");
        } else if (gb3) {
            l4.setText("Steps: " + g[3].getSteps());
            total = total + g[3].getSteps();
            divider++;
        } else {
            l4.setText("Empty");
        }

        averageLabel.setText("Average: " + total/divider);

        for (int cc = 1; cc < 6; cc++) {
            for (int rr = 0; rr < 7; rr++) {
                if (gb0) {
                    int[][] g1 = g[0].getGrid();// Retrieve grid from HistoricGrid
                    if (g1[cc][rr] == 1) { // Check for boulder in place
                        grid1[rr][cc].setColor(Color.RED);
                    } else {
                        grid1[rr][cc].setColor(Color.WHITE); // If no boulder, set as white -> Overwrite existing grid
                    }
                } else grid1[rr][cc].setColor(Color.DARK_GRAY); // Makes grid Dark Gray if no history

                if (gb1) {
                    int[][] g2 = g[1].getGrid();
                    if (g2[cc][rr] == 1) {
                        grid2[rr][cc].setColor(Color.RED);
                    } else {
                        grid2[rr][cc].setColor(Color.WHITE);
                    }
                } else grid2[rr][cc].setColor(Color.DARK_GRAY);

                if (gb2) {
                    int[][] g3 = g[2].getGrid();
                    if (g3[cc][rr] == 1) {
                        grid3[rr][cc].setColor(Color.RED);
                    } else {
                        grid3[rr][cc].setColor(Color.WHITE);
                    }
                } else grid3[rr][cc].setColor(Color.DARK_GRAY);

                if (gb3) {
                    int[][] g4 = g[3].getGrid();
                    if (g4[cc][rr] == 1) {
                        grid4[rr][cc].setColor(Color.RED);
                    } else {
                        grid4[rr][cc].setColor(Color.WHITE);
                    }
                } else grid4[rr][cc].setColor(Color.DARK_GRAY);
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == solveButton) { // When solve button is pressed
            hintsUsed = -1; // set hintsUsed to -1 to display it as solved when using "Historic Grid"
            removeButtonSettings(); // Reset button settings
            inputGrid(); // Input the user input into a grid to solve
            Solver solve = new Solver(gridToSolve); // Initialize solver
            if (solve.bfs()) { // Check if grid can be solved
                solve.showSolution(); // Show solution
                JOptionPane.showMessageDialog(buttonGrid, "Solvable");
            } else {
                JOptionPane.showMessageDialog(buttonGrid, "No Solution");
            }
        } else if (e.getSource() == hintsButton) {
            inputGrid(); // input the map into gridToSolve
            if (allHintsDisplayed) return; // Prevents user from adding hint count when all hints displayed
            allHintsDisplayed = false;
            Solver solve = new Solver(gridToSolve); // Initialize Solver
            if (solve.bfs()) { // Check if solvable
                if (steps == null) steps = solve.getSteps(); // Retrieve queue of steps
                if (!steps.isEmpty()) {
                    Solver.Move m = steps.pop(); // Dequeues from the steps queue
                    stepsNum++; // Increase the hints used count by one
                    if (hintsUsed != -1) hintsUsed = stepsNum; // If solver is used, user save hints used number
                    GUI.grid[m.p.y][m.p.x - 1].setTextOnButton(String.valueOf(stepsNum)); // Displays the number of steps on button
                    try {
                        switch (m.direction) { // Displays image on the button
                            case "U" ->
                                GUI.grid[m.p.y][m.p.x - 1].setIcon(new ImageIcon(ImageIO.read(Objects.requireNonNull(getClass().getResource("/resources/up.png")))));
                            case "D" ->
                                GUI.grid[m.p.y][m.p.x - 1].setIcon(new ImageIcon(ImageIO.read(Objects.requireNonNull(getClass().getResource("/resources/down.png")))));
                            case "L" ->
                                GUI.grid[m.p.y][m.p.x - 1].setIcon(new ImageIcon(ImageIO.read(Objects.requireNonNull(getClass().getResource("/resources/left.png")))));
                            case "R" ->
                                GUI.grid[m.p.y][m.p.x - 1].setIcon(new ImageIcon(ImageIO.read(Objects.requireNonNull(getClass().getResource("/resources/right.png")))));
                        }
                    } catch (Exception ignored) {}
                } else {
                    JOptionPane.showMessageDialog(buttonGrid, "All steps displayed"); // Resets once all steps displayed
                    steps = null;
                    stepsNum = 0;
                    allHintsDisplayed = true;
                }
            } else {
                JOptionPane.showMessageDialog(buttonGrid, "No Solution");
            }
        } else if (e.getSource() == historyButton) {
            main.setVisible(false);
            history.setVisible(true);
            updateHistoryFrame();
        } else if (e.getSource() == resetButton) {
            resetGrid();
        } else if (e.getSource() == trackerButton) {
            inputGrid();
            System.out.println(hintsUsed);
            SaveLoad.save(gridToSolve, hintsUsed, ROWS);
        } else if (e.getSource() == backButton) {
            main.setVisible(true);
            history.setVisible(false);
        }
    }

    public void inputGrid() {
        for (int y = 0; y < COL; y++) {
            for (int x = 0; x < ROWS; x++) {
                if (grid[x][y].isPressed()) {
                    gridToSolve[y + 1][x] = 1;
                } else {
                    gridToSolve[y + 1][x] = 0;
                }
            }
        }
    }

    public void resetGrid() {
        if (steps!=null) steps.clear();
        allHintsDisplayed = false;
        stepsNum = 0;
        hintsUsed = 0;
        for (int y = 0; y < COL; y++) {
            for (int x = 0; x < ROWS; x++) {
                if (y != 6) grid[x][y].setColor(Color.WHITE);
                grid[x][y].setIcon((Icon) null);
                grid[x][y].setPressed(false);
                grid[x][y].setTextOnButton(null);
                if (y == 0) grid[x][y].setColor(Color.green);
            }
        }
    }

    public void removeButtonSettings() {
        for (int y = 0; y < COL; y++) {
            for (int x = 0; x < ROWS; x++) {
                grid[x][y].setIcon((Icon) null);
                grid[x][y].setTextOnButton(null);
                if (grid[x][y].isPressed()) grid[x][y].setBoulderColor();
            }
        }
    }

    private boolean boardUpdated(int[][] g) {
        for (int y = 0; y < COL; y++) {
            for (int x = 0; x < ROWS; x++) {
                if (grid[x][y].isPressed()) {
                    if (g[y + 1][x] != 1) return true;
                } else {
                    if (g[y + 1][x] != 0) return true;
                }
            }
        }
        return false;
    }
}