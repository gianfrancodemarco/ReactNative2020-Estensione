package tree;

import data.Attribute;
import data.ContinuousAttribute;
import data.Data;
import data.DiscreteAttribute;
import server.NodeSocket;
import server.UnkownValueException;

import java.io.*;
import java.util.List;
import java.util.TreeSet;

/**
 * Classe che modella l'albero di regressione.
 * In base al dataset fornito in input al costruttore, crea ricorsivamente la struttura dell'albero.
 * L'albero è composto da nodi di decisione (split) che possono assumere valori discreti o continui,
 * e da nodi foglia.
 * Espone dei metodi per la stampa dell'albero generato
 *
 * @author Gianfranco Demarco
 */
public class RegressionTree implements Serializable {
	Node root;
	RegressionTree childTree[];

	public RegressionTree(){	}

	/**
	 * Costruttore della classe. Genera la struttura dell'albero.
	 * @param trainingSet Il training set di classe Data
	 */
	public RegressionTree(Data trainingSet){
		learnTree(trainingSet, 0, trainingSet.getNumberOfExamples()-1, trainingSet.getNumberOfExamples()*10/100);
	}

	/**
	 *
	 * @param trainingSet - il training set completo
	 * @param begin - indice di inizio del sottinsieme del training set da considerare
	 * @param end - indice di fine del sottinsieme del training set da considerare
	 * @param numberOfExamplesPerLeaf - numero massimo di esempi per foglia (se il numero di esempi è maggiore di numberOfExamplesPerLeaf, si procede con un nuovo split)
	 * @return true se il nodo da generare è un nodo foglia, falso altrimenti
	 */
	boolean isLeaf(Data trainingSet, int begin, int end, int numberOfExamplesPerLeaf) {
		return end - begin <= numberOfExamplesPerLeaf;
	}

	/**
	 * Invocato dal costruttore, genera ricorsivamente la struttura dell'albero
	 * A ogni chiamata ricorsiva, stabilisce se la mole di esempi da analizzare è tale da dover generare un nodo di decisione,
	 * o se puè essere generato un nodo foglia col valore predetto.
	 * Se dev'essere generato un nodo di split, richiama ricorsivamente se stesso su un sottoinsieme degli esempi del dataset
	 *
	 * @param trainingSet - il training set completo
	 * @param begin - indice di inizio del sottinsieme del training set da considerare
	 * @param end - indice di fine del sottinsieme del training set da considerare
	 * @param numberOfExamplesPerLeaf - numero massimo di esempi per foglia (se il numero di esempi è maggiore di numberOfExamplesPerLeaf, si procede con un nuovo split)
	 */
	void learnTree(Data trainingSet, int begin, int end, int numberOfExamplesPerLeaf){
		if(isLeaf(trainingSet, begin, end, numberOfExamplesPerLeaf)){
			//determina la classe che compare pi� frequentemente nella partizione corrente
			root = new LeafNode(trainingSet, begin, end);
		}
		else //split node
		{
			root = determineBestSplitNode(trainingSet, begin, end);

			if(root.getNumberOfChildren() > 1){
				childTree = new RegressionTree[root.getNumberOfChildren()];
				for(int i=0;i<root.getNumberOfChildren();i++){
					childTree[i]=new RegressionTree();
					childTree[i].learnTree(trainingSet, ((SplitNode)root).getSplitInfo(i).beginIndex, ((SplitNode)root).getSplitInfo(i).endIndex, numberOfExamplesPerLeaf);
				}
			}
			else
				root = new LeafNode(trainingSet,begin,end);

		}
	}


	/**
	 * Invocata da learnTree, restituisce il nodo discreto che minimizza la Somma dei Quadrati nel sottoinsieme del training set indicato
	 *
	 * @param trainingSet - training set completo
	 * @param begin - indice di inizio del sottinsieme del training set da considerare
	 * @param end - indice di fine del sottinsieme del training set da considerare
	 * @return bestSplitNode - SplitNode che minimizza la somma dei quadrati
	 */
	SplitNode determineBestSplitNode(Data trainingSet, int begin, int end) {

		List<Attribute> attrs = trainingSet.getExplanatoryAttributes();
		TreeSet <SplitNode> ts = new TreeSet<>();


		for (int i = 0; i < attrs.size(); i++) {
			if(attrs.get(i) instanceof DiscreteAttribute)
				ts.add(new DiscreteNode(trainingSet, begin, end, (DiscreteAttribute) attrs.get(i)));
			else
				ts.add(new ContinuousNode(trainingSet, begin, end, (ContinuousAttribute) attrs.get(i)));
		}

		trainingSet.sort(ts.first().getAttribute(), begin, end);

		return ts.first();

	}

	/**
	 * Overloading del metodo void printRules() - viene chiamato ricorsivamente sulla struttura dell'albero
	 * @param current - la stringa attualmente generata a cui viene concatenata quella ricavata dalla computazione attuale, e che viene passata alla prossima chiamata ricorsiva del metodo
	 * @return String current - La stringa in input a cui è concatenata la stringa generata dalla computazione
	 */
	private String printRules(String current) {
		Node currentNode = root;
		String finalString = "";

		if(currentNode instanceof LeafNode) { //Sono ad un nodo foglia. termino la riga e la ritorno
			return current + "  ==> Class = " + ((LeafNode) currentNode).getPredictedClassValue() + "\n";
		}  else {
			current += " AND " + ((SplitNode) currentNode).getAttribute().getName();
			for(int i = 0; i < childTree.length; i++) {
				finalString += childTree[i].printRules(current + ((SplitNode) currentNode).getSplitInfo(i).getComparator() + ((SplitNode) currentNode).getSplitInfo(i).getSplitValue());
			}
		}

		return finalString;

	}

	/**
	 * Stampa ricorsivamente le regole dell'albero di decisione e i valori predetti per ogni cammino dalla radice alle foglie.
	 * Il metodo rappresenta l'entry point per la stampa; viene poi chiamato il metodo String printRules(String current) che rappresenta overloading per questo metodo
	 */
	public String printRules() {
		Node currentNode = root;
		String current = ""; //
		String finalString = "\n********* RULES **********\n";

		//Inizializzo la stringa con l'intestazione

		if(currentNode instanceof LeafNode) {
			System.out.println(((LeafNode) currentNode).toString());
		} else {
			current += ((SplitNode) currentNode).getAttribute().getName();
			for(int i = 0; i < childTree.length; i++) { //PRIMA CHIAMATA RICORSIVA: current = "X=A" (esempio)
				//PER OGNI SPLIT, LA MIA STRINGA FINALE E' COMPOSTA DA TUTTI GLI SPLIT SU QUELL'ATTRIBUTO
				/*
				Ad esempio se plitto su X:
				la mia stringa finale � composta da tutti i nodi generati dallo split, che quindi iniziano per X=
				 */
				finalString += childTree[i].printRules(current + ((SplitNode) currentNode).getSplitInfo(i).getComparator() + ((SplitNode) currentNode).getSplitInfo(i).getSplitValue());
			}
		}

		finalString+= "*************************\n"; //aggiungo alla stringa finale la coda
		System.out.println(finalString);
		return finalString;
	}

	/**
	 * Stampa la struttura dell'albero, evidenziando ogni splitNode e gli insiemi di esempi su cui agiscono
	 */
	public void printTree(){
		System.out.println("********* TREE **********\n");
		System.out.println(toString());
		System.out.println("*************************\n");
	}

	/**
	 * Il metodo predictClass permette ad un client di predirre la classe target a partire dai dati di input avvalendosi dell'albero generato; ovver permette
	 * di navigare l'albero usando come guida dei valori di input
	 * La comunicazione avviene tramite un oggetto di classe NodeSocket
	 *
	 * Il server invia un messaggio composto da:
	 * '[END]' + il valore predetto: se la computazione ha raggiunto un nodo foglia;
	 * '[QUERY]' + i valori di split possibili altrimenti
	 *
	 * @throws IOException
	 * @throws UnkownValueException
	 *
	 * @param socket - la socket su cui deve avvenire la comunicazione
	 */
	public void predictClass(NodeSocket socket) throws IOException, UnkownValueException {

		if (root instanceof LeafNode) {
			socket.sendMessage("[END]" +  ((LeafNode) root).getPredictedClassValue().toString());
		} else {
			int risp;
			socket.sendMessage("[QUERY]"+((SplitNode) root).formulateQuery());
			risp = Integer.parseInt(socket.readMessage());

			if(risp == -1)
				throw new InterruptedIOException();

			if (risp >= root.getNumberOfChildren())
				throw new UnkownValueException("The answer should be an integer between 0 and " + (root.getNumberOfChildren() - 1) + "!");
			else
				childTree[risp].predictClass(socket);
		}
	}



	public String toString(){
		String tree=root.toString()+"\n";

		if( root instanceof LeafNode){

		}
		else //split node
		{
			for(int i=0;i<childTree.length;i++)
				tree +=childTree[i];
		}
		return tree;
	}

	/**
	 * Metodo per la serializzazione degli oggetti di classe RegressionTree
	 * @param nomeFile - il nome del file dove serializzare l'oggetto
	 *
	 * @throws IOException
	 */
	public void salva(String nomeFile) throws IOException {


		File file = new File("resources/dumps/" + nomeFile);

		file.getParentFile().getParentFile().mkdir(); //Creates the folder if it doesnt exist -- /resources
		file.getParentFile().mkdir(); //Creates the folder if it doesnt exist -- /dumps

		FileOutputStream outFile = new FileOutputStream(file);
		ObjectOutputStream outStream = new ObjectOutputStream(outFile);
		outStream.writeObject(this);
		outStream.close();
	}

	/**
	 * Metodo per la deserializzazione degli oggetti di classe RegressionTree
	 * @param nomeFile - il nome del file da cui deserializzare l'oggetto
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static RegressionTree carica(String nomeFile) throws IOException, ClassNotFoundException{
		FileInputStream inFile = new FileInputStream("resources/dumps/" + nomeFile);
		ObjectInputStream outStream = new ObjectInputStream(inFile);
		RegressionTree rTree = (RegressionTree) outStream.readObject();
		outStream.close();
		return rTree;
	}

}
