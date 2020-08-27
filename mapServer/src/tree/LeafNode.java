package tree;

import data.Data;

import java.io.Serializable;

/**
 * Classe specializzata da Node.
 * Rappresenta un nodo foglia (non genera alcuno split) e il suo valore predetto
 *
 * @author Gianfranco Demarco
 */
public class LeafNode extends Node implements Serializable {

	Double predictedClassValue;

	/**
	 * Invoca il costruttore della superclasse Nodo.
	 * Quindi calcola il valore predetto come media del valore target dei nodi compresi tra [beginExampleIndex, endExampleIndex]
	 *
	 * @param trainingSet - il training set completo
	 * @param beginExampleIndex - indice di inizio del sottinsieme del training set da considerare
	 * @param endExampleIndex - indice di fine del sottinsieme del training set da considerare
	 */
	public LeafNode(Data trainingSet, int beginExampleIndex, int endExampleIndex) {
		super(trainingSet, beginExampleIndex, endExampleIndex);

		//calculate class value
		double avg = 0.0;
		for(int i = beginExampleIndex; i <= endExampleIndex; i++) {
			avg+=trainingSet.getClassValue(i);
		}
		predictedClassValue = avg/(endExampleIndex - beginExampleIndex + 1);

	}

	@Override
	protected int getNumberOfChildren() {
		return 0;
	}

	@Override
	public String toString() {
		return "LEAF : class=" + predictedClassValue + "  " + super.toString();
	}

	public Double getPredictedClassValue() {
		return predictedClassValue;
	}

}
