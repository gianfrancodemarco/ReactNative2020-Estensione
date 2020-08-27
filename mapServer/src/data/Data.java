package data;

import database.*;

import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 1. Valorizza l'array explanatorySet[] con gli attributi letti dal dataset.
 * Ogni attributo discreto è creato associando ad esso l'array dei valori discreti (@desc) che esso può
 * assumere.
 * 2. Valorizza classAttribute istanziando un oggetto di tipo ContinuousAttribute.
 * 3. Valorizza il numero di esempi (letto dal tag @data).
 * 4. Popola la matrice data [][] con gli esempi di training
 */
public class Data {

	/**
	 * Matrice nXm di tipo Object che contiene il training set. Il training set è
	 * organizzato come (numero di esempi) X (numero di attributi)
	 */
	private List<Example> data = new ArrayList<>();
	private int numberOfExamples;

	/**
	 * Array di oggetti di tipo Attribute per rappresentare gli attributi
	 * indipendenti
	 */
	private List<Attribute> explanatorySet = new ArrayList<Attribute>();

	/**
	 * Oggetto per modellare l'attributo di classe (target
	 * attribute). L'attributo di classe è un attributo numerico.
	 */
	private ContinuousAttribute classAttribute;

	/**
	 * Costruttore della classe Data.
	 * Recupera il training set dalla tabella di nome {tableName} dal database e valorizza le variabili di istanza
	 *
	 * @param tableName - nome della tabella da cui recuperare i dati
	 * @throws TrainingDataException
	 * @throws SQLException
	 */
	public Data(String tableName) throws TrainingDataException {
		DbAccess dbAccess = null;
		try {
			dbAccess = new DbAccess();
		} catch (DatabaseConnectionException e) {
			throw new TrainingDataException(e.getMessage()+ ": could not connect to Database");
		}

		TableSchema tableSchema = null;
		try {
			tableSchema = new TableSchema(dbAccess, tableName);
		} catch (SQLException throwables) {
			throw new TrainingDataException(throwables.getMessage() + ": could not find table " + tableName);
		}

		TableData tableData = new TableData(dbAccess);
		Iterator<Example> transazioni = null;
		try {
			transazioni = tableData.getTransazioni(tableName).iterator();
		} catch (SQLException | EmptySetException e) {
			throw new TrainingDataException("No training data found!");
		}

		int i = 0;
		while (transazioni.hasNext()){
			data.add(transazioni.next());
			i++;
		}

		numberOfExamples = i;

		Iterator<Column> columns = tableSchema.iterator();
		Column column = null;
		i = 0;

		while(columns.hasNext()){
			column = columns.next();
			if(column.isNumber())
				explanatorySet.add(new ContinuousAttribute(column.getColumnName(), i));
			else{
				Set<String> discreteValues = null;
				try {
					discreteValues = tableData.getDistinctColumnValues(tableName, column).stream().map(o -> o.toString()).collect(Collectors.toSet());
				} catch (SQLException | EmptySetException e) {
					throw new TrainingDataException(e.getMessage());
				}
				explanatorySet.add(new DiscreteAttribute(column.getColumnName(), i, discreteValues));
			}
			i++;
		}

		if(i < 2) throw new TrainingDataException("Too few columns!");

		explanatorySet.remove(explanatorySet.size() - 1);
		classAttribute = new ContinuousAttribute(column.getColumnName(), explanatorySet.size());
	}


	int getNumberOfExplanatoryAttributes() {
		return explanatorySet.size();
	}

	public int getNumberOfExamples() {
		return numberOfExamples;
	}

	public double getClassValue(int exampleIndex) {
		return (double) data.get(exampleIndex).get(explanatorySet.size());
	}

	public Object getExplanatoryValue(int exampleIndex, int attributeIndex) {
		return data.get(exampleIndex).get(attributeIndex);
	}

	public Attribute getExplanatoryAttribute(int index) {
		return explanatorySet.get(index);
	}

	ContinuousAttribute getClassAttribute() {
		return classAttribute;
	}

	public String toString() {
		String value = "";
		for (int i = 0; i < numberOfExamples; i++) {
			for (int j = 0; j < explanatorySet.size(); j++)
				value += data.get(i).get(j) + ",";

			value += data.get(i).get(explanatorySet.size()) + "\n";
		}
		return value;

	}

	/**
	 *  Ordina il sottoinsieme di esempi compresi nell'intervallo [inf,sup] in data[][] rispetto
	 * allo specifico attributo attribute. Usa l'Algoritmo quicksort per l'ordinamento di un array di interi usando
	 * come relazione d'ordine totale "minore uguale". L'array, in questo caso, è dato dai valori assunti dall'attributo passato in
	 * input. Vengono richiamati i metodi: private void quicksort(Attribute attribute, int inf,
	 * int sup); private int partition(DiscreteAttribute attribute, int inf, int sup)
	 *  e private void swap(int i,int j)
	 *
	 * @param attribute
	 * @param beginExampleIndex - indice di inizio del sottinsieme del training set da considerare
	 * @param endExampleIndex - indice di fine del sottinsieme del training set da considerare
	 */
	public void sort(Attribute attribute, int beginExampleIndex, int endExampleIndex) {
		quicksort(attribute, beginExampleIndex, endExampleIndex);
	}

	// scambio esempio i con esempi oj
	private void swap(int i,int j){
		Object temp1 = data.get(i);
		Object temp2 = data.get(j);
		data.remove(i);
		data.add(i, (Example) temp2);
		data.remove(j);
		data.add(j, (Example) temp1);
	}


	/*
	 * Partiziona il vettore rispetto all'elemento x e restiutisce il punto di
	 * separazione
	 */
	private  int partition(DiscreteAttribute attribute, int inf, int sup){
		int i,j;

		i=inf;
		j=sup;
		int	med=(inf+sup)/2;
		String x=(String)getExplanatoryValue(med, attribute.getIndex());
		swap(inf,med);

		while (true)
		{

			while(i<=sup && ((String)getExplanatoryValue(i, attribute.getIndex())).compareTo(x)<=0){
				i++;

			}

			while(((String)getExplanatoryValue(j, attribute.getIndex())).compareTo(x)>0) {
				j--;

			}

			if(i<j) {
				swap(i,j);
			}
			else break;
		}
		swap(inf,j);
		return j;

	}



	/*
	 * Partiziona il vettore rispetto all'elemento x e restiutisce il punto di separazione
	 */
	private  int partition(ContinuousAttribute attribute, int inf, int sup){
		int i,j;

		i=inf;
		j=sup;
		int	med=(inf+sup)/2;
		Double x=(Double)getExplanatoryValue(med, attribute.getIndex());
		swap(inf,med);

		while (true)
		{

			while(i<=sup && ((Double)getExplanatoryValue(i, attribute.getIndex())).compareTo(x)<=0){
				i++;

			}

			while(((Double)getExplanatoryValue(j, attribute.getIndex())).compareTo(x)>0) {
				j--;

			}

			if(i<j) {
				swap(i,j);
			}
			else break;
		}
		swap(inf,j);
		return j;

	}




	/**
	 * Algoritmo quicksort per l'ordinamento di un array di interi A
	 * usando come relazione d'ordine totale "<="
	 * @param A
	 */
	private void quicksort(Attribute attribute, int inf, int sup){

		if(sup>=inf){

			int pos;
			if(attribute instanceof DiscreteAttribute)
				pos=partition((DiscreteAttribute)attribute, inf, sup);
			else
				pos=partition((ContinuousAttribute)attribute, inf, sup);

			if ((pos-inf) < (sup-pos+1)) {
				quicksort(attribute, inf, pos-1);
				quicksort(attribute, pos+1,sup);
			}
			else
			{
				quicksort(attribute, pos+1, sup);
				quicksort(attribute, inf, pos-1);
			}


		}

	}


	public List<Attribute> getExplanatoryAttributes() {
		return explanatorySet;
	}





}
