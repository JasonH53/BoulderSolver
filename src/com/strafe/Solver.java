package com.strafe;

import java.awt.*;
import java.util.*;

public class Solver {

    private int[][] grid;
    private Queue<Route> routes;
    private TreeSet<Route> solvedRoutes;
    private Set<Long> visited;
    private Route solvedRoute;
    private int time;
    private int solvedPaths = 0;
    private boolean invalidPush;
    private Map<Long, Queue<Move>> cachedRoute;

    public Solver(int [][] solveGrid) {
        this.grid = solveGrid;
        this.routes = new ArrayDeque<Route>();
        this.solvedRoutes = new TreeSet();
        this.visited = new HashSet<Long>();
        this.solvedRoute = null;
        this.invalidPush = true;
        this.cachedRoute = new HashMap<Long, Queue<Move>>();
        this.time = 5000;
    }

    public void showSolution() {
        if (solvedRoute != null) {
            Queue<Move> moveList = solvedRoute.moveList;
            while (!moveList.isEmpty()) {
                Move m = moveList.poll();
                switch (m.offsetType) {
                    case 1:
                        System.out.println("up" + " " + m.p.x + " " + (m.p.y + 1));
                        ButtonGrid.grid[m.p.y][m.p.x-1].setColor(Color.pink);
                        break;
                    case 2:
                        System.out.println("down" + " " + m.p.x + " " + (m.p.y + 1));
                        ButtonGrid.grid[m.p.y][m.p.x-1].setColor(Color.pink);
                        break;
                    case 3:
                        System.out.println("left" + " " + m.p.x + " " + (m.p.y + 1));
                        ButtonGrid.grid[m.p.y][m.p.x-1].setColor(Color.pink);
                        break;
                    case 4:
                        System.out.println("right" + " " + m.p.x + " " + (m.p.y + 1));
                        ButtonGrid.grid[m.p.y][m.p.x-1].setColor(Color.pink);
                        break;
                    default:
                        break;
                }
                if (moveList.peek()!=null) System.out.println("Next Step:");
            }
        }
    }


    public boolean solve() {
        if (!invalidPush) {
            return true;
        }
        routes = new ArrayDeque<Route>();
        solvedRoutes = new TreeSet();
        visited = new HashSet<Long>();
        solvedPaths = 0;
        int[][] newGrid = copyGrid(grid);

        Route first = new Route();
        first.grid = grid;
        first.player = new Point(7, 0); // Initial location
        routes = new ArrayDeque<Route>();
        visited = new HashSet<Long>();
        routes.add(first);
        solvedRoutes = new TreeSet();
        long startTime = System.currentTimeMillis();
        while (!routes.isEmpty()) { // Iterates through possible routes
            long gridCode;
            if (System.currentTimeMillis() - startTime > (long) time) { // Returns no solution after certain time period
                if (solvedPaths != 0) break;
                return false;
            }
            if (solvedPaths > 5) break; // Prevents more than 5 solutions from created
            Route r = routes.poll();
            if (r == null || visited.contains(gridCode = getGridCode(r.grid))) continue;
            visited.add(gridCode);
            /*
             A code is generated based off the grid, then checked against the list of visited locations
            */
            Point start = r.player;
            if (start.x == 0) { // If route ends at top, adds to solved paths
                solvedRoutes.add(r);
                solvedPaths++;
                continue;
            }
            grid = r.grid;
            HashSet<Point> playerLocs = new HashSet<Point>();
            ArrayDeque<Point> pMoves = new ArrayDeque<Point>();
            pMoves.add(start);
            block1:
            while (!pMoves.isEmpty()) { // Iterates through pMoves queue
                Point loc = (Point) pMoves.poll();
                if (playerLocs.contains(loc)) continue; // Pulls player location from pMoves and checks if it already existed
                playerLocs.add(loc);
                Set<Point> moves = validMoves(loc); // Generates possible moves from location, checks if it is valid
                for (Point p1 : moves) { // Iterates through every valid move
                    if (p1.x == 0) { // Check if it reaches finishing line (x=0)
                        solvedRoutes.add(r);
                        solvedPaths++;
                        pMoves.clear();
                        continue block1;
                    }
                    if (grid[p1.x][p1.y] == 1) continue; // Checks if there is a boulder, if it does not add to possible Moves
                    pMoves.add(p1);
                }
            }
            for (Point p : playerLocs) { // Iterates through every possible position player can get to without moving a Boulder
                getValidBoulderPush(p, grid, validMoves(p), r); // Checks if there is a valid boulder movement
            }
        }
        if (solvedPaths != 0) {
            Route r = solvedRoutes.pollFirst();
            if (r == null) { // Catching error
                return false;
            }
            solvedRoute = r;
            ArrayDeque<Move> cloned = new ArrayDeque<Move>(r.moveList);
            while (!cloned.isEmpty()) { // copies the possible moves
                Move poll = (Move) cloned.poll();
                pushBoulder(poll, newGrid);
                cachedRoute.put(getGridCode(newGrid), new ArrayDeque<Move>(cloned));
            }
//            System.out.println(solvedPaths);
            return true;
        }
        return false;
    }

    private void getValidBoulderPush (Point start, int[][] grid, Set<Point> moves, Route r) {
        for (Point p : moves) {
            long gridCode;
            int[][] newGrid = copyGrid(grid);
            Move push = pushBoulder(start.x, start.y, p.x, p.y, newGrid);
            if (push == null || visited.contains(gridCode = getGridCode(newGrid))) continue;
            Route r1 = new Route(r);
            r1.moveList.add(push);
            r1.moves++;
            r1.grid = newGrid;
            r1.player = push.p;
            routes.add(r1);
        }
    }

    private void pushBoulder (Move move, int[][] grid) {
        int boxX = move.p.x;
        int boxY = move.p.y;
        if (move.offsetType == 1) {
            grid[boxX - 1][boxY] = 1;
            grid[boxX][boxY] = 0;
        } else if (move.offsetType == 2) {
            grid[boxX + 1][boxY] = 1;
            grid[boxX][boxY] = 0;
        } else if (move.offsetType == 3) {
            grid[boxX][boxY - 1] = 1;
            grid[boxX][boxY] = 0;
        } else if (move.offsetType == 4) {
            grid[boxX][boxY + 1] = 1;
            grid[boxX][boxY] = 0;
        }
    }

    private Move pushBoulder (int playerX, int playerY, int boxX, int boxY, int[][] grid) {
        if (grid[boxX][boxY] != 1) {
            return null;
        }
        if (boxX == 0 || boxX == 7) {
            return null;
        }
        if (boxX - playerX == -1) {
            if (grid[boxX - 1][boxY] == 0) {
                grid[boxX - 1][boxY] = 1;
                grid[boxX][boxY] = 0;
                Move move = new Move();
                move.p = new Point(boxX, boxY);
                move.offsetType = 1;
                return move;
            }
        } else if (boxX - playerX == 1) {
            if (grid[boxX + 1][boxY] == 0) {
                grid[boxX + 1][boxY] = 1;
                grid[boxX][boxY] = 0;
                Move move = new Move();
                move.p = new Point(boxX, boxY);
                move.offsetType = 2;
                return move;
            }
        } else if (boxY - playerY == -1) {
            if (boxY == 0) {
                return null;
            }
            if (grid[boxX][boxY - 1] == 0) {
                grid[boxX][boxY - 1] = 1;
                grid[boxX][boxY] = 0;
                Move move = new Move();
                move.p = new Point(boxX, boxY);
                move.offsetType = 3;
                return move;
            }
        } else if (boxY - playerY == 1) {
            if (boxY == 6) {
                return null;
            }
            if (grid[boxX][boxY + 1] == 0) {
                grid[boxX][boxY + 1] = 1;
                grid[boxX][boxY] = 0;
                Move move = new Move();
                move.p = new Point(boxX, boxY);
                move.offsetType = 4;
                return move;
            }
        }
        return null;
    }

    private Set<Point> validMoves (Point start) {
        HashSet<Point> out = new HashSet<Point>();
        int x = start.x;
        int y = start.y;
        if (y > 0 && y < 6) {
            out.add(new Point(x, y + 1));
            out.add(new Point(x, y - 1));
        } else if (y == 0) {
            out.add(new Point(x, y + 1));
        } else if (y == 6) {
            out.add(new Point(x, y - 1));
        }
        if (x > 0 && x < 7) {
            out.add(new Point(x + 1, y));
            out.add(new Point(x - 1, y));
        } else if (x == 0) {
            out.add(new Point(x + 1, y));
        } else if (x == 7) {
            out.add(new Point(x - 1, y));
        }
        return out;
    }

    private int[][] copyGrid (int[][] input) {
        int[][] out = new int[input.length][input[0].length];
        for (int i = 0; i < input.length; ++i) {
            System.arraycopy(input[i], 0, out[i], 0, input[0].length);
        }
        System.out.println(Arrays.deepToString(input));
        return out;
    }

    private long getGridCode (int[][] grid) {
        long sum = 0L;
        for (int[] i : grid) {
            sum = 7L * sum + (long) Arrays.hashCode(i);
        }
        return sum;
    }

    private static class Route implements Comparable<Route> {
        int[][] grid;
        Point player;
        Queue<Move> moveList = new ArrayDeque<Move>();
        int moves = 0;

        Route() {
            this.moveList = new ArrayDeque<Move>();
            this.moves = 0;
        }

        Route(Route r) {
            this.grid = r.grid;
            this.player = r.player;
            this.moveList = new ArrayDeque<Move>(r.moveList);
            this.moves = r.moves;
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof Route)) {
                return false;
            }
            Route route = (Route) o;
            return Arrays.equals((Object[]) this.grid, (Object[]) route.grid);
        }

        public int hashCode() {
            int sum = 0;
            for (int[] i : this.grid) {
                sum = 31 * sum + Arrays.hashCode(i);
            }
            return sum;
        }

        @Override
        public int compareTo(Route o) {
            return this.moves - o.moves;
        }
    }

    private static class Move {
        Point p;
        int offsetType;
        /*
        Offset Types:
        1 = Up
        2 = Down
        3 = Left
        4 = Right
         */
    }
}

