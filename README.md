# The Matrix: Escaped :running_man:
An artificial intelligence software designed to simulate the character Neo from the matrix movie escaping the matrix under different circumstances.

## Description :memo:
The project is about searching for a solution in an environment that consists of the following:

1. **The world :world_map: :** a grid consisting of cells. Each cell can contain more than one object.
2. **Neo :superhero_man: :** the main character that the algorithm controls.
3. **Agents :man_pilot: :** characters that Neo should avoid in order to not lose health. 
4. **Hostages :standing_man: :** characters that Neo should save before they get turned into agents.
5. **Launching pads :door: :** pads that Neo could step on to teleport to other cells immediately.
6. **Health pills :pill: :** pills that Neo could take to restore his health.
7. **Telephone booth :telephone: :** the destination that Neo must reach in order to escape the matrix.

The main mission for Neo is to reach the telephone booth with as many hostages saved as possible.

## Approach
The following search strategies are used in order to search for a solution:

* Breadth-first search.
* Depth-first search.
* Iterative deepening search.
* Uniform-cost search.
* Greedy search with two heuristics.
* A* search with two admissible heuristics.

## Note
This is a university (GUC) course project. Course name is (CSEN 901 : Introduction to Artificial Intelligence). For a detailed description of the project and its requirements as well as the project report, please have a look at the [description](description) folder.
