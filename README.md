# IAProject
Implementazione in java di IDA* con varie euristiche per risolvere puzzle 4x4, seguendo l'articolo (Korf and Felner, 2002).

# Euristiche utilizzate:
1. Manhattan
2. Manhattan con Linear Conflicts
3. Disjoint Databases
4. Disjoint Databases sfruttando le riflessioni

Per l'ottimale funzionamento del programma abbiamo messo a disposizione della JVM 10GB con -Xmx10G (per la sola creazione dei databse basta meno).

****************************************Classe Main***********************************************

****** Precompilazione dei databases:

Per utilizzare i due Disjoint Pattern Databases bisogna prima precompilarli su due file. Questa operazione dura circa 20 minuti.
Per far ciò si definisce il nodo goal dal quale calcolare le euristiche da memorizzare.
Assicurarsi di mettere a disposizione alla JVM abbastanza memoria con il comando -Xmx. (4-5 GB dovrebbero bastare)

Seguire i comandi: 

		Byte[] arr = new Byte[]
				{0,  1,  2,  3,
				 4,  5,  6,  7,
				 8,  9,  10, 11,
			         12, 13, 14, 15};
	List<Byte> goal = Arrays.asList(arr);
  
Database evenDb= new Database();
Database oddDb = new Database();
evenDb.createDatabase(Database.codificaDown(goal), Db.DOWN);
evenDb.saveDatabase("evenDB_file.dat");
oddDb.createDatabase(Database.codificaUp(goal), Db.UP);
oddDb.saveDatabase("oddDB_file.dat");

Dopo aver eseguito tali comandi una volta i metodi createDatabase e saveDatabase non devono più essere chiamati.

****** Caricare i database precedentemente salvati in memoria:

Una volta precompilati i Databases si possono caricare su array di byte attraverso i comandi:

	evenDb.setDatabase(evenDb.loadDatabase("evenDB_file.dat"));
	oddDb.setDatabase(oddDb.loadDatabase("oddDB_file.dat"));

****** Testare gli algoritmi su numerosi problemi risolvibili generati in maniera random:

Con il codice:

        int numberOfProblems=10;
        RandomPuzzlesGenerator generator= new RandomPuzzlesGenerator();
        List<Problem> problems = generator.randomProblemArray(numberOfProblems);

Viene generata una lista di 10 problemi random. 
E' sufficiente modificare il valore della variabile int numberOfProblems 
per scegliere il numero di problemi su cui testare gli algoritmi.

Con il codice:

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

Nel ciclo for vengono risolti i problemi grazie al metodo:

	public List<Number> solveProblem(Heuristic modalità, Database oddDb, Database evenDb)

E' sufficiente modificare l'argomento Heuristic di tale metodo per determinare con quale euristica
verranno risolti i problemi.

Tutti i dati verranno salvati in: List<Number> data = new ArrayList<>(); 
Per poi venire rielaborati e stampati a console a fine esecuzione del programma. 

****** Risolvere problemi inseriti manualmente:
E' possibile ovviamente anche chiamare il metodo solveProblem(...) anche su problemi
creati manualmente.
Nel main ci sono alcuni esempi nella 2° sezione commentata racchiusa da

//************************************************



//************************************************

 Se si desidera stampare su console i puzzle della soluzione ottimale di ogni problema
 basta "de-commentare" la chiamata del metodo PrintPath(current); nella Classe Problem -> solveProblem(...)
