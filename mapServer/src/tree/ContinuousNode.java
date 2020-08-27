package tree;

import data.Attribute;
import data.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;



public class ContinuousNode extends SplitNode implements Serializable {

	/**
	 * Costruttore della classe.
	 * Ordina il subset degli esempi in base al valore dell'{attribute} indicato
	 * Quindi popola l'oggetto interno SplitInfo e calcola la varianza dello split
	 *
	 * @param trainingSet - il training set completo
	 * @param beginExampleIndex - indice di inizio del sottinsieme del training set da considerare
	 * @param endExampleIndex - indice di fine del sottinsieme del training set da considerare
	 * @param attribute - l'attributo continuo su cui il nodo effettua lo split
	 */
	ContinuousNode(Data trainingSet, int beginExampleIndex, int endExampleIndex, Attribute attribute) {
		super(trainingSet, beginExampleIndex, endExampleIndex, attribute);
	}

	/**
	 * Genera le informazioni sullo split.
	 * Determina lo split migliore in base ai valori continui presenti nel subset del dataset: genera tutti gli split possibili e sceglie quello con la varianza minore
	 *
	 * @param trainingSet - il training set completo
	 * @param beginExampleIndex - indice di inizio del sottinsieme del training set da considerare
	 * @param endExampleIndex - indice di fine del sottinsieme del training set da considerare
	 * @param attribute - l'attributo su cui il nodo effettua lo split
	 */
	void setSplitInfo(Data trainingSet,int beginExampleIndex, int endExampleIndex, Attribute attribute){
		//Update mapSplit defined in SplitNode -- contiene gli indici del partizionamento
		Double currentSplitValue= (Double)trainingSet.getExplanatoryValue(beginExampleIndex,attribute.getIndex());
		double bestInfoVariance=0;
		List <SplitInfo> bestMapSplit=null;

		for(int i=beginExampleIndex+1;i<=endExampleIndex;i++){
			Double value=(Double)trainingSet.getExplanatoryValue(i,attribute.getIndex());
			if(value.doubleValue()!=currentSplitValue.doubleValue()){
				//	System.out.print(currentSplitValue +" var ");
				double localVariance=new LeafNode(trainingSet, beginExampleIndex,i-1).getVariance();
				double candidateSplitVariance=localVariance;
				localVariance=new LeafNode(trainingSet, i,endExampleIndex).getVariance();
				candidateSplitVariance+=localVariance;
				//System.out.println(candidateSplitVariance);
				if(bestMapSplit==null){
					bestMapSplit=new ArrayList<SplitInfo>();
					bestMapSplit.add(new SplitInfo(currentSplitValue, beginExampleIndex, i-1,0,"<="));
					bestMapSplit.add(new SplitInfo(currentSplitValue, i, endExampleIndex,1,">"));
					bestInfoVariance=candidateSplitVariance;
				}
				else{

					if(candidateSplitVariance<bestInfoVariance){
						bestInfoVariance=candidateSplitVariance;
						bestMapSplit.set(0, new SplitInfo(currentSplitValue, beginExampleIndex, i-1,0,"<="));
						bestMapSplit.set(1, new SplitInfo(currentSplitValue, i, endExampleIndex,1,">"));
					}
				}
				currentSplitValue=value;
			}
		}

		if(bestMapSplit != null){
			mapSplit=bestMapSplit;
			//rimuovo split inutili (che includono tutti gli esempi nella stessa partizione)

			if(mapSplit.get(1).beginIndex == mapSplit.get(1).getEndIndex()){
				mapSplit.remove(1);
			}

		}
	}


	@Override
	int testCondition(Object value) {
		return 0;
	}

	@Override
	public String toString() {
		String v = "CONTINUOUS SPLIT : attribute=" + attribute.getName() + " " + super.toString();

		return v;
	}

}
