package puzzle15;

import static java.lang.Math.*;
import static puzzle15.Node.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.Integer;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class Database {
	private byte[] database = null;

	public byte[] getDatabase() {
		return database;
	}

	public void setDatabase(byte[] database) {
		this.database = database;
	}

	public byte[] loadDatabase(String filePath) {
		File file = new File(filePath);
		int size = (int) file.length();
		byte[] output = new byte[size];
		try {
			FileInputStream sorgente = new FileInputStream(new File(filePath));
			sorgente.read(output);
			sorgente.close();
			file = null;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return output;
	}

	public void saveDatabase(String filePathOutput) {
		ByteBuffer buffer = ByteBuffer.allocateDirect(this.database.length);
		buffer.put(this.database);
		File file = new File(filePathOutput);
		try (RandomAccessFile stream = new RandomAccessFile(file, "rw")) {
			FileChannel channel = stream.getChannel();
			buffer.flip();
			channel.write(buffer);
		}

		catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void createDatabase(int goal, Db databaseType) {
		int size = 0;
		if (databaseType == Db.LEFT || databaseType == Db.UP)
			size = (int) pow(2, 28);
		if (databaseType == Db.RIGHT || databaseType == Db.DOWN)
			size = (int) pow(2, 30);

		this.database = new byte[size];
		System.out.println("Creazione database in corso: " + databaseType);
		MyQueue frontiera = new MyQueue((int) pow(2, 28));
		frontiera.add(goal);
		int contatore = 0;
		int cicliFatti = 0;
		this.database[goal] = (byte) 0;

		while (!frontiera.isEmpty()) {
			contatore++;
			if (contatore == 5_000_000) {
				cicliFatti += 5;
				System.out.println(cicliFatti + " milioni di nodi estratti dalla frontiera");
				System.out.println("frontiera size: " + frontiera.size());
				contatore = 0;
			}
			int current = frontiera.poll();

			frontiera.addAll(generaFigliNonEsplorati(current, databaseType));
		}
		this.database[goal] = 0;
		frontiera = null;
	}

	public List<Integer> generaFigliNonEsplorati(int hashCodeParent, Db databaseType) {
		List<Integer> result = new ArrayList<>();
		Byte parentCost = database[hashCodeParent];
		List<Byte> puzzle = decodifica(hashCodeParent, databaseType);
		int hashCodeChild;
		for (int i = 0; i < puzzle.size(); i++) {
			if (puzzle.get(i) == 0)
				continue;

			if (i - 4 >= 0 && puzzle.get(i - 4) == 0) {
				Collections.swap(puzzle, i, i - 4);
				hashCodeChild = codifica(puzzle, databaseType);
				if (database[hashCodeChild] == 0) {
					result.add(hashCodeChild);
					database[hashCodeChild] = (byte) (parentCost + 1);
				}
				Collections.swap(puzzle, i, i - 4);
			}
			if (i + 4 <= 15 && puzzle.get(i + 4) == 0) {
				Collections.swap(puzzle, i, i + 4);
				hashCodeChild = codifica(puzzle, databaseType);
				if (database[hashCodeChild] == 0) {
					result.add(hashCodeChild);
					database[hashCodeChild] = (byte) (parentCost + 1);
				}
				Collections.swap(puzzle, i, i + 4);
			}
			if ((i % 4) != 0 && puzzle.get(i - 1) == 0) {
				Collections.swap(puzzle, i, i - 1);
				hashCodeChild = codifica(puzzle, databaseType);
				if (database[hashCodeChild] == 0) {
					result.add(hashCodeChild);
					database[hashCodeChild] = (byte) (parentCost + 1);
				}
				Collections.swap(puzzle, i, i - 1);
			}
			if ((i + 1) % 4 != 0 && puzzle.get(i + 1) == 0) {
				Collections.swap(puzzle, i, i + 1);
				hashCodeChild = codifica(puzzle, databaseType);
				if (database[hashCodeChild] == 0) {
					result.add(hashCodeChild);
					database[hashCodeChild] = (byte) (parentCost + 1);
				}
				Collections.swap(puzzle, i, i + 1);
			}
		}
		return result;
	}

	public static int codifica(List<Byte> puzzle, Db databaseType) {
		switch (databaseType) {
		case DOWN:
			return Database.codificaDown(puzzle);

		case UP:
			return Database.codificaUp(puzzle);

		case LEFT:
			return Database.codificaLeft(puzzle);

		case RIGHT:
			return Database.codificaRight(puzzle);
		}
		System.out.println("Inserita tipologia di database non implementata");
		return -1;
	}

	public static int codificaDown(List<Byte> puzzle) {
		// codifico il puzzle con 30 bit, i 20 bit meno significativi contengono le
		// coordinate dei numeri da 8 a 12 (4 bit per ciascuno),
		// invece i 10 bit più significativi codificano le posizioni del 13, 14 e del 15
		// con un numero che va da 0 a 989.

		int result = 0;
		int n13 = 0; // numero caselle vuote prima del 13 (numero compreso fra 0-10)
		int n14 = 0; // numero caselle vuote prima del 14 (numero compreso fra 0-10)
		int n15 = 0; // numero caselle vuote prima del 15 (numero compreso fra 0-10)
		int nBlank = 0; // numero di caselle vuote finora (senza i numeri 8-13) , ovvero
		// indice della pos di 14 tra le 10 caselle (quelle senza i numeri 8-13) che
		// possono contenere il 14 o il 15

		for (int i = 0; i < puzzle.size(); i++) {
			int contenuto = puzzle.get(i);
			if (contenuto == 13) {
				n13 = nBlank++;
				continue;
			} // n13 è l'indice di 13 tra le 11 caselle che non contengono i numeri da 8 a 12
			if (contenuto == 14) {
				n14 = nBlank++;
				continue;
			} // n14 è l'indice di 14 tra le 11 caselle che non contengono i numeri da 8 a 12
			if (contenuto == 15) {
				n15 = nBlank++;
				continue;
			} // n15 è l'indice di 15 tra le 11 caselle che non contengono i numeri da 8 a 12
			if (contenuto < 8) {
				nBlank++;
				continue;
			} // casella vuota
			int x = x(i);
			int y = y(i);
			int offset = 4 * (contenuto - 8);
			result += (x << (offset + 2)) + (y << offset);
		}

		if (n15 < n14)
			n14--; // ora n14 è compreso fra 0-9 (non considera più la casella occupata da n15)
		if (n15 < n13)
			n13--;
		if (n14 < n13)
			n13--; // ora n13 è compreso fra 0-8 (non considera più le caselle occupate da n14 e
					// n15)

		int supporter = n15 * 90 + n14 * 9 + n13; // supporter è i 10 bit di codifica del 13,14,15; numero compreso fra
													// 0-989
		result += supporter << 20;
		return result;
	}

	public static int codificaUp(List<Byte> puzzle) {
		int result = 0;
		for (int i = 0; i < puzzle.size(); i++) {
			int contenuto = puzzle.get(i);
			if (contenuto > 7 || contenuto == 0)
				continue;
			int x = x(i);
			int y = y(i);
			int molt = (int) pow(16, contenuto - 1);
			result += (int) (x * molt * 4 + y * molt);
			// System.out.print(contenuto);
			// System.out.println(" x: "+x+" y: "+y+ " molt: "+molt);
		}
		return result;
	}

	public static int codificaRight(List<Byte> puzzle) {
		// codifico andando ad applicare una riflessione sulla diagonale rispetto alla
		// codifica dell'hashDown

		List<Byte> reflected_puzzle = new ArrayList<Byte>(16);
		for (int j = 0; j < 16; j++) {
			reflected_puzzle.add((byte) 0);
		}
		Set<Byte> numeriValidi = new HashSet<Byte>();
		numeriValidi.addAll(Arrays.asList(new Byte[] { 14, 10, 6, 2, 15, 11, 7, 3 }));
		int reflected_index = 0;
		for (int i = 0; i < 16; i++) {
			if (!numeriValidi.contains(puzzle.get(i))) {
				continue;
			}
			if (i < 4) {
				reflected_index = i * 4;
			}
			if (i >= 4 && i < 8) {
				reflected_index = ((i % 4) * 4) + 1;
			}
			if (i >= 8 && i < 12) {
				reflected_index = ((i % 4) * 4) + 2;
			}
			if (i >= 12 && i < 16) {
				reflected_index = ((i % 4) * 4) + 3; // trasformo l'indice rispettivamente dela 1°,2°,3°,4° riga
			} // nella 1°,2°,3°,4° colonna
			switch (puzzle.get(i)) {
			case 2: {
				reflected_puzzle.set(reflected_index, (byte) 8);
				break;
			}
			case 3: {
				reflected_puzzle.set(reflected_index, (byte) 12);
				break;
			}
			case 6: {
				reflected_puzzle.set(reflected_index, (byte) 9);
				break;
			}
			case 7: {
				reflected_puzzle.set(reflected_index, (byte) 13);
				break;
			}
			case 10: {
				reflected_puzzle.set(reflected_index, (byte) 10);
				break;
			}
			case 11: {
				reflected_puzzle.set(reflected_index, (byte) 14);
				break;
			}
			case 14: {
				reflected_puzzle.set(reflected_index, (byte) 11);
				break;
			}
			case 15: {
				reflected_puzzle.set(reflected_index, (byte) 15);
				break;
			}
			}
			continue;
		}
		return (Database.codificaDown(reflected_puzzle));
	}

	public static int codificaLeft(List<Byte> puzzle) {

		// codifico andando ad applicare una riflessione sulla diagonale rispetto alla
		// codifica dell'hashUp

		List<Byte> reflected_puzzle = new ArrayList<Byte>(16);
		for (int j = 0; j < 16; j++) {
			reflected_puzzle.add((byte) 0);
		}
		Set<Byte> numeriValidi = new HashSet<Byte>();
		numeriValidi.addAll(Arrays.asList(new Byte[] { 1, 4, 5, 8, 9, 12, 13 }));
		int reflected_index = 0;
		for (int i = 0; i < 16; i++) {
			if (!numeriValidi.contains(puzzle.get(i))) {
				continue;
			}
			if (i < 4) {
				reflected_index = i * 4;
			}
			if (4 <= i && i < 8) {
				reflected_index = ((i % 4) * 4) + 1;
			}
			if (8 <= i && i < 12) {
				reflected_index = ((i % 4) * 4) + 2;
			}
			if (12 <= i && i < 16) {
				reflected_index = ((i % 4) * 4) + 3;
			}
			switch (puzzle.get(i)) {
			case 1: {
				reflected_puzzle.set(reflected_index, (byte) 4);
				break;
			}
			case 4: {
				reflected_puzzle.set(reflected_index, (byte) 1);
				break;
			}
			case 5: {
				reflected_puzzle.set(reflected_index, (byte) 5);
				break;
			}
			case 8: {
				reflected_puzzle.set(reflected_index, (byte) 2);
				break;
			}
			case 9: {
				reflected_puzzle.set(reflected_index, (byte) 6);
				break;
			}
			case 12: {
				reflected_puzzle.set(reflected_index, (byte) 3);
				break;
			}
			case 13: {
				reflected_puzzle.set(reflected_index, (byte) 7);
				break;
			}
			}
			continue;
		}
		return (Database.codificaUp(reflected_puzzle));

	}

	public static List<Byte> decodifica(int hashCode, Db databaseType) {
		switch (databaseType) {
		case DOWN:
			return Database.decodificaDown(hashCode);

		case UP:
			return Database.decodificaUp(hashCode);

		case LEFT:
			return Database.decodificaLeft(hashCode);

		case RIGHT:
			return Database.decodificaRight(hashCode);
		}
		System.out.println("Inserita tipologia di database non implementata");
		return null;
	}

	public static List<Byte> decodificaDown(int hashCode) {
		int mask = 0b11; // numero binario che seleziona gli ultimi 2 bit tramite un AND operatore
		List<Byte> result = new ArrayList<>(16);
		for (Byte s = 0; s < 16; s++) { // il metodo set non riconosceva il size iniziale di 16
			result.add((byte) 0);
		}
		for (Byte i = 8; i <= 12; i++) { // loop for every element of the puzzle
			int y = (hashCode & mask); // last 2 bits of the hashCode
			hashCode = hashCode >>> 2; // shift a destra di 2
			int x = (hashCode & mask);
			hashCode = hashCode >>> 2;
			result.set(x + 4 * y, i); // x+4*y is the index of the ArrayList where to put the value i
//	System.out.println("size:"+result.size());
		}

		int n15 = hashCode / 90; // 0-10
		hashCode %= 90; // 0-89
		int n14 = hashCode / 9; // 0-9
		int n13 = hashCode % 9; // 0-8

		if (n14 <= n13)
			n13++; // n13->0-9
		if (n15 <= n14)
			n14++; // n14->0-10
		if (n15 <= n13)
			n13++; // n13->0-10

		int i = 0, j = 0; // j = #caselle attraversate nel puzzle completo, i = #caselle vuote
							// attraversate
		boolean done13 = false, done14 = false, done15 = false;
		while (!done13 || !done14 || !done15) {
			if (result.get(j) != 0) {
				j++;
				continue;
			}
			i++;
			if (n13 == i - 1 && !done13) {
				result.set(j, (byte) 13);
				done13 = true;
			}
			if (n14 == i - 1 && !done14) {
				result.set(j, (byte) 14);
				done14 = true;
			}
			if (n15 == i - 1 && !done15) {
				result.set(j, (byte) 15);
				done15 = true;
			}
			j++;
		}
		return result;
	}

	public static List<Byte> decodificaUp(int hashCode) {
		int mask = 0b11; // numero binario che seleziona gli ultimi 2 bit tramite un AND operatore
		List<Byte> result = new ArrayList<>(16);
		for (Byte s = 0; s < 16; s++) { // il metodo set non riconosceva il size iniziale di 16
			result.add((byte) 0);
		}
		for (Byte i = 1; i <= 7; i++) { // loop for every element of the puzzle
			int y = (hashCode & mask); // last 2 bits of the hashCode
			hashCode = hashCode >>> 2; // shift a destra di 2
			int x = (hashCode & mask);
			hashCode = hashCode >>> 2;
			result.set(x + 4 * y, i); // x+4*y is the index of the ArrayList where to put the value i
		}

		return result;
	}

	public static List<Byte> decodificaRight(int hashCode) {
		List<Byte> reflected_puzzle = Database.decodificaDown(hashCode);
		List<Byte> puzzle = new ArrayList<Byte>(16);
		for (int j = 0; j < 16; j++) {
			puzzle.add((byte) 0);
		}
		int original_index = 0;
		for (int i = 0; i < 16; i++) {
			if (i < 4) {
				original_index = i * 4;
			}
			if (4 <= i && i < 8) {
				original_index = ((i % 4) * 4) + 1;
			}
			if (8 <= i && i < 12) {
				original_index = ((i % 4) * 4) + 2;
			}
			if (12 <= i && i < 16) {
				original_index = ((i % 4) * 4) + 3;
			}

			switch (reflected_puzzle.get(i)) {
			case 8: {
				puzzle.set(original_index, (byte) 2);
				break;
			}
			case 9: {
				puzzle.set(original_index, (byte) 6);
				break;
			}
			case 10: {
				puzzle.set(original_index, (byte) 10);
				break;
			}
			case 11: {
				puzzle.set(original_index, (byte) 14);
				break;
			}
			case 12: {
				puzzle.set(original_index, (byte) 3);
				break;
			}
			case 13: {
				puzzle.set(original_index, (byte) 7);
				break;
			}
			case 14: {
				puzzle.set(original_index, (byte) 11);
				break;
			}
			case 15: {
				puzzle.set(original_index, (byte) 15);
				break;
			}
			}
			continue;
		}
		return puzzle;
	}

	public static List<Byte> decodificaLeft(int hashCode) {
		List<Byte> reflected_puzzle = Database.decodificaUp(hashCode);
		List<Byte> puzzle = new ArrayList<Byte>(16);
		for (int j = 0; j < 16; j++) {
			puzzle.add((byte) 0);
		}
		int original_index = 0;
		for (int i = 0; i < 16; i++) {
			if (i < 4) {
				original_index = i * 4;
			}
			if (4 <= i && i < 8) {
				original_index = ((i % 4) * 4) + 1;
			}
			if (8 <= i && i < 12) {
				original_index = ((i % 4) * 4) + 2;
			}
			if (12 <= i && i < 16) {
				original_index = ((i % 4) * 4) + 3;
			}

			switch (reflected_puzzle.get(i)) {
			case 1: {
				puzzle.set(original_index, (byte) 4);
				break;
			}
			case 2: {
				puzzle.set(original_index, (byte) 8);
				break;
			}
			case 3: {
				puzzle.set(original_index, (byte) 12);
				break;
			}
			case 4: {
				puzzle.set(original_index, (byte) 1);
				break;
			}
			case 5: {
				puzzle.set(original_index, (byte) 5);
				break;
			}
			case 6: {
				puzzle.set(original_index, (byte) 9);
				break;
			}
			case 7: {
				puzzle.set(original_index, (byte) 13);
				break;
			}
			}
			continue;
		}
		return puzzle;
	}

	public static Long GeneratePuzzleKey(List<Byte> puzzle) {
		long result = 0;

		for (byte i : puzzle) {
			result = result << 4;
			result += i;
		}
		return result;
	}
}