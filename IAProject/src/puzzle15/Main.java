package puzzle15;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

enum Db {
    UP, DOWN, LEFT, RIGHT
}
enum Heuristic {
	MANHATTAN, LINEAR_CONFLICTS, DISJOINT_DATABASE, DISJOINT_REFLECTED
}

public class Main {

	@SuppressWarnings("unused")
	public static void main(String[] args) throws Exception {
		
		Byte[] arr = new Byte[]
				{0,  1,  2,  3,
				 4,  5, 6,  7,
				 8,  9,  10,  11,
			     12 ,13, 14, 15};
		List<Byte> goal = Arrays.asList(arr);
		
		Database evenDb= new Database();
		Database oddDb = new Database();
		
//***********************************************************************************************************	
// Usare i comandi di create/save Database solo la prima volta!
// Spazio occupato in totale dai 2 file: 1GB + 256 MB
//////////////////////////////////////		evenDb.createDatabase(Database.codificaDown(goal), Db.DOWN);
//////////////////////////////////////		evenDb.saveDatabase("evenDB_file.dat");
//////////////////////////////////////      oddDb.createDatabase(Database.codificaUp(goal), Db.UP);
//////////////////////////////////////      oddDb.saveDatabase("oddDB_file.dat");
//Per la precompilazione dei database non è necessario eseguire il codice sottostante
//***********************************************************************************************************	
		
		evenDb.setDatabase(evenDb.loadDatabase("evenDB_file.dat"));
		oddDb.setDatabase(oddDb.loadDatabase("oddDB_file.dat"));

//***********************************************************************************************************	
// Il seguente codice, racchiuso fra 2 barre di "*******..." commentate,
// serve per utilizzare i vari algoritmi risolutivi su puzzle inseriti manualmente,
// Se si desidera stampare su console i puzzle della soluzione ottimale di ogni problema
// basta "de-commentare" la chiamata del metodo PrintPath(current); nella Classe Problem -> solveProblem(...)
		
//			Byte[] arr2 = new Byte[]
//					{14,  1,  7,  15,
//					 9,  8, 0,  13,
//					 5,  2,  10,  11,
//				     12 ,6, 4, 3};
//			List<Byte> test2 = Arrays.asList(arr2);
//
//					Byte[] arr3 = new Byte[]
//							{0,  13,  12,  3,
//							 4,  5,  6,  7,
//							 9,  8,  11, 10,
//						      1, 2, 15, 14};
//					List<Byte> test3 = Arrays.asList(arr3);
//							
//							Byte[] arr4 = new Byte[]
//									{0,  2,  5,  3,
//									 6,  1, 7,  4,
//									 11,  9,  8,  10,
//								     13 ,15, 14, 12};
//							List<Byte> test4 = Arrays.asList(arr4);
 
//		Node starter = new Node(goal);
//		Node starter2 = new Node(test2);
//		Node starter3 = new Node(test3);
//		Node starter4 = new Node(test4);
//        Problem problem1 = new Problem(starter);
//        Problem problem2 = new Problem(starter2); 
//        Problem problem3 = new Problem(starter3);
//        Problem problem4 = new Problem(starter4); 
    
// In questo esempio verrà risolto il problem2 con ogni modalità di euristica
		
//        problem2.solveProblem(Heuristic.MANHATTAN,oddDb,evenDb);
//        problem2.solveProblem(Heuristic.LINEAR_CONFLICTS,oddDb,evenDb);
//        problem2.solveProblem(Heuristic.DISJOINT_DATABASE,oddDb,evenDb);
//        problem2.solveProblem(Heuristic.DISJOINT_REFLECTED,oddDb,evenDb);
//***********************************************************************************************************	 
		
//Il codice sottostante serve per testare gli algoritmi su numerosi problemi generati random al fine
//di ottenere i dati necessari per compilare la tabella conclusiva del progetto

//La variabile sottostante int numberOfProblems è responsabile del numero di problemi generati
//che il programma dovrà risolvere. Modificare a piacere ma è sconsigliabile numeri grandi in quanto
// la risoluzione di ogni singolo problema può richiedere fino anche a qualche minuto
		
        int numberOfProblems=3;
        RandomPuzzlesGenerator generator= new RandomPuzzlesGenerator();
        List<Problem> problems = generator.randomProblemArray(numberOfProblems);
        
int i=0;
int value= 0;
long nodes = 0;
double seconds= 0;
double nodes_sec= 0;
int unresolvedProblems=0;
List<Number> temporary;
List<Number> data = new ArrayList<>();
data.add(value); data.add(nodes); data.add(seconds); data.add(nodes_sec);

for(Problem problem: problems) {
	temporary=problem.solveProblem(Heuristic.DISJOINT_REFLECTED, oddDb, evenDb);
	if(temporary==null) {
		System.out.println("Problem failed, (too much memory used): "+ ++i +"/"+numberOfProblems);
		unresolvedProblems++; 
		System.gc();
		Thread.sleep(1_000);
		continue;
	}
	data.set(0, data.get(0).intValue()+temporary.get(0).intValue());
	data.set(1, data.get(1).longValue()+temporary.get(1).longValue());
	data.set(2, data.get(2).doubleValue()+temporary.get(2).doubleValue());
	System.out.println("Problems solved: "+ ++i +"/"+numberOfProblems);
	System.gc();
	Thread.sleep(1_000);
}

int solvedProblems=numberOfProblems-unresolvedProblems;
data.set(3, data.get(1).longValue()/data.get(2).doubleValue());
data.set(0, data.get(0).doubleValue()/solvedProblems);
data.set(1, data.get(1).longValue()/solvedProblems);
data.set(2, data.get(2).doubleValue()/solvedProblems);

System.out.println("solvedProblems: "+solvedProblems);
System.out.println("unresolvedProblems: "+unresolvedProblems);
System.out.println("Average value: "+data.get(0).doubleValue());
System.out.println("Average nodes: "+data.get(1).longValue());
System.out.println("Average seconds: "+data.get(2).doubleValue());
System.out.println("Average nodes/sec: "+data.get(3).doubleValue());

	}
}