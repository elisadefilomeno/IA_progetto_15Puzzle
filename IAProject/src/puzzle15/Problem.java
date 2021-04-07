package puzzle15;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;
import java.util.TreeMap;

public class Problem {

	private Node starter;
	private Stack<Node> frontiera;
	private TreeMap<Long, Node> explored;

	public Problem(Node starter) {
		this.starter = starter;
		this.frontiera = new Stack<>();
		this.explored = new TreeMap<>();
	}

	public Node getStarter() {
		return starter;
	}

	public void PrintPath(Node nodo) {
		List<Node> pathLinkedList = nodo.getPath();
		Collections.reverse(pathLinkedList);
		System.out.println(pathLinkedList);
	}

	public boolean isSolvable(Node starter) {
		// If N is even, in this case N=4, puzzle instance is solvable if:
		// - the blank is on an even row counting from the the top (0 row and and 2 row)
		// and number of inversions is even.
		// - the blank is on an odd row counting from the top (1 row and and 3 row) and
		// number of inversions is odd.
		// For all other cases, the puzzle instance is not solvable.

		List<Byte> puzzle = starter.getPuzzle();
		int numberInversions = 0;
		int gridWidth = (int) Math.sqrt(puzzle.size()); // gridWidth==4
		int blankRow = Node.y(starter.getblankTile()); // the row with the blank tile

		for (int i = 0; i < puzzle.size(); i++) {
			for (int j = i + 1; j < puzzle.size(); j++) {
				if (puzzle.get(i) > puzzle.get(j) && puzzle.get(j) != 0) {
					numberInversions++;
				}
			}
		}
		if (gridWidth % 2 == 0) { // even grid
			if (blankRow % 2 == 0) { // blank on even row (0 row or 2 row); counting from top
				return (numberInversions % 2) == 0; // number of inversions is even
			} else { // blank on odd row (1 row or 3 row); counting from top
				return (numberInversions % 2) != 0; // number of inversions is odd
			}
		} else { // odd grid
			return (numberInversions % 2) == 0;
		}
	}

	public List<Number> solveProblem(Heuristic modalità, Database oddDb, Database evenDb) {

		List<Number> result = new ArrayList<>();
		long timeStart = System.currentTimeMillis();
		System.out.println(modalità);
		int a = this.starter.buildHeuristic(modalità, oddDb, evenDb);
		this.starter.setHeuristic(a);
		this.starter.setG_distance(0);
		this.starter.setF_cost(starter.getG_distance() + starter.getHeuristic());
		if (!isSolvable(this.starter)) {
			System.out.println("puzzle non risolvibile!");
			return null;
		}
		int numberNodesExplored = 0;
		int limit = starter.getF_cost() - 2;
		boolean found = false;
		boolean AlreadyExplored;
		Node nodeAlreadyExplored;
		Node current;
		long timeStop;
		long elapsedTime;
		double elapsedTimeInSecond;
		int all_nodes = 0;
		Long key;

		while (!found) {
			frontiera.clear();
			explored.clear();
			frontiera.push(starter);
			limit += 2;
			while (!frontiera.isEmpty()) {
				numberNodesExplored++;

//ogni elemento in explored occupa circa 200 Byte di spazio, il limite di 40 milioni è circa il massimo
//consigliato permettendo alla JVM di usare 10GB con il comando -Xmx10G .
//se si usa un quantitativo minore di memoria per il corretto funzionamento del programma 
//è consigliabile ridurre adeguatamente il numero di elemnti massimi consentiti in explored
				if (explored.size() > 40_000_000) {
					frontiera.clear();
					explored.clear();
					return null;
				}

				current = frontiera.pop();
				current.setContenutoInFrontiera(false);

				if (current.getHeuristic() == 0) {
					found = true;
//Il sottostante comando PrintPath(current) stampa su console tutti i puzzle della soluzione ottimale,
//è sconsigliato di usarlo se si risolvono numerosi problemi in un' unica esecuzione		

					// PrintPath(current);
					System.out.println("Trovato! Passi:" + current.getG_distance());
					break;
				}

				for (Node child : current.findChildren(modalità, oddDb, evenDb, limit)) {
//          I nodi generati da findChildren(...) rispettano già il limite imposto sul F_cost

					key = Database.GeneratePuzzleKey(child.getPuzzle());
					nodeAlreadyExplored = explored.get(key);
					AlreadyExplored = nodeAlreadyExplored != null;

					if (AlreadyExplored) {

						if (child.getG_distance() < nodeAlreadyExplored.getG_distance()) {
							child.update(frontiera, explored, current, nodeAlreadyExplored);
						}
						continue;
					}

					// se il successore non è in explored
					frontiera.push(child);
					child.setContenutoInFrontiera(true);
					explored.put(key, child);
					child = null;
				} // fine del for sui figli
			} // fine del while più interno
				// System.out.println("limite: " + limit);
		} // fine while esterno

		timeStop = System.currentTimeMillis();
		elapsedTime = timeStop - timeStart;
		elapsedTimeInSecond = (double) elapsedTime / 1000;

//		System.out.println("Nodi esplorati:" + numberNodesExplored);
//		System.out.println("tutti i nodi : "+all_nodes);
//		System.out.println("Solved in: " + elapsedTimeInSecond + " seconds");
//		System.out.println("*****************************************");
		result.add(this.starter.getHeuristic());
		result.add(numberNodesExplored);
		result.add(elapsedTimeInSecond);
		result.add(all_nodes / elapsedTimeInSecond);
		frontiera = null;
		explored = null;
		nodeAlreadyExplored = null;
		current = null;
		return result;
	} // fine metodo solveProblem
} // fine classe