package tree;

import data.Attribute;
import data.Data;
import data.DiscreteAttribute;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

/**
 * Classe che specializza SplitNode.
 * Rappresenta un nodo di Split generato su un attributo discreto
 *
 * @author Gianfranco Demarco
 */
public class DiscreteNode extends SplitNode implements Serializable {

	/**
	 * Costruttore di DiscreteNode. Invoca il costruttore della superclasse SplitNode
	 * @param trainingSet - il training set completo
	 * @param beginExampleIndex - indice di inizio del sottinsieme del training set da considerare
	 * @param endExampleIndex - indice di fine del sottinsieme del training set da considerare
	 * @param discreteAttribute - l'attributo discreto su cui il nodo effettua lo split
	 */
	public DiscreteNode(Data trainingSet, int beginExampleIndex, int endExampleIndex,
						DiscreteAttribute discreteAttribute) {
		super(trainingSet, beginExampleIndex, endExampleIndex, discreteAttribute);
	}

	/**
	 * Genera le informazioni sullo split.
	 * Per ogni valore discreto presente nel subset [beginExampleIndex, endExampleIndex] sull'attributo [attribute], crea l'oggetto
	 * SplitInfo contentente le informazioni generate dallo split
	 *
	 * @param trainingSet - il training set completo
	 * @param beginExampleIndex - indice di inizio del sottinsieme del training set da considerare
	 * @param endExampleIndex - indice di fine del sottinsieme del training set da considerare
	 * @param attribute - l'attributo su cui il nodo effettua lo split
	 */
	@Override
	void setSplitInfo(Data trainingSet, int beginExampleIndex, int endExampleIndex, Attribute attribute) {


		Iterator<String> attrValues = ((DiscreteAttribute) attribute).getValues().iterator();
		ArrayList<Object> presentValues = new ArrayList();

		////////////////////////////////////////////////////////////////////////
		//gets only the attribute values actually present in the dataset range
		for (int i = beginExampleIndex; i <= endExampleIndex; i++){
			if(!presentValues.contains(trainingSet.getExplanatoryValue(i, attribute.getIndex())))
				presentValues.add(trainingSet.getExplanatoryValue(i, attribute.getIndex()));
		}
		////////////////////////////////////////////////////////////////////////


		for (int i = 0; i < presentValues.size(); i++) {
			int begin = i == 0 ? beginExampleIndex : mapSplit.get(i - 1).getEndIndex() + 1;
			int end = begin;

			while(end < endExampleIndex && trainingSet.getExplanatoryValue(end + 1, attribute.getIndex()).equals(presentValues.get(i)))
				end++;

			//mapSplit[i] = new SplitInfo(attrValues[i], begin, end, presentValues.size());
			mapSplit.add(new SplitInfo(attrValues.next(), begin, end, i));
		}

	}

	@Override
	int testCondition(Object value) {

		int index = -1;

		for (int i = 0; i < mapSplit.size(); i++) {
			if (value.equals(mapSplit.get(i).getSplitValue()))
				index = i;
		}

		return index;
	}

	@Override
	public String toString() {
		String v = "DISCRETE SPLIT : \n\tattribute=" + attribute.getName() + " " + super.toString();

		return v;
	}

}
