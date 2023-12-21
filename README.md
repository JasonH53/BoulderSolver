# Boulder Game Solver

This program provides a solution for the puzzle game Boulder. It allows users to place boulders on a 7x7 grid and then solves the puzzle to find the optimal path to the finishing point.

## Key Features

- 7x7 grid where users can place boulders
- Solve button to find the optimal solution path
- Hints button to reveal the solution one step at a time
- Save button to store grid configurations
- History button to view previously saved maps
- Tracks the number of hints or solver uses for each map

![Sample Map](https://imgur.com/a/L55f5UQ)

![Impossible Map](https://imgur.com/a/xdkHeX4)

## How it Works

Users can place boulders on the grid by clicking squares, with the top and bottom rows remaining empty. Pressing the solve button will run a depth-first search algorithm to find the shortest path to the finish, taking into account rules like only being able to push boulders one space at a time.

Hints reveal the solution incrementally with each button press. Maps and hint usages are saved to a text file. The history feature displays the last 4 configurations along with hint data. Error handling prevents crashes from invalid inputs.

## Usage

To use the program, follow these steps:

1. Place boulders on the grid by clicking squares.
2. Click the solve button to see the optimal route.
3. Press the hints button to get hints incrementally.
4. Click "Save" to store maps.
5. View the history of past games.
   
This program provides an interactive way to learn solutions for Boulder puzzles.

## Development

The program was designed with wireframes, flowcharts, and pseudocode to plan components and logic. A depth-first search algorithm efficiently solves maps. Data is persisted to track progress. Thorough testing ensured all requirements were met.

This Boulder game solver allows users to engage with the puzzle game through an interactive interface that conveniently solves configurations and stores results.