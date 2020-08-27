package tree;

import data.Attribute;
import data.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe che modella un nodo di decisione (split)
 * Tiene traccia del sottoinsieme di esempi del dataset che comprende
 *
 *
 * @author Gianfranco Demarco
 */
public abstract class SplitNode extends Node implements Comparable<SplitNode>, Serializable {
	// Classe che colelzione informazioni descrittive dello split
	class SplitInfo implements Serializable {
		Object splitValue;
		int beginIndex;
		int endIndex;
		int numberChild;
		String comparator="=";
		protected SplitInfo(Object splitValue, int beginIndex, int endIndex, int numberChild){
			this.splitValue=splitValue;
			this.beginIndex=beginIndex;
			this.endIndex=endIndex;
			this.numberChild=numberChild;
		}

		/**
		 * Inner class della classe SplitNode.
		 * Contiene le informazioni che caratterizzano il nodo di Split
		 *
		 * @param splitValue
		 * @param beginIndex
		 * @param endIndex
		 * @param numberChild
		 * @param comparator
		 */
		SplitInfo(Object splitValue, int beginIndex, int endIndex, int numberChild, String comparator){
			this.splitValue=splitValue;
			this.beginIndex=beginIndex;
			this.endIndex=endIndex;
			this.numberChild=numberChild;
			this.comparator=comparator;
		}
		int getBeginindex(){
			return beginIndex;
		}
		int getEndIndex(){
			return endIndex;
		}
		Object getSplitValue(){
			return splitValue;
		}
		public String toString(){
			return "child " + numberChild +" split value"+comparator+splitValue + "[Examples:"+beginIndex+"-"+endIndex+"]";
		}
		String getComparator(){
			return comparator;
		}


	}

	Attribute attribute;

	List<SplitInfo> mapSplit = new ArrayList<>();

	double splitVariance;

	/**
	 * Costruttore della classe.
	 * Ordina il subset degli esempi in base al valore dell'{attribute} indicato
	 * Quindi popola l'oggetto interno SplitInfo e calcola la varianza dello split
	 *
	 * @param trainingSet
	 * @param beginExampleIndex
	 * @param endExampleIndex
	 * @param attribute
	 */
	SplitNode(Data trainingSet, int beginExampleIndex, int endExampleIndex, Attribute attribute){
		super(trainingSet, beginExampleIndex,endExampleIndex);
		this.attribute=attribute;
		trainingSet.sort(attribute, beginExampleIndex, endExampleIndex); // order by attribute
		setSplitInfo(trainingSet, beginExampleIndex, endExampleIndex, attribute);

		//compute variance
		splitVariance=0;

		for(int i=0; i < mapSplit.size(); i++){
			double localVariance = new LeafNode(trainingSet, mapSplit.get(i).getBeginindex(),mapSplit.get(i).getEndIndex()).getVariance();
			splitVariance+=(localVariance);
		}
	}

	abstract void setSplitInfo(Data trainingSet,int beginExampelIndex, int endExampleIndex, Attribute attribute);

	abstract int testCondition (Object value);

	protected Attribute getAttribute(){
		return attribute;
	}

	public double getVariance(){
		return splitVariance;
	}

	protected int getNumberOfChildren(){
		return mapSplit.size();
	}

	SplitInfo getSplitInfo(int child){
		return mapSplit.get(child);
	}

	/**
	 * Mostra gli split generati dal nodo
	 */
	protected String formulateQuery(){
		String query = "";
		for(int i=0;i<mapSplit.size();i++)
			query+= (i + ":" + attribute.getName() + mapSplit.get(i).getComparator() + mapSplit.get(i).getSplitValue())+"\n";
		return query;
	}

	public String toString(){
		String v= super.toString() + "\nSplit Variance: " + getVariance()+ "\n" ;

		for(int i=0;i<mapSplit.size();i++){
			v+= "\t"+mapSplit.get(i)+"\n";
		}

		return v;
	}

	@Override
	public int compareTo(SplitNode o) {
		if(this.getVariance() < o.getVariance()){
			return -1;
		}else if(this.getVariance() > o.getVariance()){
			return 1;
		}else{
			return 0;
		}
	}
}
