package puzzle15;

import static java.lang.Math.abs;
import static java.util.Collections.swap;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;
import java.util.TreeMap;

public class Node {
	private List<Byte> puzzle;
	private int heuristic;
	private int g_distance;
	private int f_cost;
	private int blankTile;
	private Node parentNode;
	private boolean contenutoInFrontiera = false;

	// costruttore solo per nodo starter del problema
	public Node(List<Byte> puzzle) {
		this.puzzle = puzzle;
		this.heuristic = -1;
		this.g_distance = 0;
		this.f_cost = heuristic + g_distance;
		this.blankTile = puzzle.indexOf((byte) 0);
		this.parentNode = null;
	}

	// costruttore per i nodi figli nel caso di utilizzo dei databases
	public Node(List<Byte> puzzle, int g_distance, int heuristic, int vuoto, Heuristic modalità, Node parent,
			Database oddDb, Database evenDb) {
		this.puzzle = puzzle;
		this.g_distance = g_distance;
		this.heuristic = heuristic;
		this.blankTile = vuoto;
		this.f_cost = heuristic + g_distance;
		this.parentNode = parent;
	};

	// metodi getter/setter
	public List<Byte> getPuzzle() {
		return puzzle;
	}

	public void setPuzzle(List<Byte> puzzle) {
		this.puzzle = puzzle;
	}

	public int getHeuristic() {
		return heuristic;
	}

	public void setHeuristic(int heuristic) {
		this.heuristic = heuristic;
	}

	public int getG_distance() {
		return g_distance;
	}

	public void setG_distance(int g_distance) {
		this.g_distance = g_distance;
	}

	public int getblankTile() {
		return blankTile;
	}

	public void setBlankTile(int blankTile) {
		this.blankTile = blankTile;
	}

	public int getF_cost() {
		return f_cost;
	}

	public void setF_cost(int f_cost) {
		this.f_cost = f_cost;
	}

	public Node getParentNode() {
		return parentNode;
	}

	public void setParentNode(Node parentNode) {
		this.parentNode = parentNode;
	}

	public boolean getContenutoInFrontiera() {
		return this.contenutoInFrontiera;
	}

	public void setContenutoInFrontiera(boolean b) {
		this.contenutoInFrontiera = b;

	}

	public int buildHeuristic(Heuristic modalità, Database oddDb, Database evenDb) {
		if (modalità == Heuristic.MANHATTAN || modalità == Heuristic.LINEAR_CONFLICTS) {
			this.heuristic = this.manhattan(modalità);
		}
		if (modalità == Heuristic.DISJOINT_DATABASE) {
			this.heuristic = (evenDb.getDatabase()[Database.codifica(puzzle, Db.DOWN)]
					+ oddDb.getDatabase()[Database.codifica(puzzle, Db.UP)]);
		}
		if (modalità == Heuristic.DISJOINT_REFLECTED) {
			int cost, cost_reflect;
			cost = (evenDb.getDatabase()[Database.codifica(puzzle, Db.DOWN)]
					+ oddDb.getDatabase()[Database.codifica(puzzle, Db.UP)]);
			cost_reflect = (oddDb.getDatabase()[Database.codifica(puzzle, Db.LEFT)]
					+ evenDb.getDatabase()[Database.codifica(puzzle, Db.RIGHT)]);
			this.heuristic = Math.max(cost, cost_reflect);
		}
		return heuristic;
	}

	// metodi hashCode() and equals()
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((puzzle == null) ? 0 : puzzle.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Node other = (Node) obj;
		if (puzzle == null) {
			if (other.puzzle != null)
				return false;
		} else if (!puzzle.equals(other.puzzle))
			return false;
		return true;
	}

	// toString()
	@Override
	public String toString() {

		return getClass().getSimpleName() + "[heuristic=" + heuristic + ", g_distanza=" + g_distance + ", f_costo="
				+ f_cost + ", vuoto=" + blankTile + "]" + "\n" + "Puzzle:" + "\n" + puzzle.get(0) + " " + puzzle.get(1)
				+ " " + puzzle.get(2) + " " + puzzle.get(3) + "\n" + puzzle.get(4) + " " + puzzle.get(5) + " "
				+ puzzle.get(6) + " " + puzzle.get(7) + "\n" + puzzle.get(8) + " " + puzzle.get(9) + " "
				+ puzzle.get(10) + " " + puzzle.get(11) + "\n" + puzzle.get(12) + " " + puzzle.get(13) + " "
				+ puzzle.get(14) + " " + puzzle.get(15);
	}

	// coordinate casella
	public static int x(int casella) {
		return casella % 4;
	}

	public static int y(int casella) {

		if (casella == 0 || casella == 1 || casella == 2 || casella == 3)
			return 0;
		if (casella == 4 || casella == 5 || casella == 6 || casella == 7)
			return 1;
		if (casella == 8 || casella == 9 || casella == 10 || casella == 11)
			return 2;
		else
			return 3;
	}

	// manhattanDistance with or whithout linearConflicts algorithm
	public int manhattan(Heuristic modalità) {
		// non conto la distanza del blank tile dalla casella 0
		int result = 0;

		for (int tileIndex = 0; tileIndex < puzzle.size(); tileIndex++) {
			int contenuto1 = puzzle.get(tileIndex);
			if (contenuto1 == 0)
				continue;
			else {
				int x_tileIndex = x(tileIndex);
				int x_contenuto1 = x(contenuto1);
				int y_tileIndex = y(tileIndex);
				int y_contenuto1 = y(contenuto1);
				result += abs(x_tileIndex - x_contenuto1) + abs(y_tileIndex - y_contenuto1);

				if (modalità == Heuristic.LINEAR_CONFLICTS) {

					int contenuto2 = puzzle.get(contenuto1);
					if (contenuto2 == 0)
						continue;
					boolean contenutoUgualeIndice = tileIndex == contenuto1;
					if (contenutoUgualeIndice)
						continue;

					boolean sameRow = y_tileIndex == y_contenuto1;
					boolean sameColumn = x_tileIndex == x_contenuto1;
					boolean sameRowOrColumn = sameRow || sameColumn;
					int tileIndex2 = puzzle.indexOf((byte) tileIndex);

					if (contenuto1 == tileIndex2 && sameRowOrColumn && tileIndex == contenuto2) {
						result += 1;
					}
				}
			}
		}
		return result;
	}

	// ancestors nodes
	public List<Node> getPath() {
		Node current = this;
		List<Node> path = new LinkedList<Node>();
		while (current != null) {
			path.add(current);
			current = current.getParentNode();
		}
		return path;
	}

	// children nodes
	public List<Node> findChildren(Heuristic modalità, Database oddDb, Database evenDb, int limit) {
		List<Node> children = new ArrayList<>();
		int v = this.getblankTile();
		int x = x(v);
		int y = y(v);
		List<Byte> puzzle1;
		int h_cost;
		int g = this.getG_distance();

		if ((x + 1) <= 3) {
			swap(this.getPuzzle(), v, v + 1);
			h_cost = buildHeuristic(modalità, oddDb, evenDb);
			swap(this.getPuzzle(), v, v + 1);
			if (h_cost + g + 1 <= limit) {

				puzzle1 = new ArrayList<>(this.getPuzzle());
				swap(puzzle1, v, v + 1);
				children.add(new Node(puzzle1, g + 1, h_cost, v + 1, modalità, this, oddDb, evenDb));
			}
		}
		if ((x - 1) >= 0) {
			swap(this.getPuzzle(), v, v - 1);
			h_cost = buildHeuristic(modalità, oddDb, evenDb);
			swap(this.getPuzzle(), v, v - 1);
			if (h_cost + g + 1 <= limit) {

				puzzle1 = new ArrayList<>(this.getPuzzle());
				swap(puzzle1, v, (v - 1));
				children.add(new Node(puzzle1, g + 1, h_cost, v - 1, modalità, this, oddDb, evenDb));
			}
		}
		if ((y + 1) <= 3) {
			swap(this.getPuzzle(), v, v + 4);
			h_cost = buildHeuristic(modalità, oddDb, evenDb);
			swap(this.getPuzzle(), v, v + 4);
			if (h_cost + g + 1 <= limit) {

				puzzle1 = new ArrayList<>(this.getPuzzle());
				swap(puzzle1, v, (v + 4));
				children.add(new Node(puzzle1, g + 1, h_cost, v + 4, modalità, this, oddDb, evenDb));
			}
		}
		if ((y - 1) >= 0) {
			swap(this.getPuzzle(), v, v - 4);
			h_cost = buildHeuristic(modalità, oddDb, evenDb);
			swap(this.getPuzzle(), v, v - 4);
			if (h_cost + g + 1 <= limit) {

				puzzle1 = new ArrayList<>(this.getPuzzle());
				swap(puzzle1, v, (v - 4));
				children.add(new Node(puzzle1, g + 1, h_cost, v - 4, modalità, this, oddDb, evenDb));
			}
		}
		puzzle1 = null;
		return children;
	}

	public static void printPuzzle(List<Byte> puzzle) {
		System.out.println(puzzle.get(0) + " " + puzzle.get(1) + " " + puzzle.get(2) + " " + puzzle.get(3) + "\n"
				+ puzzle.get(4) + " " + puzzle.get(5) + " " + puzzle.get(6) + " " + puzzle.get(7) + "\n" + puzzle.get(8)
				+ " " + puzzle.get(9) + " " + puzzle.get(10) + " " + puzzle.get(11) + "\n" + puzzle.get(12) + " "
				+ puzzle.get(13) + " " + puzzle.get(14) + " " + puzzle.get(15));
	}

	public static void printUpCompressedPuzzle(int puzzle) {
		printPuzzle(Database.decodificaUp(puzzle));
	}

	public static void printDownCompressedPuzzle(int puzzle) {
		printPuzzle(Database.decodificaDown(puzzle));
	}

	public static void printLeftCompressedPuzzle(int puzzle) {
		printPuzzle(Database.decodificaLeft(puzzle));
	}

	public static void printRightCompressedPuzzle(int puzzle) {
		printPuzzle(Database.decodificaRight(puzzle));
	}

	public void update(Stack<Node> frontiera, TreeMap<Long, Node> explored, Node parent, Node oldNode) {

		int newG = this.getG_distance();
		oldNode.setG_distance(newG);
		int oldH = oldNode.getHeuristic();
		oldNode.setF_cost(newG + oldH);
		oldNode.setParentNode(parent);

		if (!oldNode.getContenutoInFrontiera()) {
			frontiera.push(oldNode);
			oldNode.setContenutoInFrontiera(true);
		}
	}
}