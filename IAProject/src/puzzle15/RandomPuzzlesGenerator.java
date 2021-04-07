package puzzle15;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RandomPuzzlesGenerator {
	Problem problem;

	public List<Byte> randomPuzzle() {
		List<Byte> puzzle = new ArrayList<>();
		for (byte i = 0; i < 16; i++)
			puzzle.add(i);
		Collections.shuffle(puzzle);
		return puzzle;
	}

	public Node randomNode() {
		return new Node(randomPuzzle());
	}

	public Problem randomProblem() {
		return new Problem(randomNode());
	}

	public List<Problem> randomProblemArray(int size) {
		int i = 0;
		List<Problem> problems = new ArrayList<>();
		while (i < size) {
			problem = randomProblem();
			if (problem.isSolvable(problem.getStarter())) {
				problems.add(problem);
				i++;
			}
		}
		return problems;
	}
}
