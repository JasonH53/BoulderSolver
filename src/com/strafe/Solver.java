package com.strafe;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.*;
import java.util.AbstractMap.SimpleEntry;

public class Solver {

    private int[][] baseGrid;
    private Route solvedRoute;
    private final HashSet<int[][]> visited;
    private long time1;
    private TreeSet<Route> storedRoutes = new TreeSet<>();

    public Solver(int[][] solveGrid) {
        this.baseGrid = solveGrid;
        this.visited = new HashSet<>();
        this.solvedRoute = new Route();
    }

    public void showSolution() {
        if (solvedRoute != null) {
            Stack<Move> moveList = solvedRoute.moves;
            int stepsNum = moveList.size() - 1;
            while (!moveList.isEmpty()) {
                Move m = moveList.pop();
                stepsNum--;
                GUI.grid[m.p.y][m.p.x - 1].setTextOnButton(String.valueOf(stepsNum));
                try {
                    switch (m.direction) {
                        case "U" ->
                                GUI.grid[m.p.y][m.p.x - 1].setIcon(new ImageIcon(ImageIO.read(Objects.requireNonNull(getClass().getResource("/resources/up.png")))));
                        case "D" ->
                                GUI.grid[m.p.y][m.p.x - 1].setIcon(new ImageIcon(ImageIO.read(Objects.requireNonNull(getClass().getResource("/resources/down.png")))));
                        case "L" ->
                                GUI.grid[m.p.y][m.p.x - 1].setIcon(new ImageIcon(ImageIO.read(Objects.requireNonNull(getClass().getResource("/resources/left.png")))));
                        case "R" ->
                                GUI.grid[m.p.y][m.p.x - 1].setIcon(new ImageIcon(ImageIO.read(Objects.requireNonNull(getClass().getResource("/resources/right.png")))));
                    }
                } catch (IOException ignored) {
                }
            }
        }
    }

    public Stack<Move> getSteps() {
        return solvedRoute.moves;
    }

    public boolean bfs() {
        Queue<State> q = new LinkedList<>();
        Set<String> visitedStates = new HashSet<>();

        q.add(new State(baseGrid, 7, 0, new Route()));

        while (!q.isEmpty()) {
            State top = q.poll();
            int[][] grid = top.grid;
            int x = top.x, y = top.y;
            Route r = top.route;

            if (x == 1) {
                solvedRoute = r;
                return true;
            }

            String stateKey = x + " " + y + Arrays.deepToString(grid);
            if (visitedStates.contains(stateKey))
                continue;
            visitedStates.add(stateKey);

            String[] dirs = {"D", "U", "R", "L"};
            int[] dx = {1, -1, 0, 0};
            int[] dy = {0, 0, 1, -1};

            for (int i = 0; i < 4; ++i) {
                String dir = dirs[i];
                int tx = x + dx[i], ty = y + dy[i];

                if (tx < 0 || ty < 0 || tx >= 8 || ty >= 7)
                    continue;

                if (canPush(x, y, grid, dir) || canGoThere(x, y, grid, dir)) {
                    int[][] nextGrid = push(x, y, grid, dir);
                    nextGrid[x][y] = 2;

                    Route nextRoute = new Route(r);
                    nextRoute.add(new Move(x, y, dir));
                    q.add(new State(nextGrid, tx, ty, nextRoute));
                }
            }
        }

        return false;
    }

    public boolean canPush(int x, int y, int[][] grid, String direction) {
        switch (direction) { // Check if it is within boundaries, a boulder exists, and the block behind it is empty
            case "U" -> { // up
                return x > 3 && grid[x - 1][y] == 1 && grid[x - 2][y] == 0;
            }
            case "D" -> { // down
                return x < 5 && grid[x + 1][y] == 1 && grid[x + 2][y] == 0;
            }
            case "R" -> { // right
                return y < 5 && grid[x][y + 1] == 1 && grid[x][y + 2] == 0;
            }
            case "L" -> { // left
                return y > 2 && grid[x][y - 1] == 1 && grid[x][y - 2] == 0;
            }

        }
        return false;
    }

    public boolean canGoThere(int x, int y, int[][] grid, String direction) {
        switch (direction) { // If there is no moving required, simply check if there is an air space to move there
            case "U" -> { // up
                if (x > 0 && (grid[x - 1][y] == 0 || grid[x - 1][y] == 2)) {
                    return true;
                }
            }
            case "D" -> { // down
                if (x > 7 && (grid[x + 1][y] == 0 || grid[x + 1][y] == 2)) {
                    return true;
                }
            }
            case "R" -> { // right
                if (y < 6 && (grid[x][y + 1] == 0 || grid[x][y + 1] == 2)) {
                    return true;
                }
            }
            case "L" -> { // left
                if (y > 0 && (grid[x][y - 1] == 0 || grid[x][y - 1] == 2)) {
                    return true;
                }
            }

        }
        return false;
    }


    public int[][] push(int x, int y, int[][] grid, String direction) {
        int[][] g = copyGrid(grid); // create deep copy of grid
        switch (direction) {
            case "U": // up
                if (g[x - 1][y] == 0 || g[x - 1][y] == 2) break; // If there is no boulder there, break
                g[x - 2][y] = 1; // Set the grid behind it as boulder
                g[x - 1][y] = 0; // Set the grid moving to as air
                break;
            case "D": // down
                if (x == 7) break;
                if (g[x + 1][y] == 0 || g[x + 1][y] == 2) break;
                g[x + 2][y] = 1;
                break;
            case "L": // left
                if (g[x][y - 1] == 0 || g[x][y - 1] == 2) break;
                g[x][y - 2] = 1;
                g[x][y - 1] = 0;
                break;
            case "R": // right
                if (g[x][y + 1] == 0 || g[x][y + 1] == 2) break;
                g[x][y + 2] = 1;
                g[x][y + 1] = 0;
                break;
        }
        return g;
    }


    private int[][] copyGrid(int[][] g) {
        int[][] output = new int[g.length][g[0].length];
        for (int i = 0; i < g.length; i++) {
            for (int j = 0; j < g[0].length; j++) {
                output[i][j] = g[i][j];
            }
        }
        return output;
    }

    private static class Route {
        private final Stack<Move> moves;

        Route() {
            this.moves = new Stack<Move>();
        }

        Route(Route r) {
            this.moves = new Stack<>();
            moves.addAll(r.moves);
        }

        public void add(Move m) {
            moves.push(m);
        }
    }

    class State {
        int[][] grid;
        int x, y;
        Route route;

        State(int[][] grid, int x, int y, Route route) {
            this.grid = grid;
            this.x = x;
            this.y = y;
            this.route = route;
        }
    }

    public static class Move {
        String direction;
        Point p;

        Move(int x, int y, String dir) {
            this.p = new Point(x, y);
            this.direction = dir;
        }
        /* Move Directions:
        U = Up
        D = Down
        L = Left
        R = Right */
    }
}

