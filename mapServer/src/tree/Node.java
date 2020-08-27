package tree;

import data.Data;

import java.io.Serializable;

/**
 * Classe che modella un generico nodo dell'albero di regressione
 * Contiene un progressivo statico ({idNodeCount}) che tiene traccia dei nodi generati
 * Fa riferimento al sottoinsieme del training set individuato da [beginExampleIndex, endExampleIndex] e la varianza dei valori target calcolata su questo intervallo
 *
 * @author Gianfranco Demarco
 */
public abstract class Node implements Serializable {

	static int idNodeCount = 0;

	private int idNode;
	private int beginExampleIndex;
	private int endExampleIndex;
	private double variance;

	/**
	 * Costruttore della classe.
	 * Valorizza gli attributi di istanza, calcolando anche la varianza.
	 *
	 *
	 * @param trainingSet - il training set completo
	 * @param beginExampleIndex - indice di inizio del sottinsieme del training set da considerare
	 * @param endExampleIndex - indice di fine del sottinsieme del training set da considerare
	 */
	public Node(Data trainingSet, int beginExampleIndex, int endExampleIndex){
		idNode = idNodeCount;
		idNodeCount++;
		this.beginExampleIndex = beginExampleIndex;
		this.endExampleIndex = endExampleIndex;
		//variance = Utils.variance(trainingSet.getTargetValues(), beginExampleIndex, endExampleIndex);

		////////////////////////////////////////////////////////////////////////////////////
		//CALCULATE SSE: Errore quadratico medios
		double sumOfSquares = 0.0;
		double squareOfSums = 0.0;
		double tempVariance = 0.0;

		for (int i = beginExampleIndex; i <= endExampleIndex; i++) {
			sumOfSquares += trainingSet.getClassValue(i) * trainingSet.getClassValue(i);
			squareOfSums += trainingSet.getClassValue(i);
		}

		squareOfSums *= squareOfSums;

		tempVariance = sumOfSquares - (squareOfSums/(endExampleIndex - beginExampleIndex + 1));

		variance = tempVariance;

		////////////////////////////////////////////////////////////////////////////////////

	}

	/**
	 *@return numero di figli del nodo (0 se foglia, maggiore di 1 se nodo di split)
	 */
	abstract int getNumberOfChildren();


	public double getVariance() {
		return variance;
	}

	public String toString() {
		return "\t[Examples:" + this.beginExampleIndex + "-" + this.endExampleIndex + "] \n\tVariance: " + this.variance;

	}

}
